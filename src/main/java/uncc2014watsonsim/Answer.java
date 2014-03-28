package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.EnumMap;
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
    public List<Document> docs = new ArrayList<Document>();
    public Map<Score, Double> scores = new EnumMap<Score, Double>(Score.class);
    // INDRI_RANK, INDRI_SCORE, LUCENE_RANK, LUCENE_SCORE, GOOGLE_RANK, BING_RANK, WORD_PROXIMITY, COMBINED, CORRECT
    private static final double[] defaults_scores = new double[]
    		{20.0, -15.0, 20.0, -1.0, 20.0, 55.0, 10.0, 0.0, -1.0};

    /** Create an Answer with one implicitly defined Document */
    public Answer(Document d) {
        this.docs.add(d);
    }
    
    /** Create an Answer with one implicitly defined Document */
    public Answer(String engine, String title, String full_text, String reference) {
    	this(new Document(engine, title, full_text, reference));
    }
    
    
    /** Create an Answer (with engine) from JSON */
	public Answer(String engine, JSONObject attr) {
		this(
			engine,
			(String) attr.get(engine+"_title"),
			"",
			"");
		score(Score.valueOf(engine+"_RANK"), (double) attr.get(engine+"_rank"));
		score(Score.valueOf(engine+"_SCORE"), (double) attr.get(engine+"_score"));
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
    	for (Document doc : docs)
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
        
        for (Document doc1 : this.docs) {
        	String t1 = StringUtils.filterRelevant(doc1.title);
        	for (Document doc2 : other.docs) {
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
    	for (Document e: this.docs) engines += e.engine_name.substring(0, 1);
    	
    	// ResultSet don't know if they are correct anymore..
    	//String correct = isCorrect() ? "✓" : "✗";
    	
    	// Should look like: [0.9998 gil] Flying Waterbuffalos ... 
    	return String.format("[%01f %-3s] %s", score(), engines, getTitle());
    }
    
    /* Score retrieval */
    
    /** Return the combined score for the answer, or null */
    public Double score() {
        return scores.get(Score.COMBINED);
    }
    
    /** Return the value of this Score for this answer, or null */
    public Double score(Score name) {
    	return scores.get(name);
    }
    
    /** Set the value of this Score for this answer, returning the Answer.
     * 
     * The intended use is something like this:
     * Answer a = new Answer(.......).score(Score.BING_RANK, 9.45).score(Score.INDRI_SCORE, -1.2)
     * @param name
     * @param value
     */
    public Answer score(Score name, Double value) {
    	scores.put(name, value);
    	return this;
    }
    
    /** Convenience method for returning all of the answer's scores as a primitive double[].
     * Intended for Weka, but it could be useful for any ML. */
    public double[] scoresArray() {
    	double[] out = defaults_scores.clone();
		for (Score dimension : scores.keySet()) {
			out[dimension.ordinal()] = score(dimension);
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
