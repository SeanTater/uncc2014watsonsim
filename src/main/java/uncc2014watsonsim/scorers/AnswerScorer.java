package uncc2014watsonsim.scorers;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;

public abstract class AnswerScorer implements Scorer {
	String name;
	{
		name = this.getClass().getSimpleName().replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
		Score.register(name, Double.NaN, Merge.Mean);
	}
	/**
	 * By default, score every answer to a question.
	 * Remember to call scoreAnswer if you override this.
	 * @param q		Question
	 */
	@Override
	public void scoreQuestion(Question q) {
		for (Answer a : q)
			a.score(name, scoreAnswer(q, a));		
	}
	
	/**
	 * Override this method with your scorer implementation.
	 * @param q		Question
	 * @param a		Answer
	 * @return	The score for this answer, or NaN if not applicable.
	 */
	public double scoreAnswer(Question q, Answer a) {
		return Double.NaN;
	}
}
