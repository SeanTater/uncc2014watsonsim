package uncc2014watsonsim.scorers;

/*
 * Author: Chris Stephenson
 * later rewritten by Sean
 */

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

public class AnswerInPassage extends PassageScorer {
	@Override
	public double scorePassage(Question q, Answer a, Passage p)
	{
		return p.text.contains(a.candidate_text) ?
				1 : 0;
	}
}
