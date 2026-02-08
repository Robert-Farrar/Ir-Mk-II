import java.util.concurrent.ConcurrentHashMap;

public class Matrix {
    public ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> tcm;
    public ConcurrentHashMap<String, Integer> wordCounts = new ConcurrentHashMap<String, Integer>();
    public double totalCount = 0;

    public void putInTcm(String[] w, int a, int b) {

        // gets thhe occurance of the target word for (P(x))
         wordCounts.put(w[a], wordCounts.getOrDefault(w[a], 0) + 1);

        // increments the total global events
        totalCount++;

        // makes sure our inner map exists for the word (its not bird lmfao)
        tcm.putIfAbsent(w[a], new ConcurrentHashMap<String, Integer>());

        // updates the co-occurance count for the word pair (P(x,y))
        ConcurrentHashMap<String, Integer> innerMap = tcm.get(w[a]);
        innerMap.put(w[b], innerMap.getOrDefault(w[b], 0) + 1);
        /* 
        // needed this for individual word counts for PMI calculation.
        wordCounts.put(w[a], wordCounts.getOrDefault(w[a], 0) + 1);
        totalCount++;

        if (!tcm.containsKey(w[a])) {
            tcm.put(w[a], new ConcurrentHashMap<String, Integer>());
        }

        if (!tcm.containsKey(w[a])) {
            tcm.put(w[a], new ConcurrentHashMap<String, Integer>());
        }

        if (!tcm.get(w[a]).containsKey(w[b])) {
            tcm.get(w[a]).put(w[b], 1);
        } else {
            tcm.get(w[a]).put(w[b], tcm.get(w[a]).get(w[b]) + 1);
        }

            */
    }
}
