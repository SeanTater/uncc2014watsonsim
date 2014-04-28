package uncc2014watsonsim.research;

//Not sure if this is exactly what we were suppose to do 
//also not sure if it actually will work the way its needed to 

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

/**
 * @author Irene Pate
 *
 */

public class IreneScorer extends PassageScorer 
{
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
