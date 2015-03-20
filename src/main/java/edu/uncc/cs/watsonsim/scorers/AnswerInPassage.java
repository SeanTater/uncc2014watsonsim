package edu.uncc.cs.watsonsim.scorers;

/*
 * Author: Chris Stephenson
 * later rewritten by Sean
 */

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Question;

public class AnswerInPassage extends PassageScorer {
	@Override
	public double scorePassage(Question q, Answer a, Passage p)
	{
		return p.text.contains(a.text) ?
				1 : 0;
	}
}
