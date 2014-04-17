package uncc2014watsonsim.qAnalysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uncc2014watsonsim.QType;
import uncc2014watsonsim.Question;

public class AnnotationController {
	
	private Question question = null;
	
	/**
	 * Create annotations as needed for the Question's QType
	 */
	public void createAnnotations(Question question) {
		
		this.question = question;
		
		if (question.getType() == QType.FITB) {
			createFitbAnnotations();
		}
		
	}
	
	private void createFitbAnnotations() {

		FITBAnnotations annot = question.getFITBAnnotations(); //temporary holder to shorten calls to this
		boolean result = false;
		//find blanks and set their locations in the annotation (if they exist)
		Pattern pattern = Pattern.compile("_+");
		Matcher matcher = pattern.matcher(question.getRaw_text());
		while (matcher.find()) {
			annot.getBlanks().add( new BlankAnnotation(matcher.start(),matcher.end()) );
			//System.out.print(" blank: " + question.raw_text.subSequence(matcher.start(), matcher.end())); //debug
			result = true; //return will be true as a blank was found
		}
		
		//if blanks were found, find and set locations of sections 1 & 2 in the annotations (if they exist)
		if (result == true) {
			pattern = Pattern.compile("\"");
			matcher = pattern.matcher(question.getRaw_text());
			int firstBlankBeginning = annot.getBlanks().get(0).getBegin();
			annot.setSection1End(firstBlankBeginning);
			
			annot.setSection1Begin(0);
			while (matcher.find() && matcher.start()< firstBlankBeginning) {
				annot.setSection1Begin(matcher.start());
			}

			annot.setSection2Begin(annot.getBlanks().get(annot.getBlanks().size()-1).getEnd());
			matcher.region(annot.getSection2Begin(), question.getRaw_text().length());
			if (matcher.find()) {
				annot.setSection2End(matcher.end());
			}
			else {
				annot.setSection2End(question.getRaw_text().length());
			}
			
			//System.out.println("current question: " + question.getRaw_text()); //for debug
			//System.out.println(annot); //for debug
		}

	}
	
}
