package uncc2014watsonsim.research;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

public class Correct extends Scorer {
	@Override
	/** Detect if the question matches the answer, score it appropriately
	 * This is to ease machine learning*/
	// TODO: Don't reassign for every passage
	public double scorePassage(Question q, Answer a, Passage p) {
		return a.matches(q.answer) ? 1.0 : 0.0;
	}
}
