package uncc2014watsonsim.uima.uimaexperiment;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.Sofa;

import uncc2014watsonsim.QClassDetection;
import uncc2014watsonsim.QType;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;
import uncc2014watsonsim.uima.types.UIMAQuestion;

public class findQuestionTypeEngine extends JCasAnnotator_ImplBase {
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			UIMAQuestion question = UimaTools.getSingleton(aJCas, UIMAQuestion.type);
			uncc2014watsonsim.Question q = new uncc2014watsonsim.Question(question.getRaw_text());
			question.setQtype(q.getType().toString());
		}
		catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
