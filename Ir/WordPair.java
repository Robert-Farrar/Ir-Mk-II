public class WordPair implements Comparable<WordPair> {
    public double pmi;
    public String word1;
    public String word2;


    public WordPair(double pmi, String word1, String word2){
        this.pmi = pmi;
        this.word1 = word1;
        this.word2 = word2;
    }

    public int compareTo(WordPair w){
        return Double.compare(w.pmi, this.pmi);
    }

}
