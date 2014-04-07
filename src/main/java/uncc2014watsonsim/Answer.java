
package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.json.simple.JSONObject;

import uncc2014watsonsim.search.Searcher;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class Answer implements Comparable<Answer> {
    public List<Passage> docs = new ArrayList<Passage>();
    public EnumMap<Score, Double> scores = new EnumMap<Score, Double>(Score.class);
    
    public List<Passage> passages = new ArrayList<Passage>();

    /** Create an Answer with one implicitly defined Document */
    public Answer(Passage d) {
        this.docs.add(d);
    }
    
    /** Create an Answer with one implicitly defined Document */
    public Answer(String engine, String title, String full_text, String reference) {
    	this(new Passage(engine, title, full_text, reference));
    }
    
    
    /** Create an Answer (with engine) from JSON */
	public Answer(String engine, JSONObject attr) {
		this(
			engine,
			(String) attr.get(engine+"_title"),
			"",
			"");
		passages.get(0).score(Score.valueOf(engine+"_RANK"), (double) attr.get(engine+"_rank"));
		passages.get(0).score(Score.valueOf(engine+"_SCORE"), (double) attr.get(engine+"_score"));
	}
	
    /** some discussion has to be made on how this has to work. Not finalized*/
    public void setTitle(String title) {
    	docs.get(0).title = title;
    }
    /** Get the primary title for this record */
    public String getTitle() {
    	// TODO: Consider a way of making a title from multiple titles
    	// NLP realm?
    	String shortest_title = docs.get(0).title;
    	for (Passage doc : docs)
    		if (doc.title.length() < shortest_title.length())
    			shortest_title = doc.title;
        return shortest_title;
    }
    
    /** Get the primary full text for this record */
    public String getFullText() {
    	return docs.get(0).text;
    }

    @Override
    /** How to handle inexact matches this way?? */
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.getTitle());
        return hash;
    }

    /** Does `this` match `other`, where `this` is the candidate answer
     *  and `other` is the reference. **Not Transitive or Commutative**!
     */
    public boolean matches(Answer other) {
        if (other == null) {
            return false;
        }
        
        for (Passage doc1 : this.docs) {
        	String t1 = StringUtils.filterRelevant(doc1.title);
        	for (Passage doc2 : other.docs) {
        		String t2 = StringUtils.filterRelevant(doc2.title);
        		if (StringUtils.match_subset(t1, t2)) return true;
        	}
    	}
        return false;
    }

    @Override
    public String toString() {
    	// Make a short view of the engines as single-letter abbreviations
    	String engines = "";
    	for (Passage e: this.docs) engines += e.engine_name.substring(0, 1);
    	
    	// ResultSet don't know if they are correct anymore..
    	//String correct = isCorrect() ? "✓" : "✗";
    	
    	// Should look like: [0.9998 gil] Flying Waterbuffalos ... 
    	return String.format("[%01f %-3s] %s", score(), engines, getTitle());
    }
    
    public String toJSON() {
    	return String.format("{\"score\": %01f, \"title\": \"%s\"}", score(), getTitle().replace("\"", "\\\""));
    }
    
    /* Score retrieval */
    
    /** Return the combined score for the answer, or null */
    public Double score() {
        return scores.get(Score.COMBINED);
    }
    
    /** Convenience method for returning all of the answer's scores as a primitive double[].
     * Intended for Weka, but it could be useful for any ML. */
    public double[] scoresArray() {
    	// First all of the answer's scores, followed by all of the scores from
    	// all of the passages
    	double[] out = new double[Searcher.MAX_RESULTS * Score.values().length];
    	Arrays.fill(out, Double.NaN);
		
		// All the passage's scores
		for (int pi=0; pi<passages.size(); pi++) {
			int passage_offset = pi * Searcher.MAX_RESULTS;
			Passage p = passages.get(pi);
			for (Entry<Score, Double> dimension : p.scores.entrySet()) {
				out[passage_offset + dimension.getKey().ordinal()] = dimension.getValue();
			}
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
    	docs.addAll(other.docs);
    }
    

}
