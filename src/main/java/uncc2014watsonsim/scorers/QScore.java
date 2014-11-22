package uncc2014watsonsim.scorers;

import java.util.HashMap;

import uncc2014watsonsim.Answer;

/** Type alias for HashMap<Answer, AScore>.
 * This is just to improve static code analysis and programmer sanity. */
public class QScore extends HashMap<Answer, AScore> {
	private static final long serialVersionUID = -6099128768170069764L;
	public QScore() {super();}
	
	public static QScore singleton(Answer a, AScore as) {
		QScore qs = new QScore();
		qs.put(a, as);
		return qs;
	}
	
	/** Return the identity of AScore. */
	public static QScore mzero() {
		return new QScore();
	}
	
	/**
	 * Merge two QScores, returning a new copy.
	 * This treats QScores as if they are immutable, because it is intended
	 * to be used in parallel processing. If left and right both have the same
	 * keys, then the key from the right will be chosen. But this should never
	 * be the case in normal use.
	 * 
	 * This is an O(N) operation so it could be a bottleneck later. Profiling
	 * may be necessary.
	 * @param left		Scores of less preference 
	 * @param right		Scores of greater preference
	 * @return The new copy containing the union of the two keys
	 */
	public static QScore mappend(QScore left, QScore right) {
		QScore out = new QScore();
		out.putAll(left);
		out.putAll(right);
		return out;
	}
}