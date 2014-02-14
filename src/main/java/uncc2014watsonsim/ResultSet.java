package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class ResultSet implements Comparable<ResultSet> {

    private List<String> titles = new ArrayList<String>();
    private boolean correct;
    public List<Engine> engines = new ArrayList<Engine>();

    /** Create a ResultSet with one implicitly defined Engine */
    public ResultSet(String title, String engine, long rank, double score, boolean correct) {
        this.titles.add(title);
        this.engines.add(new Engine(engine, rank, score));
        this.correct = correct;
    }
    
    /** Create a ResultSet (with engine) from JSON */
	public ResultSet(String engine_name, JSONObject attr) {
		// There is a bit of redundancy: the engine name is in every attribute
        titles.add((String) attr.get(String.format("%s_title", engine_name)));
        long rank = (long) attr.get(String.format("%s_rank", engine_name));
        double score = (double) attr.get(String.format("%s_score", engine_name));
        engines.add(new Engine(engine_name, rank, score));
        // TODO: Reformat questions so that the correct answer is a ResultSet in the engine "perfect"
        String a = (String) attr.get(String.format("%s_answer", engine_name));
        correct = a.equalsIgnoreCase("yes");
	}
    
    /** Copy constructor */
    public ResultSet(ResultSet resultset) {
    	titles.add(resultset.getTitle());
    	correct = resultset.correct;
    }

    /** Get the primary title for this record */
    public String getTitle() {
    	// TODO: Consider a way of making a title from multiple titles
    	// NLP realm?
        return titles.get(0);
    }

    /** deprecated */
    public boolean isCorrect() {
        return correct;
    }
    /** deprecated */
    public void setCorrect(boolean correct) {
        this.correct = correct;
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
        	for (String t2 : other.titles) {
                // 1: You get the length difference for free
                int threshold = Math.abs(t1.length() - t2.length());
                // 2: And up to a quarter of the shorter answer  
                threshold += Math.min(t1.length(), t1.length()) / 4;
                
                if (StringUtils.getLevenshteinDistance(t1, t2) < threshold)
                	return true;
        	}
        }
    	return false;
    }

    @Override
    public String toString() {
        return "ResultSet{" + "title=" + getTitle() + ", correct=" + correct + '}';
    }
    
    /** Fetch the combined Engine if it exists (otherwise null) */
    public Engine combined() {
        // A linear search of 3 or 4 engines is probably not bad
		for (Engine e: engines) {
			if (e.name.equals("combined")) {
				return e;
			}
		}
        return null;
    }
    
    @Override
	public int compareTo(ResultSet other) {
    	Engine us = combined();
    	Engine them = other.combined();
    	if (us == null || them == null)
    		// Comparing a resultset without a combined engine is undefined
    		return 0;
    	return new Double(us.score).compareTo(new Double(them.score));
	}
    
    /** Change this ResultSet to include all the information of another */
    public void merge(ResultSet other) {
    	engines.addAll(other.engines);
    	titles.addAll(other.titles);
    }
}