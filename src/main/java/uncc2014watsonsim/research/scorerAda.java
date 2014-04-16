package uncc2014watsonsim.research;

/* 
 * @author Adarsh
 */

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

public class passagescorer extends Scorer {

	public double passage(Question q, Answer a, Passage p) {
		String qs = q.getRaw_text();
		String qst= q.text; //processes question, stopwords, punctuation removed
		String as= a.candidate_text;
		String ps=p.text; // text is guaranteed to have content
	//	ps.tokenize();


		int pl = p.text.length();
		int al = as.length();
		double sc=pl/al;
		return sc;
		
		//return Double.NaN; // has to return a Double
	}

}
