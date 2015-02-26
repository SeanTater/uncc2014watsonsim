package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

public class Passage {
	// Which engine found this passage?
	public final String reference;
	private final String text;
	private final List<String> _tokens;
	public final String engine_name;
	public final String title;
    public double[] scores = Score.empty();
    
    /**
     * Create a new Passage
     * 
     * @param engine_name  A simple lowercase string
     * @param title
     * @param text
     * @param reference   Specific to the engine, or a URL, for later lookup
     */
	public Passage(String engine_name, String title, String text, String reference) {
		if (engine_name == null)
			throw new NullPointerException("Engine name cannot be null.");
		if (title == null)
			throw new NullPointerException("Title cannot be null.");
		if (text == null)
			throw new NullPointerException("Text cannot be null.");
		if (reference == null)
			throw new NullPointerException("Reference cannot be null.");
			
		this.text = StringEscapeUtils.unescapeXml(text);
		this._tokens = StringUtils.tokenize(this.text);
		this.reference = reference;
		this.engine_name = engine_name;
		this.title = StringEscapeUtils.unescapeXml(title);
	}
    
    /** Return the value of this Score for this answer, or null */
    public double score(String name) {
    	scores = Score.update(scores);
    	return Score.get(scores, name, -1);
    }
    
    /** Set the value of this Score for this passage, returning the passage.
     * 
     * The intended use is something like this:
     * new Passage(.......).score("SKIP_BIGRAM", 9.45).score("NGRAM", -1.2)
     * @param name
     * @param value
     */
    public Passage score(String name, double value) {
    	scores = Score.set(scores, name, value);
    	return this;
    }
    
    /**
     * Get a filtered list of tokens from the passage text
     * @return
     */
    public List<String> tokens() {
    	return new ArrayList<>(_tokens);
    }

    /**
     * Wrapper for passage text
     * This is wrapped because it triggers on-the-fly calculations
     * @return
     */
	public String getText() {
		return text;
	}
}