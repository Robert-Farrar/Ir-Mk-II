import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

public class Ir implements Runnable {
    File f;
    Matrix m;
    int k;
    ConcurrentHashMap<String, Integer> vocab;

    Ir(File f, Matrix m, int k) {
        vocab = new ConcurrentHashMap<String, Integer>();
        this.f = f;
        this.m = m;
        this.k = k;
    }

    public void run() {
        if (f != null) {
            scanDoc();
        }
    }

    private void scanDoc() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()));

            String s = "";

            while ((s = br.readLine()) != null) {
                String[] tokens = s.split("\s+");

                parse(tokens);
                addToVocab(tokens);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parse(String[] w) {
        // bidirectional co-occurence matrix with window size k
        for (int i = 0; i < w.length; i++) {
            // looks ahead k words
            for (int step = 1; step <= k; step++) {
                int j = step + i;
                // stays withing our bounds
                if (j < w.length) {
                    // records A -> B occurances
                    m.putInTcm(w, i, j);
                    // records B -> A occurances
                    m.putInTcm(w, j, i);
                }
            }
        }

        /*
         * for (int i = 0; i < w.length; i++) {
         * int j = i - 1;
         * int count = 0;
         * while (j >= 0 && count < k) {
         * m.putInTcm(w, j, i);
         * j--;
         * count++;
         * }
         * 
         * j = i + 1;
         * count = 0;
         * 
         * while (j < w.length && count < k) {
         * m.putInTcm(w, i, j);
         * j++;
         * count++;
         * }
         * }
         */
    }

    public void addToVocab(String[] s) {
        for (String str : s) {
            if (!vocab.containsKey(str)) {
                vocab.put(str, 1);
            }
        }
    }

    public void termMatrixProp(ConcurrentHashMap<String, Integer> vocab) {
        try {
            for (String s : m.tcm.keySet()) {
                for (String str : vocab.keySet()) {
                    if (!m.tcm.get(str).containsKey(s)) {
                        m.tcm.get(str).put(s, 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return;
        }
    }

    public static void main(String args[]) {
        try {
            File file = null;
            if (args.length > 0) {
                file = new File(args[0]);
            }
            File[] files = file.listFiles();
            ArrayList<Thread> l = new ArrayList<Thread>();
            Matrix m = new Matrix();
            m.tcm = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
            for (File f : files) {
                Ir i = new Ir(f, m, 2);

                Thread t = new Thread(i);

                t.start();
            }

            for (Thread t : l) {
                t.join();
            }

            Scanner sc = new Scanner(System.in);

            System.out.println("Please enter a word. ");

            String w1 = sc.next();

            getPMITop5(w1, m);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getPMITop5(String w1, Matrix m) {
        // check to see if word exists in the data
        if (!m.tcm.containsKey(w1)) {
            System.out.println("word not found");
            return;
        }

        // min-heap of size 5 to store the top 5 PMI values since you aka robert are a
        // genious and know that this is faster.
        PriorityQueue<PmiPair> top5 = new PriorityQueue<>(Comparator.comparingDouble(p -> p.score));

        // gets the map of the neighboring words for the target word
        ConcurrentHashMap<String, Integer> coOccur = m.tcm.get(w1);
        double pWord = (double) m.wordCounts.get(w1) / m.totalCount;

        coOccur.forEach((wordY, count) -> {
            double pWordY = (double) m.wordCounts.get(wordY) / m.totalCount;
            double pCoOccur = (double) count / m.totalCount;
            // i hate math
            double pmi = Math.log(pCoOccur / (pWord * pWordY)) / Math.log(2);

            PmiPair pair = new PmiPair(w1, wordY, pmi);

            // Maintains only the top 5 PMI pairs in the min-heap
            if (top5.size() < 5) {
                top5.add(pair);
            } else if (pmi > top5.peek().score) {
                top5.poll();
                top5.add(pair);
            }

            System.out.printf("\nTop 5 associated words with %s:\n", w1);
            if (top5.isEmpty()) {
                System.out.println("No associated words found.");
            }
            while(!top5.isEmpty()) {
                PmiPair p = top5.poll();
                System.out.printf("PMI(%s, %s) = %.2f%n", p.w1, p.w2, p.score);
            }
        });

        /*
         * if (!m.tcm.containsKey(w1)) {
         * System.out.println("word not found");
         * return;
         * }
         * 
         * double pWord = (double) m.wordCounts.get(w1) / m.totalCount;
         * ConcurrentHashMap<String, Integer> coOccur = m.tcm.get(w1);
         * 
         * coOccur.forEach((wordY, count) -> {
         * double pWordY = (double) m.wordCounts.get(wordY) / m.totalCount;
         * double pCoOccur = (double) count / m.totalCount;
         * // i hate math
         * double pmi = Math.log(pCoOccur / (pWord * pWordY)) / Math.log(2);
         * 
         * // formated output to 2 decimal places
         * System.out.printf("PMI(%s, %s) = %.2f%n", w1, wordY, pmi);
         * });
         */
    }
}