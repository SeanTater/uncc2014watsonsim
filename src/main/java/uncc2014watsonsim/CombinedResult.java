package uncc2014watsonsim;

import java.util.Objects;

/**
 *
 * @author Phani Rahul
 */
public class CombinedResult {

    private boolean correct;
    private String title;
    private double indriScore;
    private double luceneScore;
    private long indriRank;
    private long luceneRank;

    public CombinedResult(boolean correct, String title, double indriScore, double luceneScore, long indriRank, long luceneRank) {
        this.correct = correct;
        this.title = title;
        this.indriScore = indriScore;
        this.luceneScore = luceneScore;
        this.indriRank = indriRank;
        this.luceneRank = luceneRank;
    }

   
    public CombinedResult() {
    }

    public long getIndriRank() {
        return indriRank;
    }

    public void setIndriRank(int indriRank) {
        this.indriRank = indriRank;
    }

    public long getLuceneRank() {
        return luceneRank;
    }

    public void setLuceneRank(int luceneRank) {
        this.luceneRank = luceneRank;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getIndriScore() {
        return indriScore;
    }

    public void setIndriScore(double indriScore) {
        this.indriScore = indriScore;
    }

    public double getLuceneScore() {
        return luceneScore;
    }

    public void setLuceneScore(double luceneScore) {
        this.luceneScore = luceneScore;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.title);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CombinedResult other = (CombinedResult) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CombinedResult{" + "correct=" + correct + ", title=" + title + ", indriScore=" + indriScore + ", luceneScore=" + luceneScore + ", indriRank=" + indriRank + ", luceneRank=" + luceneRank + '}';
    }
    
    
}
