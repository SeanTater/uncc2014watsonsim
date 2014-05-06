package uncc2014watsonsim.anagramPipeline;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import uncc2014watsonsim.anagram.AnagramQuestion;

public class AnagramParseQuestionEngine extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
			AnagramQuestion question = new AnagramQuestion(aJCas);
			//there should be three parts to the anagram type questions: the indicator, the question part, and the anagram
			//each section should be split up by the following mark: ~=~
			//TODO: remove the need for the ~=~ marker
			question.setFullText(aJCas.getDocumentText());
			String[] question_sections = aJCas.getDocumentText().split("~=~");
			question.setQuestionCategory("ANAGRAM");
			question.setQuestionText(question_sections[0]);
			question.setAnagramText(question_sections[1]);
			question.addToIndexes();
	}
}
