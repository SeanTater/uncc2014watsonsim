
//Not sure if this is exactly what we were suppose to do 
//also not sure if it actually will work the way its needed to 

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;

/**
 * @author Irene Pate
 *
 */

public class aScoreerWZ extends Scoreer 
{
	public double (passageIquestion q, Answer a, Passage p)
		{
			String qs = q.getRaw text():
			String qst = q.text;
			String as = a.candidate_text;
			String ps=p.text;

			int score;
			int sc1 = score1;
			int sc2 = score2;

			if (sc1 > sc2)
			{
				return -1;
			}
				else if (sc1< sc2)
			{
				reutrn +1;
			}
				else 
			{
				return 0;
			}

			int pl = p.text length;
			int ql = qs.length;
			double sc=pl/ql;
			return sc; 
		}
}
