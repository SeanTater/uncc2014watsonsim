package uncc2014watsonsim.scorers;

/*
 * Author: Chris Stephenson
 */

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

public class StephensonScorer extends PassageScorer {
	@Override
	public double scorePassage(Question q, Answer a, Passage p)
	{
		String qs = q.getRaw_text();
		String qst = q.text;
		String as = a.candidate_text;
		String ps=p.getText();

		int pl = ps.length();
		int ql = qs.length();
		double sc=pl/ql;
		return sc; 
	}
}
