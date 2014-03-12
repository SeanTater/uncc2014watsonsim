package uncc2014watsonsim;

public class Passage {
	
	// Reference should be something that we can use to find the original document this was taken from
	public String reference;
	public String text;
	
	public Passage(String text, String reference) {
		this.text = text;
		this.reference = reference;
	}
}
