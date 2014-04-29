package uncc2014watsonsim.qAnalysis;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

import uncc2014watsonsim.Pipeline;
import uncc2014watsonsim.QType;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;
import uncc2014watsonsim.uima.types.UIMAQuestion;

public class AnnotationController {
	
	boolean debug = true;
	
	private Question question = null;
	private JCas queryCas = null;
	/**
	 * @return the queryCas
	 */
	public JCas getQueryCas() {
		return queryCas;
	}

	AnalysisEngine ae = null;
	
	/**
	 * Create annotations as needed for the Question's QType
	 */
	public void createAnnotations(Question question) {
		
		try {
			ae = Pipeline.uimaAE;
			
			JCas jcas = ae.newJCas();
			jcas.setDocumentText(question.getRaw_text());
			queryCas = jcas.createView("QUERY");
			queryCas.setDocumentText(question.getRaw_text());
			ae.process(jcas);
			if(debug){
				String docText = jcas.getDocumentText();
				System.out.println("The text that was analyzed: " + docText);
				UIMAQuestion uimaQuestion = UimaTools.getSingleton(jcas, UIMAQuestion.type);
				
				System.out.println("Found LAT: " + uimaQuestion.getLAT());
				}
		} catch (ResourceInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AnalysisEngineProcessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UimaToolsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(ae == null){
			System.err.println("Running without any UIMA Annotations!!");
		}

		
		
	}
	

	
}
