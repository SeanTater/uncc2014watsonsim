
package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.simple.JSONObject;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class Answer implements Comparable<Answer> {
    public Map<String, Double> scores = new HashMap<>();
    public List<Passage> passages = new ArrayList<>();
    public String candidate_text;

    /**
     * Create an Answer with one implicitly defined Passage
     */
    public Answer(Passage d) {
        this.passages.add(d);
        this.scores = d.scores;
        this.candidate_text = d.title;
    }
    
    /**
     * Create an Answer with one implicitly defined Passage
     */
    public Answer(String engine, String title, String full_text, String reference) {
    	this(new Passage(engine, title, full_text, reference));
    }
    
    /**
     * Create an Answer without any passages
     */
    public Answer(String candidate_text) {
    	this.candidate_text = candidate_text;
    }
    
    /** Create an Answer (with engine) from JSON */
	public Answer(String engine, JSONObject attr) {
		this(
			engine,
			(String) attr.get(engine+"_title"),
			"",
			"");
		passages.get(0).score(engine+"_RANK", (double) attr.get(engine+"_rank"));
		passages.get(0).score(engine+"_SCORE", (double) attr.get(engine+"_score"));
	}

    @Override
    /** How to handle inexact matches this way?? */
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.candidate_text);
        return hash;
    }

    /** Does `this` match `other`, where `this` is the candidate answer
     *  and `other` is the reference. **Not Transitive or Commutative**!
     */
    public boolean matches(Answer other) {
        if (other == null) {
            return false;
        }
        
        return StringUtils.match_subset(other.candidate_text, candidate_text);
        
        /* The old method: any two passages match 
        for (Passage doc1 : this.passages) {
        	String t1 = StringUtils.filterRelevant(doc1.title);
        	for (Passage doc2 : other.passages) {
        		String t2 = StringUtils.filterRelevant(doc2.title);
        		if (StringUtils.match_subset(t1, t2)) return true;
        	}
    	}*/
    }

    @Override
    public String toString() {
    	// Make a short view of the engines as single-letter abbreviations
    	String engines = "";
    	for (Passage e: this.passages)
    		if (e.engine_name != null)
    			engines += e.engine_name.substring(0, 1);
    	
    	// ResultSet don't know if they are correct anymore..
    	//String correct = isCorrect() ? "✓" : "✗";
    	
    	// Should look like: [0.9998 gil] Flying Waterbuffalos ... 
    	return String.format("[%01f %-3s] %s", score(), engines, candidate_text);
    }
    
    public String toJSON() {
    	return String.format("{\"score\": %01f, \"title\": \"%s\"}", score(), candidate_text.replace("\"", "\\\""));
    }
    
    /**
     * Return the combined score for the answer, or null
     * */
    public Double score() {
        return scores.get("COMBINED");
    }

    /**
     * Assign a score to this answer. If you want to automatically generate
     * models to go with this score, remember to call Score.registerAnswerScore
     * @param name		The name of the score 
     * @param score		Double value of score (or NaN)
     */
	public void score(String name, double score) {
		scores.put(name, score);
	}
    
    /** Convenience method for returning all of the answer's scores as a primitive double[].
     * Intended for Weka, but it could be useful for any ML. */
    public double[] scoresArray(List<String> answerScoreNames) {
    	double[] out = new double[answerScoreNames.size()];
    	Arrays.fill(out, Double.NaN);
    	
    	// Answer scores
    	for (int dim_i=0; dim_i < answerScoreNames.size(); dim_i++){
			Double value = scores.get(answerScoreNames.get(dim_i));
    		out[dim_i] = value == null ? Double.NaN : value;
    	}
		return out;
    }
    
    @Override
	public int compareTo(Answer other) {
    	if (score() == null || other.score() == null)
    		// Comparing a resultset without a combined engine is undefined
    		return 0;
    	return score().compareTo(other.score());
	}
    
    /** Change this Answer to include all the information of another
     * TODO: What should we do to merge scores? */
    public void merge(Answer other) {
    	passages.addAll(other.passages);
    	// Merge the scores
    	for (Map.Entry<String, Double> sc : other.scores.entrySet()) {
    		if (!scores.containsKey(sc.getKey())) {
    			scores.put(sc.getKey(), sc.getValue());
    		}
    	}
    }
    

}
