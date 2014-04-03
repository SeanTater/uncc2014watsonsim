package uncc2014watsonsim.qAnalysis;

/**
 * Used by FITBAnnotations to store the locations of the blanks in the FITB question.
 * 
 * Begin is the 0-based index of the first character in the blank.
 * End is the 0-based index of the character after the last character in the blank.
 * 
 * @author Ken Overholt
 *
 */
public class BlankAnnotation {

	private int begin = -1;
	private int end = -1;

	/**
	 * Default constructor.  Both begin and end fields are -1 indicating they haven't been set yet.
	 */
	public BlankAnnotation() {
		
	}
	
	/**
	 * Constructor setting both begin and end locations of the blank
	 * @param begin 0-based index of the first character in the blank
	 * @param end 0-based index of the character after the last character in the blank
	 */
	public BlankAnnotation(int begin, int end) {
		setBegin(begin);
		setEnd(end);
	}
	
	/**
	 * @return the 0-based character location within the document where the blank begins
	 */
	public int getBegin() {
		return begin;
	}
	
	/**
	 * @param begin the 0-based character location within the document where the blank begins
	 */
	public void setBegin(int begin) {
		this.begin = begin;
	}
	
	/**
	 * @return the 0-based character location within the document where the blank ends
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @param end the 0-based character location within the document where the blank ends
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	
}
