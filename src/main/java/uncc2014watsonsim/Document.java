package uncc2014watsonsim;

public class Document extends Passage {
	// Which engine found this passage? 
	public String engine_name;
	public String title;
	
	/** Create a Document
	 * @param engine name
	 * @param title
	 * @param text
	 * @param reference
	 * @param rank
	 * @param score
	 */
	public Document(String engine_name, String title, String text, String reference) {
		super(text, reference);
		this.engine_name = engine_name;
		this.title = title;
	}
}