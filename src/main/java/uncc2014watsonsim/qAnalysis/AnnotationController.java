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
	
	boolean debug = true;
	
	private JCas queryCas = null;
	private JCas jcas = null;
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

	AnalysisEngine ae = null;
	
	/**
	 * Create annotations as needed for the Question's QType
	 */
	public void createAnnotations(Question question) {
		
		try {
			ae = DefaultPipeline.uimaAE;
			
			jcas = ae.newJCas();
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
		} catch (ResourceInitializationException | AnalysisEngineProcessException | CASException | UimaToolsException e) {
			e.printStackTrace();
		}
		
		if(ae == null){
			System.err.println("Running without any UIMA Annotations!!");
		}

		
		
	}
	

	
}
