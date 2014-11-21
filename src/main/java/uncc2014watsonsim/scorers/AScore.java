package uncc2014watsonsim.scorers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/** Type alias for HashMap<String, Double>.
 * This is just to improve static code analysis and programmer sanity. */
public class AScore extends HashMap<String, Double> {
	private static final long serialVersionUID = 824290187783254344L;
	public AScore() {super();}
	
	/** These are the dimensions in the current model.
	 * TODO: Find some way to put this in properties instead of the code.
	 * I hate updating it.
	 */
	private static final List<String> MODEL_ANSWER_DIMENSIONS = Arrays.asList(new String[]{
			"BING_RANK_MAX",
			"BING_RANK_MEAN",
			"BING_RANK_MEDIAN",
			"BING_RANK_MIN",
			"CORE_NLPSENTENCE_SIMILARITY_MAX",
			"CORE_NLPSENTENCE_SIMILARITY_MEAN",
			"CORE_NLPSENTENCE_SIMILARITY_MEDIAN",
			"CORE_NLPSENTENCE_SIMILARITY_MIN",
			"CORRECT",
			"DIST_SEM_COS_QASCORE",
			"FITB_EXACT_MATCH_SCORE",
			"GOOGLE_RANK_MAX",
			"GOOGLE_RANK_MEAN",
			"GOOGLE_RANK_MEDIAN",
			"GOOGLE_RANK_MIN",
			"INDRI_ANSWER_RANK",
			"INDRI_ANSWER_SCORE",
			"INDRI_RANK_MAX",
			"INDRI_RANK_MEAN",
			"INDRI_RANK_MEDIAN",
			"INDRI_RANK_MIN",
			"INDRI_SCORE_MAX",
			"INDRI_SCORE_MEAN",
			"INDRI_SCORE_MEDIAN",
			"INDRI_SCORE_MIN",
			"LATTYPE_MATCH_SCORER",
			"LUCENE_ANSWER_RANK",
			"LUCENE_ANSWER_SCORE",
			"LUCENE_RANK_MAX",
			"LUCENE_RANK_MEAN",
			"LUCENE_RANK_MEDIAN",
			"LUCENE_RANK_MIN",
			"LUCENE_SCORE_MAX",
			"LUCENE_SCORE_MEAN",
			"LUCENE_SCORE_MEDIAN",
			"LUCENE_SCORE_MIN",
			"NGRAM_MAX",
			"NGRAM_MEAN",
			"NGRAM_MEDIAN",
			"NGRAM_MIN",
			"PASSAGE_COUNT",
			"PASSAGE_QUESTION_LENGTH_RATIO_MAX",
			"PASSAGE_QUESTION_LENGTH_RATIO_MEAN",
			"PASSAGE_QUESTION_LENGTH_RATIO_MEDIAN",
			"PASSAGE_QUESTION_LENGTH_RATIO_MIN",
			"PASSAGE_TERM_MATCH_MAX",
			"PASSAGE_TERM_MATCH_MEAN",
			"PASSAGE_TERM_MATCH_MEDIAN",
			"PASSAGE_TERM_MATCH_MIN",
			"PERCENT_FILTERED_WORDS_IN_COMMON_MAX",
			"PERCENT_FILTERED_WORDS_IN_COMMON_MEAN",
			"PERCENT_FILTERED_WORDS_IN_COMMON_MEDIAN",
			"PERCENT_FILTERED_WORDS_IN_COMMON_MIN",
			"QUESTION_IN_PASSAGE_SCORER_MAX",
			"QUESTION_IN_PASSAGE_SCORER_MEAN",
			"QUESTION_IN_PASSAGE_SCORER_MEDIAN",
			"QUESTION_IN_PASSAGE_SCORER_MIN",
			"SKIP_BIGRAM_MAX",
			"SKIP_BIGRAM_MEAN",
			"SKIP_BIGRAM_MEDIAN",
			"SKIP_BIGRAM_MIN",
			"WORD_PROXIMITY_MAX",
			"WORD_PROXIMITY_MEAN",
			"WORD_PROXIMITY_MEDIAN",
			"WORD_PROXIMITY_MIN",
			"WPPAGE_VIEWS"
		});
	
	public double[] orderedScores() {
		double[] out = new double[MODEL_ANSWER_DIMENSIONS.size()];
		for (int i=0; i<MODEL_ANSWER_DIMENSIONS.size(); i++) {
			out[i] = getOrDefault(MODEL_ANSWER_DIMENSIONS.get(i), Double.NaN);
		}
		return out;
	}

	/** Return the identity of AScore. */
	public static AScore mzero() {
		return new AScore();
	}
	
	/**
	 * Merge two AScores, returning a new copy.
	 * This treats Ascores as if they are immutable, because it is intended
	 * to be used in parallel processing. If left and right both have the same
	 * keys, then the key from the right will be chosen. But this should never
	 * be the case in normal use.
	 * @param left		Scores of less preference 
	 * @param right		Scores of greater preference
	 * @return The new copy containing the union of the two keys
	 */
	public static AScore mappend(AScore left, AScore right) {
		AScore out = new AScore();
		out.putAll(left);
		out.putAll(right);
		return out;
	}
}