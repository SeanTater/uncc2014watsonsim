package uncc2014watsonsim.scorers;

import java.util.HashMap;
import java.util.Map;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;

public abstract class AnswerScorer extends Scorer {
	String name;
	{
		name = this.getClass().getSimpleName().replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
		Score.registerAnswerScore(name);
	}
	/**
	 * By default, score every answer to a question.
	 * Remember to call scoreAnswer if you override this.
	 * @param q		Question
	 * @return 
	 */
	@Override
	public QScore scoreQuestion(Question q) {
		QScore answer_scores = new QScore();
		q.parallelStream()
			.map(a -> multiScoreAnswer(q, a))
			.;
		for (Answer a : q)
			answer_scores.put(a, multiScoreAnswer(q, a));
		return answer_scores;
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
	
	/**
	 * Wrapper for handling the whole map at once
	 */
	public AScore multiScoreAnswer(Question q, Answer a) {
		AScore m = new AScore();
		m.put(name, scoreAnswer(q, a));
		return m;
	}
}
