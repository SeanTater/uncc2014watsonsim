package uncc2014watsonsim.qAnalysis;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import uncc2014watsonsim.DefaultPipeline;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;
import uncc2014watsonsim.uima.types.UIMAQuestion;

public class AnnotationController {
	boolean success = true;
	boolean debug = true;
	AnalysisEngine ae;
	private JCas queryCas = null;
	private JCas jcas;
	
	/**
	 * Create a new blank AnnotationController
	 */
	public AnnotationController() {
		 ae = DefaultPipeline.uimaAE;
		 try {
			 jcas = ae.newJCas();
		 } catch (ResourceInitializationException e) {
			 fail(e);
		 }
	}
	
	/**
	 * Report failure, bringing UIMA analysis to a halt but not stopping the
	 * system. Only reports the first error.
	 * @param e  Any exception
	 */
	private void fail(Exception e) {
		if (success) {
			success = false;
			e.printStackTrace();
			System.err.println("Running without any UIMA Annotations!!");
		}
	}
	
	/**
	 * @return the queryCas
	 */
	public JCas getQueryCas() {
		return queryCas;
	}
	/*
	 * This is the only one we really need, the other is for convenience.
	 */
	public JCas getCas(){
		return jcas;
	}

	
	/**
	 * Create annotations as needed for the Question's QType
	 */
	public void createAnnotations(Question question) {
		String q_text = question.getRaw_text();
		jcas.reset();
		jcas.setDocumentText(q_text);
		try {
			queryCas = jcas.createView("QUERY");
			queryCas.setDocumentText(q_text);
			ae.process(jcas);
			if(debug){
				System.out.println("The text that was analyzed: " + q_text);
				UIMAQuestion uimaQuestion = UimaTools.getSingleton(jcas, UIMAQuestion.type);
				
				System.out.println("Found LAT: " + uimaQuestion.getLAT());
			}
		} catch (AnalysisEngineProcessException | CASException | UimaToolsException e) {
			fail(e);
		}
	}
	

	
}
