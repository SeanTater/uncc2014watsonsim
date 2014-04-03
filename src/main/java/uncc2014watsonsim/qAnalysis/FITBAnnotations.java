package uncc2014watsonsim.qAnalysis;

import java.util.ArrayList;
import java.util.List;

/**
 * Documents the begin and end characters if FITB annotations on the original
 * question text (Question.raw_text)
 * 
 * @author Ken Overholt
 *
 */
public class FITBAnnotations {

    private int section1Begin = -1;
	private int section1End = -1;
    private int section2Begin = -1;
    private int section2End = -1;
    final private List<BlankAnnotation> blanks = new ArrayList<BlankAnnotation>();
    
    public int getSection1Begin() {
		return section1Begin;
	}
	public void setSection1Begin(int section1Begin) {
		this.section1Begin = section1Begin;
	}
	public int getSection1End() {
		return section1End;
	}
	public void setSection1End(int section1End) {
		this.section1End = section1End;
	}
	public int getSection2Begin() {
		return section2Begin;
	}
	public void setSection2Begin(int section2Begin) {
		this.section2Begin = section2Begin;
	}
	public int getSection2End() {
		return section2End;
	}
	public void setSection2End(int section2End) {
		this.section2End = section2End;
	}
	public List<BlankAnnotation> getBlanks() {
		return blanks;
	}
	
	/* the list structure should not change, only its contents
	 * public void setBlanks(List<BlankAnnotation> blanks) {
		this.blanks = blanks;
	}*/
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("Section 1 begin: " + this.getSection1Begin() + "\n"); 
		sb.append("Section 1 end: ");
		sb.append(this.getSection1End());
		sb.append("\n"); 
		sb.append("Section 2 begin: ");
		sb.append(getSection2Begin());
		sb.append("\n"); 
		sb.append("Section 2 end: ");
		sb.append(this.getSection2End());
		sb.append("\n"); 
		return sb.toString();
	}
    
}
