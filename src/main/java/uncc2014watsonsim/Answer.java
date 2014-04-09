
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
    public EnumMap<Score, Double> scores = new EnumMap<Score, Double>(Score.class);
    public List<Passage> passages = new ArrayList<Passage>();
    public String candidate_text;

    /** Create an Answer with one implicitly defined Document */
    public Answer(Passage d) {
        this.passages.add(d);
        candidate_text = d.title;
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

    @Override
    /** How to handle inexact matches this way?? */
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.candidate_text);
        return hash;
    }

    /** Does `this` match `other`, where `this` is the candidate answer
     *  and `other` is the reference. **Not Transitive or Commutative**!
     */
    public boolean matches(Answer other) {
        if (other == null) {
            return false;
        }
        
        return StringUtils.match_subset(other.candidate_text, candidate_text);
        
        /* The old method: any two passages match 
        for (Passage doc1 : this.passages) {
        	String t1 = StringUtils.filterRelevant(doc1.title);
        	for (Passage doc2 : other.passages) {
        		String t2 = StringUtils.filterRelevant(doc2.title);
        		if (StringUtils.match_subset(t1, t2)) return true;
        	}
    	}*/
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
    
    /* Score retrieval */
    
    /** Return the combined score for the answer, or null */
    public Double score() {
        return scores.get(Score.COMBINED);
    }
    
    public static String[] scoreNames() {
    	String[] names = new String[Searcher.MAX_RESULTS * Score.values().length];
    	for (int a=0; a<Searcher.MAX_RESULTS; a++)
    		for (int b=0; b<Score.values().length; b++)
    			names[a*Score.values().length + b] = Score.values()[b].name() + "_" + a;
    	return names;
    }
    
    /** Convenience method for returning all of the answer's scores as a primitive double[].
     * Intended for Weka, but it could be useful for any ML. */
    public double[] scoresArray() {
    	// First all of the answer's scores, followed by all of the scores from
    	// all of the passages
    	double[] out = new double[Searcher.MAX_RESULTS * Score.values().length];
    	Arrays.fill(out, Double.NaN);
		
		// All the passage's scores
    	// TODO: It's possible to have more than MAX_RESULTS passages because of merging.
		for (int p_i=0; p_i<Searcher.MAX_RESULTS; p_i++) {
			int passage_offset = p_i * Searcher.MAX_RESULTS;
			Passage p = passages.get(p_i);
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
    	passages.addAll(other.passages);
    	// Merge scores
    	Passage p_other = other.passages.get(0);
    	for(Score s : p_other.scores.keySet()) {
    		if(!passages.get(0).scores.containsKey(s)) {
    			passages.get(0).scores.put(s, p_other.score(s));
    		}
    		else { // take highest score
    			passages.get(0).scores.put(s, Math.max(passages.get(0).score(s), p_other.score(s)));
    		}    			
    	}
    	/* TODO: merge internal passages? */
    }

	public double[] SearchResultsArray() {
		// TODO Auto-generated method stub
		// array of scores for results returned from search engines
		// first passage is result originally returned from search engine
		double[] out = new double[Score.values().length];
		Arrays.fill(out, 0.0);
		for(Score s: passages.get(0).scores.keySet())
			out[s.ordinal()] = passages.get(0).scores.get(s);
		return out;
	}
    

}
