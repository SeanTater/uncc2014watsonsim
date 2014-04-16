package uncc2014watsonsim;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
		
		List<String> MODEL_ANSWER_DIMENSIONS = Arrays.asList(new String[]{"PASSAGE_COUNT"});
		List<String> MODEL_PASSAGE_DIMENSIONS = Arrays.asList(new String[]{"BING_RANK", "FITB_EXACT_MATCH_SCORE", "INDRI_RANK", "INDRI_SCORE", "LUCENE_RANK", "LUCENE_SCORE", "PASSAGE_QUESTION_LENGTH_RATIO", "PASSAGE_TERM_MATCH", "PERCENT_FILTERED_WORD_IN_COMMON", "QUESTION_IN_PASSAGE_SCORER", "SKIP_BIGRAM", "WORD_PROXIMITY"});
		
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
