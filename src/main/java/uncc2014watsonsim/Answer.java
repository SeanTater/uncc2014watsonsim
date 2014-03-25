package uncc2014watsonsim;

import java.util.ArrayList;
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
    public Map<String, Double> scores = new HashMap<String, Double>();

    /** Create an Answer with one implicitly defined Document */
    public Answer(Document d) {
        this.docs.add(d);
    }
    
    /** Create an Answer with one implicitly defined Document */
    public Answer(String engine, String title, String full_text, String reference, double rank, double score) {
    	this(new Document(engine, title, full_text, reference));
    	
    	scores.put(engine+"_rank", rank);
    	scores.put(engine+"_score", score);
    }
    
    
    /** Create an Answer (with engine) from JSON */
	public Answer(String engine, JSONObject attr) {
        String title = (String) attr.get(engine+"_title");
        scores.put(engine+"_rank", (double) attr.get(engine+"_rank"));
        scores.put(engine+"_score", (double) attr.get(engine+"_score"));
        docs.add(new Document(engine, title, "", null));
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
    
    /** Convenience method for returning the combined score for this answer */
    public Double score() {
        return scores.get("combined");
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