/**
 * 
 */
package uncc2014watsonsim.uima.qAnalysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;
import uncc2014watsonsim.uima.types.UIMAQuestion;

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
		UIMAQuestion uimaQuestion;
		//Get the existing question object, if we don't have it, then create it
		try{
			uimaQuestion = UimaTools.getSingleton(cas, UIMAQuestion.type);
		}catch(UimaToolsException e){
			System.out.println("Creating UIMA Question CAS");
			uimaQuestion = new UIMAQuestion(cas);
			uimaQuestion.addToIndexes();
		}
		
		// See if this is a FITB Question
		boolean result = false;
		Annotation blanks = new Annotation(cas);
		//find blanks and set their locations in the annotation (if they exist)
		Pattern pattern = Pattern.compile("_+");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			blanks = new Annotation(cas, matcher.start(),matcher.end());
			uimaQuestion.setFitbBlanks(blanks);
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

			Annotation sec1 = new Annotation(cas, section1Begin, firstBlankBeginning);
			
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
			Annotation sec2 = new Annotation(cas, section2Begin, section2End);
			
			//System.out.println("current question: " + question.getRaw_text()); //for debug
			//System.out.println(annot); //for debug
			
			uimaQuestion.setFitbSection1(sec1);
			uimaQuestion.setFitbSection2(sec2);
		}
		
	}

}
