package watson;
import java.util.*;

/** Holds a series of results from one search engine. */
public class Resultset extends ArrayList<Result> {
	private static final long serialVersionUID = 1L;
	public String engine;
	
	public Resultset(String engine) {
		this.engine = engine;
	}
}

/** Holds one search result, associated with one document.
 * 
 * Note the meaning of score and max_score.
 * Lower scores are better (and can be negative).
 * worst_score is the worst possible score for this result (which may not have existed in the set)
 * Scores are later scaled according to ( 
 * 
 * */ 
class Result {
	// Inherent
	public String docid;
	public String text;
	public String title;
	// Calculated
	private double score;
	// Dependent on engine
	// You can set this via inheritance if you prefer.
	public double best_score = 0; 
	public double worst_score = 1;
	
	/** Convenience constructor */
	public Result(String docid, String title, String text, double score) {
		this.docid = docid;
		this.title = title;
		this.text = text;
		this.score = score;
	}
	
	/** Copy constructor */
	public Result(Result result) {
		this.docid = result.docid;
		this.title = result.title;
		this.text = result.text;
		this.score = result.score;
		this.best_score = result.best_score;
		this.worst_score = result.worst_score;
	}
	
	/** Return normalized score.
	 * This may not be same number you put into setScore.
	 */
	public double getScore() {
		return score;
	}
	
	/** Normalize scores to be from 0 to 1, less is better. */
	public Result setScore(double raw) {
		score = raw / (worst_score - best_score);
		return this;
	}
}
