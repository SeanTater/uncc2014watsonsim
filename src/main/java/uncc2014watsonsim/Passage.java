package uncc2014watsonsim;

import java.util.EnumMap;
import java.util.Map;

public class Passage {
	// Which engine found this passage?
	public String reference;
	public String text;
	public String engine_name;
	public String title;

    public Map<Score, Double> scores = new EnumMap<Score, Double>(Score.class);
    
    /** Return the value of this Score for this answer, or null */
    public Double score(Score name) {
    	return scores.get(name);
    }
    
    /** Set the value of this Score for this answer, returning the Answer.
     * 
     * The intended use is something like this:
     * Answer a = new Answer(.......).score(PassageScore.SKIP_BIGRAM, 9.45).score(PassageScore.SKIP_BIGRAM, -1.2)
     * @param name
     * @param value
     */
    public Passage score(Score name, Double value) {
    	scores.put(name, value);
    	return this;
    }
	
	/** Create a Document
	 * @param engine name
	 * @param title
	 * @param text
	 * @param reference
	 * @param rank
	 * @param score
	 */
	public Passage(String engine_name, String title, String text, String reference) {
		this.text = text;
		this.reference = reference;
		this.engine_name = engine_name;
		this.title = title;
	}
}