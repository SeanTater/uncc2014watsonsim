package uncc2014watsonsim.research;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

public class Correct extends AnswerScorer {
	@Override
	/**
	 * Generate the target attribute for Machine Learning.
	 * @returns correctness		1.0 -> correct, 0.0 -> incorrect
	 * */
	public double scoreAnswer(Question q, Answer a) {
		return a.matches(q.answer) ? 1.0 : 0.0;
	}
}
