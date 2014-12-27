
package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
    
    public Answer(List<Passage> passages,
    		Map<String, Double> scores,
    		String candidate_text) {
    	this.passages = passages;
    	this.scores = scores;
    	this.candidate_text = candidate_text;
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

    /** 
     * Return a threshold on distance
     */
    public boolean matches(Answer other) {
        return distance(other) <= 2;
    }
    
    /** 
     * Return a Levenshtein distance between answers after stopword removal.
     */
    public double distance(Answer other) {
        if (other == null) {
            return 10;
        }
        int dist = StringUtils.getLevenshteinDistance(
        		StringUtils.filterRelevant(candidate_text),
        		StringUtils.filterRelevant(other.candidate_text),
        		10);
        
        return (dist == -1) ? 10 : dist;
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
     * HACK: We average the scores but we should probably use a
     * pluggable binary operator*/
    public static Answer merge(List<Answer> others) {
    	Map<String, Double> scores = new HashMap<>();
        List<Passage> passages = new ArrayList<>();
        String candidate_text;
        
        // Merge all the passages
    	for (Answer other : others)
    		passages.addAll(other.passages);
    	
    	// Merge the scores
    	Set<String> all_score_names = new HashSet<>();
    	for (Answer other : others) all_score_names.addAll(other.scores.keySet());
    	/// Just average them for now  - THIS IS A HACK
    	for (String score_name : all_score_names) {
    		double total=0; 
    		for (Answer other : others) {
    			Double score = other.scores.get(score_name);
    			if (score != null) total += score; 
    		}
    	    scores.put(score_name, total / others.size());
    	}
    	
    	// Pick the first candidate answer
    	candidate_text = others.get(0).candidate_text;
    	
    	// Now make an answer from it
    	return new Answer(passages, scores, candidate_text);
    }
    

}
