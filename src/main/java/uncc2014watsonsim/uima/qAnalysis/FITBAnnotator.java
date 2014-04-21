/**
 * 
 */
package uncc2014watsonsim.uima.qAnalysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.AbstractCas;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uncc2014watsonsim.qAnalysis.BlankAnnotation;
import uncc2014watsonsim.uima.types.fitb.Blanks;
import uncc2014watsonsim.uima.types.fitb.Section1;
import uncc2014watsonsim.uima.types.fitb.Section2;

/**
 * A basic annotator which will add blanks and Sections to a CAS.
 * @author Jonathan Shuman (UIMA)
 * @author Ken Overholt (Logic)
 *
 */
public class FITBAnnotator extends  JCasAnnotator_ImplBase {
	/**
	   * Any initializations of data structures/engines (e.g., dictionary) would go into the initialize
	   * method. In this dummy class, we don't actually use an external resource.
	   */
	  @Override
	  public void initialize(UimaContext aContext) throws ResourceInitializationException {
	    super.initialize(aContext);
	  }

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		//Get the document text
		String text = cas.getDocumentText();
		
		// See if this is a FITB Question
		boolean result = false;
		Blanks blanks = new Blanks(cas);
		//find blanks and set their locations in the annotation (if they exist)
		Pattern pattern = Pattern.compile("_+");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			blanks = new Blanks(cas, matcher.start(),matcher.end());
			blanks.addToIndexes();
			//System.out.print(" blank: " + question.raw_text.subSequence(matcher.start(), matcher.end())); //debug
			result = true; //return will be true as a blank was found
			break; //Stop running after we find a sequence of blanks
		}
		//if blanks were found, find and set locations of sections 1 & 2 in the annotations (if they exist)
		if (result == true) {
			pattern = Pattern.compile("\"");
			matcher = pattern.matcher(text);
			int firstBlankBeginning = blanks.getBegin();
	
			int section1Begin = 0;
			while (matcher.find() && matcher.start()< firstBlankBeginning) {
				section1Begin = matcher.start();
			}

			Section1 sec1 = new Section1(cas, section1Begin, firstBlankBeginning);
			sec1.addToIndexes();
			
			//Next we will setup section 2
			int section2Begin = blanks.getEnd() + 1;
			
			matcher.region(section2Begin, text.length());
			
			int section2End;
			if (matcher.find()) {
				section2End = matcher.end();
			}
			else {
				section2End =text.length();
			}
			Section2 sec2 = new Section2(cas, section2Begin, section2End);
			sec2.addToIndexes();
			
			//System.out.println("current question: " + question.getRaw_text()); //for debug
			//System.out.println(annot); //for debug
		}
		
	}

}
