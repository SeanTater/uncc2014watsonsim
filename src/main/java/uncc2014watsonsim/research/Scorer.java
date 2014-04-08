package uncc2014watsonsim.research;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.PassageScore;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;

/** Scorers apply scores in parallel to:
 *  - Answers
 *  - Passages
 *  By default, a score is NaN.
 *  Scorers are expected to run in parallel. Try to avoid side effects.
 *  Otherwise use "synchronized".
 */
public abstract class Scorer { 
	private static final long serialVersionUID = -180815276370746115L;
	// This is a constructor-less hack to give Researchers a convenient name
	// It is used for assigning scores.
	String name;
	Score score_enum;
	{
		name = this.getClass().getSimpleName().replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
		System.out.println(name);
		try {
			score_enum = Score.valueOf(name);
		} catch (IllegalArgumentException e) {
			System.out.println(name + " doesn't have a score.");
			e.printStackTrace();
			throw e;
		}
	}

	/** Default implementation of research for a question.
	 * Simply calls research_answer for every Answer
	 * Override this if you need more power.
	 * @param question
	 * @throws Exception 
	 */
	public void question(Question q) {
		for (Answer a : q)
			for (Passage p: a.passages)
				p.score(score_enum, passage(q, a, p));		
	}
	
	/** Default implementation for researching an answer.
	 * Does nothing by default. You don't need to override this if you don't
	 * use it.
	 * 
	 * @param answer
	 */
	public double passage(Question q, Answer a, Passage p) {
		return Double.NaN;
	}
	
	/** Default implementation for ending question research.
	 * This might trigger some database inserts or like writing, for example.
	 */
	public void complete() {};
}
