import java.util.concurrent.ConcurrentHashMap;

public class Matrix {
    public ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> tcm;


public void putInTcm(String[] w, int a, int b){
        if(!tcm.containsKey(w[a])){
            tcm.put(w[a], new ConcurrentHashMap<String, Integer>());
        }

        if(!tcm.get(w[a]).containsKey(w[b])){
                        tcm.get(w[a]).put(w[b], 1);
                    }else{
                        tcm.get(w[a]).put(w[b], tcm.get(w[a]).get(w[b]) + 1);
                    }
    }
}
