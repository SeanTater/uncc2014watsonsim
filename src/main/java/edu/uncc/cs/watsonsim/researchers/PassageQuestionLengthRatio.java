package edu.uncc.cs.watsonsim.researchers;

/* 
 * @author Wlodek
 */

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.scorers.PassageScorer;

public class PassageQuestionLengthRatio extends PassageScorer {
	
	public double scorePassage(Question q, Answer a, Passage p) {
		String qs = q.getRaw_text();
		//String qst= q.text; //processes question, stopwords, punctuation removed
		//String as= a.candidate_text;
		//String ps=p.text; // text is guaranteed to have content
	    //ps.tokenize();
		
		int pl = p.text.length();
		int ql = qs.length();
		double sc=pl/ql;
		return sc;
	}

}
