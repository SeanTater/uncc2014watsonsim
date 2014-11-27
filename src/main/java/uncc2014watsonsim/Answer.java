
package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class Answer {
    public List<Passage> direct_passages = new ArrayList<>();
    public List<Passage> supporting_passages = new ArrayList<>();
    public String candidate_text;

    /**
     * Create an Answer with one direct Passage
     */
    public Answer(Passage d) {
        this.direct_passages.add(d);
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
    	for (Passage e: this.direct_passages)
    		if (e.engine_name != null)
    			engines += e.engine_name.substring(0, 1);
    	
    	// Should look like: [0.9998 gil] Flying Waterbuffalos ... 
    	return String.format("[%-3s] %s", engines, candidate_text);
    }
    
    /** Change this Answer to include all the information of another */
    public void merge(Answer other) {
    	direct_passages.addAll(other.direct_passages);
    }
    

}
