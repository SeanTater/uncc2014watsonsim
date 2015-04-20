
package edu.uncc.cs.watsonsim;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class Answer extends Phrase implements Comparable<Answer> {
    
    public double[] scores = Score.empty();
    private double overall_score = Double.NaN;
    public List<Passage> passages = new ArrayList<>();
    public List<String> lexical_types = new ArrayList<>();

    /**
     * Create an Answer with one implicitly defined Passage
     */
    public Answer(Passage d) {
    	super(d.title);
        this.passages.add(d);
        this.scores = d.scores;
        
    }
    
    public Answer(List<Passage> passages,
    		double[] scores,
    		String candidate_text) {
    	super(candidate_text);
    	this.passages = passages;
    	this.scores = scores;
    	
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
    	super(candidate_text);
    	
    }
    
    // Copy with one mutation: the text
    public Answer(Answer original, String text)
    {
    	super(text);
    	scores = original.scores.clone();
    	for (Passage p : original.passages) {
    		passages.add(new Passage(p));
    	}
    }

    @Override
    public String toString() {
    	// Make a short view of the engines as single-letter abbreviations
    	String engines = "";
    	for (Passage e: this.passages)
    		if (e.engine_name != null)
    			engines += e.engine_name.substring(0, 1);
    	
    	// Should look like: [0.9998 gil] Flying Waterbuffalos ... 
    	return String.format("[%01f %-3s]%s %s",
    			getOverallScore(),
    			engines,
    			Score.get(scores, "CORRECT", 0) == 1 ? "!" : " ",
    			text);
    }
    
    public String toJSON() {
    	return String.format("{\"score\": %01f, \"title\": \"%s\"}", getOverallScore(), text.replace("\"", "\\\""));
    }
    
    /**
     * Return the combined score for the answer, or null
     * */
    public double getOverallScore() {
        return overall_score;
    }
    
    /**
     * Set the combined score for the answer, or null
     * */
    public void setOverallScore(double s) {
        overall_score = s;
    }

    /**
     * Assign a score to this answer. If you want to automatically generate
     * models to go with this score, remember to call Score.registerAnswerScore
     * @param name		The name of the score 
     * @param score		Double value of score (or NaN)
     */
	public void score(String name, double score) {
		scores = Score.set(scores, name, score);
	}
    
    @Override
	public int compareTo(Answer other) {
    	return Double.compare(getOverallScore(), other.getOverallScore());
	}
    
    /** Change this Answer to include all the information of others */
    public static Answer merge(List<Answer> others) {
        List<Passage> passages = new ArrayList<>();
        
        // Merge all the passages
    	for (Answer other : others)
    		passages.addAll(other.passages);
    	
    	// Merge the scores

    	double[] scores = Score.empty();
    	for (Answer other : others)
    		scores = Score.merge(scores, other.scores);
    	

    	// Pick the first candidate answer
    	String candidate_text = others.get(0).text;
    	for (Answer a: others) {
    		if (a.text.length() < candidate_text.length()) {
    			candidate_text = a.text;
    		}
    	}
    	
    	// Now make an answer from it
    	return new Answer(passages, scores, candidate_text);
    }
    

}
