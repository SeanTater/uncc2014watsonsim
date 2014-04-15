package uncc2014watsonsim.uima.uimaexperiment;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import uncc2014watsonsim.StringUtils;

import uncc2014watsonsim.uima.types.UIMAQuestion;

public class readQuestionEngine extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String questionText = aJCas.getDocumentText();
		UIMAQuestion q = new UIMAQuestion(aJCas);
		q.setRaw_text(questionText);
		q.setFiltered_text(StringUtils.filterRelevant(q.getRaw_text()));
		q.addToIndexes();

	}

}
