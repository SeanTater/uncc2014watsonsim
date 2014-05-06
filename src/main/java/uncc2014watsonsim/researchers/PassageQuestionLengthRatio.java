package uncc2014watsonsim.researchers;

/* 
 * @author Wlodek
 */

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.scorers.PassageScorer;

public class PassageQuestionLengthRatio extends PassageScorer {
	
	public double scorePassage(Question q, Answer a, Passage p) {
		String qs = q.getRaw_text();
		//String qst= q.text; //processes question, stopwords, punctuation removed
		//String as= a.candidate_text;
		//String ps=p.text; // text is guaranteed to have content
	    //ps.tokenize();
		
		int pl = p.getText().length();
		int ql = qs.length();
		double sc=pl/ql;
		return sc;
	}

}
