package uncc2014watsonsim.research;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;

public class CorrectResearcher extends Researcher {
	@Override
	/** Detect if the question matches the answer, score it appropriately
	 * This is to ease machine learning*/
	public void research(Question q) throws Exception {
		for (Answer a: q)
			a.score(Score.CORRECT, a.matches(q.answer) ? 1.0 : 0.0);
	}
}
