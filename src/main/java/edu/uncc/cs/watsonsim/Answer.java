
package edu.uncc.cs.watsonsim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class Answer extends Phrase implements Comparable<Answer> {
    
    public double[] scores = Score.empty();
    private double overall_score = Double.NaN;
    public List<Passage> passages = new ArrayList<>();
    public List<String> lexical_types = new ArrayList<>();
    private final Queue<Evidence> evidence = new ConcurrentLinkedQueue<>();

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
    
    /**
     * Return a *new answer* that is a copy of the original with new text
     */
    public Answer withText(String text)
    {
    	Answer a = new Answer(text);
    	a.evidence.addAll(evidence);
    	a.scores = scores.clone();
    	for (Passage p : passages) {
    		a.passages.add(new Passage(p));
    	}
    	return a;
    }
    
    /**
     * Return the answer text alone
     */
    @Override
    public String toString() {
    	return text;
    }
    
    /**
     * Return a more detailed version of the answer, including scores.
     */
    public String toLongString() {
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
    
    /**
     * Format an Answer for serialization (e.g. for a web frontend)
     */
    @SuppressWarnings("unchecked")
	public JSONObject toJSON() {
    	JSONObject jo = new JSONObject();
    	jo.put("score", getOverallScore());
    	jo.put("text", text);
    	jo.put("evidence", 
    			evidence.stream()
	        		.map(e -> e.toJSON())
	    			.collect(Collectors.toList()));
    	return jo;
    	
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
	
	/**
	 * Provide some evidence for this class.
	 * @param source (any class)
	 * @param note
	 */
	public void log(Object source, String note, Object... attachments) {
		note = String.format(note, attachments);
		evidence.add(new Evidence(source.getClass().getSimpleName(), note));
	}
	
	/**
	 * Explain why this answer was given (format and return the log)
	 */
    public String explain() {
    	return evidence.stream()
    		.map(e -> String.format("[%s: about \"%s\"] %s", e.source, text, e.note))
    		.reduce((x, y) -> x + "\n" + y)
    		.orElse("No evidence recorded.");
    }
	
    @Override
	public int compareTo(Answer other) {
    	return Double.compare(getOverallScore(), other.getOverallScore());
	}
    
    /** Change this Answer to include all the information of others */
    public static Answer merge(List<Answer> others) {
        List<Passage> passages = new ArrayList<>();
        
        // Merge the passages
    	for (Answer other : others)
    		passages.addAll(other.passages);
    	
    	// Merge the scores
    	double[] scores = Score.empty();
    	for (Answer other : others)
    		scores = Score.merge(scores, other.scores);
    	

    	// Merge the text
    	String candidate_text = others.get(0).text;
    	for (Answer a: others) {
    		if (a.text.length() < candidate_text.length()) {
    			candidate_text = a.text;
    		}
    	}
    	
    	// Merge the evidence
    	Queue<Evidence> evidence = new ConcurrentLinkedQueue<>();
    	for (Answer a: others) {
    		evidence.addAll(a.evidence);
    	}
    	
    	// Make an answer and add evidence
    	Answer a = new Answer(passages, scores, candidate_text);
    	a.evidence.addAll(evidence);
    	
    	return a;
    }
}

class Evidence {
	public final String source;
	public final String note;
	public Evidence(String source, String note) {
		this.source = source;
		this.note = note;
	}
	
	public JSONObject toJSON() {
		JSONObject jo = new JSONObject();
		jo.put("source", source);
		jo.put("note", note);
		return jo;
	}
}