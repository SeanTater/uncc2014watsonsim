package uncc2014watsonsim;

public class Document extends Passage {
	// Which engine found this passage? 
	public String engine_name;
	public String title;
	public long rank;
	public double score;
	
	/** Create a Document
	 * @param engine name
	 * @param title
	 * @param text
	 * @param reference
	 * @param rank
	 * @param score
	 */
	public Document(String title, String text, String reference, String engine_name, long rank, double score) {
		super(text, reference);
		this.engine_name = engine_name;
		this.title = title;
		this.rank = rank;
		this.score = score;
	}
}