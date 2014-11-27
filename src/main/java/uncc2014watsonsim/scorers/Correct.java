package uncc2014watsonsim.scorers;

import java.util.List;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

public class Correct extends AnswerScorer {
	@Override
	/**
	 * Generate the target attribute for Machine Learning.
	 * @returns correctness		0.0 -> incorrect, 1.0 -> correct
	 * */
	public Scored<Answer> scoreAnswer(Question q, Answer a, List<Passage> passages) {
		return a.distance(q.answer) >= 2 ? 0 : 1;
	}
}
