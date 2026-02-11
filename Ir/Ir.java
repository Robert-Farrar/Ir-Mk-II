import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

public class Ir implements Runnable{
    File f;
    Matrix m;
    int k;
    ConcurrentHashMap<String, Integer> vocab;

    Ir(File f, Matrix m, int k){
        vocab = new ConcurrentHashMap<String, Integer>();
        this.f = f;
        this.m = m;
        this.k = k;
    }


    public void run(){
        if(f != null){
            scanDoc();
        }
    }


    private void scanDoc(){
        try{
            BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()));

            String s = "";

            while((s = br.readLine()) != null){
                String[] tokens = s.split("\\s+");
                
                for (int i = 0; i < tokens.length; i++) {
                    tokens[i] = tokens[i].toLowerCase();
                }

                parse(tokens);
                addToVocab(tokens);
            }

            termMatrixProp(vocab);

            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    

    private void parse(String[] w){
        for(int i = 0; i < w.length; i++){
            // int j = i - 1;
            // int count = 0;
            // while(j >= 0 && count < k){
            //     m.putInTcm(w, j, i);
            //     j --;
            //     count++;
            // }

            int  j = i + 1; 
            int count = 0;

            while(j < w.length && count < k){
                m.putInTcm(w, i, j);
                j++;
                count++;
            }
        }
    }

    public void addToVocab(String[] s){
        for(String str : s){
            if(!vocab.containsKey(str)){
                vocab.put(str, 1);
            }
        }
    }


    public void termMatrixProp(ConcurrentHashMap<String, Integer> vocab){
        try{

            for(String s : m.tcm.keySet()){
                for(String str : vocab.keySet()){
                    if(!m.tcm.get(s).containsKey(str)){
                        m.tcm.get(s).put(str, 0);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }
    }

    public static void main(String args[]){
        try{
            File file = null;
        if(args.length > 0){
            file = new File(args[0]);
            
        }
        File[] files = file.listFiles();

        ArrayList<Thread> l = new ArrayList<Thread>();
        Matrix m = new Matrix();
        m.tcm = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
        Semaphore sem = new Semaphore(5);

        for(File f : files){

            sem.acquire();
            Ir i = new Ir(f, m, 1);
            Thread t = new Thread(i);

            t.start();

            sem.release();
        }

        for(Thread t : l){
            t.join();
        }


        Scanner sc = new Scanner(System.in);

        System.out.println("Please enter a word.");

        String w1= sc.nextLine();
        w1 = w1.toLowerCase();
        PriorityQueue<WordPair> q = new PriorityQueue<WordPair>(Collections.reverseOrder());

        if(m.tcm.containsKey(w1)){

            int total = getTotal(m);

            for(String s : m.tcm.keySet()){
                WordPair w = new WordPair(0.0, w1, s);
                w.pmi = getPMI(w1, s, m, total);

                q.add(w);
            }
        }

        for(int i = 0; i < 5; i++){
            WordPair w = q.poll();
            if(w != null){
            System.out.printf("PMI(%s, %s) = %.2f\n",w.word1, w.word2, w.pmi);
            }
        }


        sc.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static int getTotal(Matrix m){
        int total = 0;

        for(String s : m.tcm.keySet()){
            for(String str : m.tcm.get(s).keySet()){
                total += m.tcm.get(s).get(str);
            }
        }

        return total;
    }

    public static void printTCM(Matrix m){
        for(String s : m.tcm.keySet()){
            System.out.println("Word: " + s);
            for(String str : m.tcm.get(s).keySet()){
                System.out.printf("Term : %s\t", str);
            }
            System.out.println();
        }
    }

    public static double getPMI(String w1, String w2, Matrix m, int total){

        double n = ((double)m.tcm.get(w1).get(w2) + 1e-10) * total;

        double d = 0;
        int word1 = 0;
        int word2 = 0;

        for(String s : m.tcm.get(w1).keySet()){
                word1 += m.tcm.get(w1).get(s);
        }

        for(String s : m.tcm.keySet()){
            if(m.tcm.get(s).containsKey(w2)){
                word2 += m.tcm.get(s).get(w2);
            }
        }

        d = ((double)word1 * word2);

        double pmi = 0.0;
        
        if(d != 0){
            pmi = (Math.log((n / d)) / Math.log(2));
        }

        return pmi;
    }
}