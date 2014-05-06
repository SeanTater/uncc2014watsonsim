package uncc2014watsonsim.anagramPipeline;

import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import uncc2014watsonsim.anagram.AnagramQuestion;
import uncc2014watsonsim.anagram.AnagramSupportingPassage;
import uncc2014watsonsim.anagram.CandidateAnswer;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;

public class AnagramSupportingPassageScore extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		/**
		 * Read in question and candidate answers
		 */
		AnagramQuestion question = null;
		List<CandidateAnswer> canAns = null;
		try {
		    question = UimaTools.getSingleton(aJCas, AnagramQuestion.type);
			canAns = UimaTools.getFSList(question.getCandidateAnswers());
		}
		catch (UimaToolsException e) {
			e.printStackTrace();
		}
		/**
		 * Assign Scores to candidate answers (for now, score = sum of lucene scores)
		 */
		for (CandidateAnswer c : canAns) {
			double score = 0;
			List<AnagramSupportingPassage> sps = null;
			try {
				sps = UimaTools.getFSList(c.getSupportingPassages());
			} catch (UimaToolsException e) {
				e.printStackTrace();
			}
			for (AnagramSupportingPassage sp : sps) {
				score += sp.getSearcherScore();
			}
			c.setScore(score);
		}
	}

}
