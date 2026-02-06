import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.util.ArrayList;

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

                parse(tokens);
                addToVocab(tokens);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void parse(String[] w){
        for(int i = 0; i < w.length; i++){
            int j = i - 1;
            int count = 0;
            while(j >= 0 && count < k){
                m.putInTcm(w, j, i);
                j --;
                count++;
            }

            j = i + 1; 
            count = 0;

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
                    if(!m.tcm.get(str).containsKey(s)){
                        m.tcm.get(str).put(s, 0);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            Thread.currentThread().interrupt();
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
        for(File f : files){
            Ir i = new Ir(f, m, 2);

            Thread t = new Thread(i);

            t.start();
        }

        for(Thread t : l){
            t.join();
        }

        Scanner sc = new Scanner(System.in);

        System.out.println("Please enter a word. ");

        String w1= sc.next();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static double getPMI(String w1, String w2, Matrix m){

        int n = m.tcm.get(w1).get(w2);

        int d;

        return 0.0;
    }
}