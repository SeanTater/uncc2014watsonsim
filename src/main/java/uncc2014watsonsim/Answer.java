package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class Answer implements Comparable<Answer> {
    public List<Document> docs = new ArrayList<Document>();

    /** Create an Answer with one implicitly defined Document */
    public Answer(Document d) {
        this.docs.add(d);
    }
    
    /** Create an Answer with one implicitly defined Document */
    public Answer(String engine, String title, String full_text, String reference, long rank, double score) {
        this(new Document(title, full_text, reference, engine, rank, score));
    }
    
    
    /** Create an Answer (with engine) from JSON */
	public Answer(String engine_name, JSONObject attr) {
		// There is a bit of redundancy: the engine name is in every attribute
        String title = (String) attr.get(String.format("%s_title", engine_name));
        long rank = (long) attr.get(String.format("%s_rank", engine_name));
        double score = (double) attr.get(String.format("%s_score", engine_name));
        docs.add(new Document(title, "", null, engine_name, rank, score));
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

    @Override
    /** Are these two entries approximately equal?
     * Note: This equality is technically intransitive
     *       but treating it like it is may actually be a good idea
     */
    public boolean equals(Object obj) {
    	// Boilerplate equals()
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Answer other = (Answer) obj;
        
        // Now real code: compare titles
        // TODO: NLP could do this better
        // How far to go?
        
        // Here be dragons: This is (title_count^2 * title_length^3) complexity!
        for (Document doc1 : this.docs) {
        	String t1 = doc1.title.replaceFirst("\\(.*\\)", "");
        	for (Document doc2 : other.docs) {
        		String t2 = doc2.title.replaceFirst("\\(.*\\)", "");
                // 2: Up to half of the shorter answer
        		
                int threshold = Math.min(t1.length(), t2.length()) / 2;
                
                if (StringUtils.getLevenshteinDistance(t1.toLowerCase(), t2.toLowerCase()) < threshold)
                	return true;
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
    	return String.format("[%01f %-3s] %s", first("combined").score, engines, getTitle());
    }
    
    /** Fetch the first Engine with this name if it exists (otherwise null) */
    public Document first(String name) {
        // A linear search of 3 or 4 engines is probably not bad
		for (Document e: docs) {
			if (e.engine_name.equals(name)) {
				return e;
			}
		}
        return null;
    }
    
    @Override
	public int compareTo(Answer other) {
    	Document us = first("combined");
    	Document them = other.first("combined");
    	if (us == null || them == null)
    		// Comparing a resultset without a combined engine is undefined
    		return 0;
    	return new Double(us.score).compareTo(new Double(them.score));
	}
    
    /** Change this Answer to include all the information of another */
    public void merge(Answer other) {
    	docs.addAll(other.docs);
    }
    
}