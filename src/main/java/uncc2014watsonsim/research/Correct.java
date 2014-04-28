package uncc2014watsonsim.research;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

public class Correct extends AnswerScorer {
	@Override
	/**
	 * Generate the target attribute for Machine Learning.
	 * @returns correctness		0.0 -> correct, 1.0 -> incorrect
	 * */
	public double scoreAnswer(Question q, Answer a) {
		return a.matches(q.answer) ? 0.0 : 1.0;
	}
}
