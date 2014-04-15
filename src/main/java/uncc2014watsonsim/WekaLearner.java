package uncc2014watsonsim;

import java.io.IOException;

import uncc2014watsonsim.scoring.AllEnginesResultsScorer;
import uncc2014watsonsim.scoring.QuestionResultsScorer;
import uncc2014watsonsim.Score;

/** Combines scores using machine learning from Weka */
public class WekaLearner extends Learner {
	
	public void test_implementation(Question question) {
		QuestionResultsScorer weka_scorer = new AllEnginesResultsScorer();
		try {
			weka_scorer.initialize();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Weka learners are missing. Did you install Weka correctly?");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Weka models appear to be missing. Do you have data/scorers? It is not possible to run without them.");
		}
		
		String[] MODEL_ANSWER_DIMENSIONS = new String[]{"PASSAGE_COUNT"};
		String[] MODEL_PASSAGE_DIMENSIONS = new String[]{"BING_RANK", "FITB_EXACT_MATCH_SCORE", "INDRI_RANK", "INDRI_SCORE", "LUCENE_RANK", "LUCENE_SCORE", "PASSAGE_TERM_MATCH", "SKIP_BIGRAM", "WORD_PROXIMITY"};
		
		for (Answer a: question) {
			try {
				a.scores.put("COMBINED", weka_scorer.score(a.scoresArray(MODEL_ANSWER_DIMENSIONS, MODEL_PASSAGE_DIMENSIONS)));
			} catch (Exception e) {
				System.out.println("An unknown error occured while scoring with Weka. Some results may be scored wrong.");
				e.printStackTrace();
				a.scores.put("COMBINED", 0.0);
			}
		}
		
	}

}
