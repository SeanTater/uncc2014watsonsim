package uncc2014watsonsim;

import java.util.List;

/**
 * A passage is a paragraph-length block a text with a title and reference.
 * It can be tokenized (which will be cached).
 * It is thread-safe.
 * @author Sean Gallagher
 *
 */
public class Passage {
	// Which engine found this passage?
	public final String reference;
	public final String text;
	private List<String> _tokens;
	public final String engine_name;
	public final String title;
    
    /**
     * Create a new Passage
     * 
     * @param engine_name  A simple lowercase string
     * @param title
     * @param text
     * @param reference   Specific to the engine, or a URL, for later lookup
     */
	public Passage(String engine_name, String title, String text, String reference) {
		this.text = text;
		this._tokens = null;
		this.reference = reference;
		this.engine_name = engine_name;
		this.title = title;
	}
    
    /**
     * Get a filtered list of tokens from the passage text
     * @return
     */
    public synchronized List<String> tokens() {
    	if (_tokens == null)
    		_tokens = StringUtils.tokenize(getText());
    	return _tokens;
    }

    /**
     * Wrapper for passage text
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