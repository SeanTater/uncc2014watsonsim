package uncc2014watsonsim.scorers;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;

/** Scorers apply scores in parallel to:
 *  - Answers
 *  - Passages
 *  By default, a score is NaN.
 *  Scorers are expected to run in parallel. Try to avoid side effects.
 *  Otherwise use "synchronized".
 */
public abstract class PassageScorer extends Scorer { 
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
		Score.registerAnswerScore(max_name);
		Score.registerAnswerScore(min_name);
		Score.registerAnswerScore(mean_name);
		Score.registerAnswerScore(median_name);
	}

	/** Default implementation of research for a question.
	 * Calls research_answer for every Answer, collecting the mean, median, max
	 * and min of the results.
	 * Override this if you need more power.
	 * @param q		Question
	 */
	public QScore scoreQuestion(Question q) {
		QScore answer_scores = new QScore();
		
		// Answers in sequence
		for (Answer a : q) {
			// Passages in parallel
			DoubleStream scores = a.direct_passages.parallelStream().mapToDouble(p->scorePassage(q,a,p));
			
			final int p_count = a.direct_passages.size();
			Scored answer_score = new Scored();
			answer_scores.put(a, answer_score);
			
			if (p_count > 0) {
				// summaryStatistics() from the streaming API consumes it
				// but we need the median too, which it does not give
				// so we wrote it out here
				double[] scores_array = scores.toArray();
				Arrays.sort(scores_array);
				double sum=0.0;
				for (int i=0; i<scores_array.length; i++) sum += scores_array[i];
				sum /= scores_array.length;
				answer_score.put(max_name, scores_array[scores_array.length-1]);
				answer_score.put(min_name, scores_array[0]);
				answer_score.put(mean_name, sum);
				answer_score.put(median_name, scores_array[scores_array.length / 2]);
			}
		}
		return answer_scores;
	}
	
	/** Default implementation for researching a passage.
	 * Does nothing by default. You don't need to override this if you don't
	 * use it.
	 * 
	 * @param q		Input Question, varies slowest
	 * @param a		Input Answer, varies medium
	 * @param p		Input Passage, varies fastest
	 */
	public double scorePassage(Question q, Answer a, Passage p) {
		return Double.NaN;
	}
}
