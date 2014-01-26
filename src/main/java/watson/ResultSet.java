package watson;

import java.util.Objects;

import org.json.simple.JSONObject;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class ResultSet implements Comparable<ResultSet> {

    private String title;
    private double score;
    private boolean correct;
    private int rank;
    
    // These are intended to be overridden in subclasses
	double best_score = 0;
	double worst_score = 1;

    public ResultSet(String title, double score, boolean correct, int rank) {
        this.title = title;
        this.score = score;
        this.correct = correct;
        this.rank = rank;
    }
	
	public ResultSet(String engine_name, JSONObject attr) {
		// There is a bit of redundancy: the engine name is in every attribute
        title = (String) attr.get(String.format("%s_title", engine_name));
        rank = (int) attr.get(String.format("%s_rank", engine_name));
        score = (double) attr.get(String.format("%s_score", engine_name));
        correct = ((String) attr.get(String.format("%s_score", engine_name))).equalsIgnoreCase("yes");
	}
    
    /** Copy constructor */
    public ResultSet(ResultSet resultset) {
    	title = resultset.title;
    	score = resultset.score;
    	correct = resultset.correct;
    	rank = resultset.rank;
    }

    public ResultSet() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getScore() {
        return score;
    }


	/** Normalize scores to be from 0 to 1, less is better. */
	public void setScore(double raw) {
		score = (raw - best_score) / (worst_score - best_score);
	}

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.title);
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
        final ResultSet other = (ResultSet) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ResultSet{" + "title=" + title + ", score=" + score + ", correct=" + correct + ", rank=" + rank + '}';
    }
    
    @Override
	public int compareTo(ResultSet other) {
		return new Double(score).compareTo(other.getScore());
	}

}
