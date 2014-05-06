package uncc2014watsonsim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Passage {
	// Which engine found this passage?
	public String reference;
	private String text;
	private List<String> _tokens;
	public String engine_name;
	public String title;
    public Map<String, Double> scores = new HashMap<>();
    

	/** 
	 * Create a Passage with all its required fields
	 */
	public Passage(String engine_name, String title, String text, String reference) {
		this.setText(text);
		this.reference = reference;
		this.engine_name = engine_name;
		this.title = title;
	}
    
    /** Return the value of this Score for this answer, or null */
    public double score(String name) {
    	Double s = scores.get(name);
    	return s == null ? Double.NaN : s;
    }
    
    /** Set the value of this Score for this answer, returning the Answer.
     * 
     * The intended use is something like this:
     * Answer a = new Answer(.......).score(PassageScore.SKIP_BIGRAM, 9.45).score(PassageScore.SKIP_BIGRAM, -1.2)
     * @param name
     * @param value
     */
    public Passage score(String name, double value) {
    	scores.put(name, value);
    	return this;
    }
    
    /**
     * Get a filtered list of tokens from the passage text
     * @return
     */
    public List<String> tokens() {
    	if (_tokens == null)
    		_tokens = StringUtils.tokenize(getText());
    	return _tokens;
    }

    /**
     * Wrapper for passage text
     * This is wrapped because it triggers on-the-fly calculations
     * @return
     */
	public String getText() {
		return text;
	}
	/**
     * Wrapper for passage text
     * This is wrapped because it triggers on-the-fly calculations
     */
	public void setText(String text) {
		this.text = text;
    	this._tokens = null;
	}
}