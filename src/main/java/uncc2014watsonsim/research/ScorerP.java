package uncc2014watsonsim.research;

/*
 * Author: Pavan Kumar
 */

import uncc2014watsonsim.Question;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;


public class ScorerP extends PassageScorer {
	@Override
	public double scorePassage(Question q, Answer a, Passage p)
	{
		String pt = p.getText();
		String qt = q.getRaw_text();
			
		int l1 = pt.length();
		int l2 = qt.length();
		double score = (double)l1/l2;
		return score; 
	}
}
