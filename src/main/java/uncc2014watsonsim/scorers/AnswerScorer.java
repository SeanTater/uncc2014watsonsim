package uncc2014watsonsim.scorers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;

public abstract class AnswerScorer extends Scorer {
	/**
	 * Overridable convenience method for scorers with only one score.
	 * The name of the score will be generated from the subclass name.
	 *  
	 * @param q		Question
	 * @param a		Answer
	 * @param passages TODO
	 * @return	The score for this answer, or NaN if not applicable.
	 */
	public Scored<Answer> scoreAnswer(Question q, Answer a, List<Passage> passages) {
		return wrap(a, Double.NaN);
	}
}
