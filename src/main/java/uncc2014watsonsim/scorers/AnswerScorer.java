package uncc2014watsonsim.scorers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;

public abstract class AnswerScorer {
	String name;
	{
		name = this.getClass().getSimpleName().replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
		Score.registerAnswerScore(name);
	}
	
	public Scored<Answer> run(Question q, Answer a, List<Passage> passages) {
		return multiScoreAnswer(q, a, passages);
	}
	
	/**
	 * Overridable convenience method for scorers with only one score.
	 * The name of the score will be generated from the subclass name.
	 *  
	 * @param q		Question
	 * @param a		Answer
	 * @param passages TODO
	 * @return	The score for this answer, or NaN if not applicable.
	 */
	public double scoreAnswer(Question q, Answer a, List<Passage> passages) {
		return Double.NaN;
	}
	
	/**
	 * Overridable method for scorers that return more than one score.
	 * Unlike scoreAnswer, you can also choose your score name.
	 */
	public Scored<Answer> multiScoreAnswer(
			Question q,
			Answer a,
			List<Passage> passages
			) {
		Scored<Answer> m = new Scored<>(a);
		m.put(name, scoreAnswer(q, a, passages));
		return m;
	}
}
