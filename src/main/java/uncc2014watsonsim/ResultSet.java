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
public class ResultSet implements Comparable<ResultSet> {

    private List<String> titles = new ArrayList<String>();
    private List<String> full_texts = new ArrayList<String>();
    public List<Engine> engines = new ArrayList<Engine>();

    /** Create a ResultSet with one implicitly defined Engine */
    public ResultSet(String title, String full_text, String engine, long rank, double score) {
        this.titles.add(title);
        this.full_texts.add(full_text);
        this.engines.add(new Engine(engine, rank, score));
    }
    
    /** Create a ResultSet (with engine) from JSON */
	public ResultSet(String engine_name, JSONObject attr) {
		// There is a bit of redundancy: the engine name is in every attribute
        titles.add((String) attr.get(String.format("%s_title", engine_name)));
        long rank = (long) attr.get(String.format("%s_rank", engine_name));
        double score = (double) attr.get(String.format("%s_score", engine_name));
        engines.add(new Engine(engine_name, rank, score));
	}
    
    /** Copy constructor */
    public ResultSet(ResultSet resultset) {
    	titles.add(resultset.getTitle());
    }
    /** some discussion has to be made on how this has to work. Not finalized*/
    public void setTitle(String title){
        titles.add(0, title);
    }
    /** Get the primary title for this record */
    public String getTitle() {
    	// TODO: Consider a way of making a title from multiple titles
    	// NLP realm?
    	String shortest_title = titles.get(0);
    	for (String title : titles)
    		if (title.length() < shortest_title.length())
    			shortest_title = title;
        return shortest_title;
    }
    
    /** Get the primary full text for this record */
    public String getFullText() {
    	return full_texts.get(0);
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
        final ResultSet other = (ResultSet) obj;
        
        // Now real code: compare titles
        // TODO: NLP could do this better
        // How far to go?
        
        // Here be dragons: This is (title_count^2 * title_length^3) complexity!
        for (String t1 : this.titles) {
        	t1 = t1.replaceFirst("\\(.*\\)", "");
        	for (String t2 : other.titles) {
        		t2 = t2.replaceFirst("\\(.*\\)", "");
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
    	for (Engine e: this.engines) engines += e.name.substring(0, 1);
    	
    	// ResultSet don't know if they are correct anymore..
    	//String correct = isCorrect() ? "✓" : "✗";
    	
    	// Should look like: [0.9998 gil] Flying Waterbuffalos ... 
    	return String.format("[%01f %-3s] %s", first("combined").score, engines, getTitle());
    }
    
    /** Fetch the first Engine with this name if it exists (otherwise null) */
    public Engine first(String name) {
        // A linear search of 3 or 4 engines is probably not bad
		for (Engine e: engines) {
			if (e.name.equals(name)) {
				return e;
			}
		}
        return null;
    }
    
    @Override
	public int compareTo(ResultSet other) {
    	Engine us = first("combined");
    	Engine them = other.first("combined");
    	if (us == null || them == null)
    		// Comparing a resultset without a combined engine is undefined
    		return 0;
    	return new Double(us.score).compareTo(new Double(them.score));
	}
    
    /** Change this ResultSet to include all the information of another */
    public void merge(ResultSet other) {
    	engines.addAll(other.engines);
    	titles.addAll(other.titles);
    	// Only add non-empty full texts
    	for (String full_text : other.full_texts)
    		if (!full_text.isEmpty())
    			full_texts.add(full_text);
    }
    
}