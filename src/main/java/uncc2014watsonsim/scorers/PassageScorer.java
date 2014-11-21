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
			DoubleStream scores = a.passages.parallelStream().mapToDouble(p->scorePassage(q,a,p));
			
			final int p_count = a.passages.size();
			AScore answer_score = new AScore();
			answer_scores.put(a, answer_score);
			
			if (p_count > 0) {
				DoubleSummaryStatistics stats = scores.summaryStatistics();
				answer_score.put(max_name, stats.getMax());
				answer_score.put(min_name, stats.getMin());
				answer_score.put(mean_name, stats.getAverage());
				answer_score.put(median_name, scores.sorted().toArray()[p_count / 2]);
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
