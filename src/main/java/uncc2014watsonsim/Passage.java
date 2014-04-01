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
    // INDRI_RANK, INDRI_SCORE, LUCENE_RANK, LUCENE_SCORE, GOOGLE_RANK, BING_RANK,
    //   INDRI_PASSAGE_RETRIEVAL_RANK, INDRI_PASSAGE_RETRIEVAL_SCORE, WORD_PROXIMITY,
    //   COMBINED
    private static final double[] defaults_scores = new double[]
    		{20.0, -15.0, 20.0, -1.0, 20.0, 55.0, 20.0, -15.0, 10.0, 0.0};
    
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