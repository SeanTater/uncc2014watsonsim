package edu.uncc.cs.watsonsim.scorers;


import java.util.Arrays;
import java.util.List;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.Score;

/** Scorers apply scores in parallel to:
 *  - Answers
 *  - Passages
 *  By default, a score is NaN.
 *  Scorers are expected to run in parallel. Try to avoid side effects.
 *  Otherwise use "synchronized".
 */
public abstract class PassageScorer implements Scorer { 
	// This is a constructor-less hack to give Researchers a convenient name
	// It is used for assigning scores.
	String name;
	private String max_name, min_name, median_name, mean_name;
	{
		name = this.getClass().getSimpleName().replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
		max_name = name+"_MAX";
		min_name = name+"_MIN";
		mean_name = name+"_MEAN";
		median_name = name+"_MEDIAN";
		Score.register(max_name, -1, Merge.Mean);
		Score.register(min_name, -1, Merge.Mean);
		Score.register(mean_name, -1, Merge.Mean);
		Score.register(median_name, -1, Merge.Mean);
	}

	/** Default implementation of research for a question.
	 * Calls research_answer for every Answer, collecting the mean, median, max
	 * and min of the results.
	 * Override this if you need more power.
	 * @param q		Question
	 */
	public void scoreQuestion(Question q, List<Answer> answers) {
		for (Answer a : answers) {
			double sum = 0.0;
			final int p_count = a.passages.size();
			if (p_count > 0) {
				double[] scores = new double[p_count];
				for (int pi=0; pi<p_count; pi++) {
					Passage p = a.passages.get(pi);
					scores[pi] = scorePassage(q, a, p); 
					sum += scores[pi];
					p.score(name, scores[pi]);
				}
				Arrays.sort(scores);
				a.score(max_name, scores[0]);
				a.score(min_name, scores[p_count - 1]);
				a.score(mean_name, sum/p_count);
				a.score(median_name, scores[p_count / 2]);
			}
		}
	}
	
	/** Default implementation for researching a passage.
	 * Does nothing by default. You don't need to override this if you don't
	 * use it.
	 * 
	 * @param q		Input Question, varies slowest
	 * @param a		Input Answer, varies medium
	 * @param p		Input Passage, varies fastest
	 */
	public double scorePassage(Phrase q, Answer a, Passage p) {
		return Double.NaN;
	}
}
