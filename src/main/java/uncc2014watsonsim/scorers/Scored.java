package uncc2014watsonsim.scorers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import uncc2014watsonsim.Answer;

/** Type alias for HashMap<String, Double>.
 * This is just to improve static code analysis and programmer sanity. */
public class Scored<T> extends HashMap<String, Double> {
	private static final long serialVersionUID = 824290187783254344L;
	private final T target;
	public Scored(T target) {
		super();
		this.target = target;
	}
	
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
	
	/**
	 * Give something just one score.
	 * 
	 * @param target  Whatever you're scoring
	 * @param name    The name of the score you're giving it
	 * @param score   The value of the score
	 * @return        A fresh new Scored<T>.
	 */
	public static <T> Scored<T> singleton(T target, String name, double score) {
		Scored<T> s = new Scored<T>(target);
		s.put(name, score);
		return s;
	}
	
	/**
	 * Convenience wrapper for singleton(T, String, double)
	 * 
	 * It makes a name for a score based on the name of a class.
	 * QuestionInPassageScorer for example would be
	 * QUESTION_IN_PASSAGE_SCORER.
	 * 
	 * @param target	Whatever it is you're scoring.
	 * @param scorer    The class responsible for generating the score.
	 * @param score     The generated score.
	 * @return
	 */
	public static <T> Scored<T> singleton(T target, Object scorer, double score) {
		return singleton(
				target,
				scorer.getClass()
					.getSimpleName()
					.replaceAll("([a-z])([A-Z]+)", "$1_$2")
					.toUpperCase(),
				score);
	}
	
	/**
	 * Get an answer's scores in the order the model expects it.
	 * That exact order is written out inside the source.
	 * @param self  The Scored<Answer> whose scores should be given in order.
	 * @return 		An array of tight packed doubles. NaN => Unknown
	 */
	public static double[] orderedScores(Scored<Answer> self) {
		double[] out = new double[MODEL_ANSWER_DIMENSIONS.size()];
		for (int i=0; i<MODEL_ANSWER_DIMENSIONS.size(); i++) {
			out[i] = self.getOrDefault(MODEL_ANSWER_DIMENSIONS.get(i), Double.NaN);
		}
		return out;
	}

	/**
	 * Return a plain empty Scored<T>
	 */
	public static <T> Scored<T> mzero(T target) {
		return new Scored<T>(target);
	}
	
	/**
	 * Merge two Scored<T>'s, returning a new copy.
	 * This treats Scored<T>'s as if they are immutable, because it is intended
	 * to be used in parallel processing. If left and right both have the same
	 * keys, then the key from the right will be chosen. But this should never
	 * be the case in normal use.
	 * 
	 * TODO: Replacement is a bad idea. We should use a function or at least a
	 * binary operator like + or *.
	 * @param left		Scores of less preference 
	 * @param right		Scores of greater preference
	 * @return The new copy containing the union of the two keys
	 */
	public static <T> Scored<T> mappend(Scored<T> left, Scored<T> right) {
		if (!left.target.equals(right.target))
			throw new IllegalArgumentException(String.format("Merging scores "
					+ "of different targets makes no sense. Left and right "
					+ "targets are: %s and %s.", left.target, right.target));
		Scored<T> out = new Scored<T>(left.getTarget());
		out.putAll(left);
		out.putAll(right);
		return out;
	}

	/**
	 * Get the target associated with this map of scores.
	 * @return The target of type T
	 */
	public T getTarget() {
		return target;
	}
}