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
		
		List<String> MODEL_ANSWER_DIMENSIONS = Arrays.asList(new String[]{"CORRECT", "NGRAM_MAX", "NGRAM_MEAN", "NGRAM_MEDIAN", "NGRAM_MIN", "PASSAGE_COUNT", "PASSAGE_QUESTION_LENGTH_RATIO_MAX", "PASSAGE_QUESTION_LENGTH_RATIO_MEAN", "PASSAGE_QUESTION_LENGTH_RATIO_MEDIAN", "PASSAGE_QUESTION_LENGTH_RATIO_MIN", "PASSAGE_TERM_MATCH_MAX", "PASSAGE_TERM_MATCH_MEAN", "PASSAGE_TERM_MATCH_MEDIAN", "PASSAGE_TERM_MATCH_MIN", "PERCENT_FILTERED_WORDS_IN_COMMON_MAX", "PERCENT_FILTERED_WORDS_IN_COMMON_MEAN", "PERCENT_FILTERED_WORDS_IN_COMMON_MEDIAN", "PERCENT_FILTERED_WORDS_IN_COMMON_MIN", "QUESTION_IN_PASSAGE_SCORER_MAX", "QUESTION_IN_PASSAGE_SCORER_MEAN", "QUESTION_IN_PASSAGE_SCORER_MEDIAN", "QUESTION_IN_PASSAGE_SCORER_MIN", "SKIP_BIGRAM_MAX", "SKIP_BIGRAM_MEAN", "SKIP_BIGRAM_MEDIAN", "SKIP_BIGRAM_MIN", "WORD_PROXIMITY_MAX", "WORD_PROXIMITY_MEAN", "WORD_PROXIMITY_MEDIAN", "WORD_PROXIMITY_MIN"});
		List<String> MODEL_PASSAGE_DIMENSIONS = Arrays.asList(new String[]{/*"BING_RANK",*/ "FITB_EXACT_MATCH_SCORE", "INDRI_RANK", "INDRI_SCORE", "LUCENE_RANK", "LUCENE_SCORE", "NGRAM", "PASSAGE_QUESTION_LENGTH_RATIO", "PASSAGE_TERM_MATCH", "PERCENT_FILTERED_WORDS_IN_COMMON", "QUESTION_IN_PASSAGE_SCORER", "SKIP_BIGRAM", "WORD_PROXIMITY"});
		
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
