package edu.uncc.cs.watsonsim.scorers;

import java.util.List;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.Score;

public abstract class AnswerScorer implements Scorer {
	String name;
	{
		name = this.getClass().getSimpleName().replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
		Score.register(name, 0.0, Merge.Sum);
	}
	/**
	 * By default, score every answer to a question.
	 * Remember to call scoreAnswer if you override this.
	 * @param q		Question
	 */
	@Override
	public void scoreQuestion(Question q, List<Answer> answers) {
		for (Answer a : answers)
			a.score(name, scoreAnswer(q, a));		
	}
	
	/**
	 * Override this method with your scorer implementation.
	 * @param q		Question
	 * @param a		Answer
	 * @return	The score for this answer, or NaN if not applicable.
	 */
	public double scoreAnswer(Question q, Answer a) {
		return 0.0;
	}
}
