package edu.uncc.cs.watsonsim.scorers;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;

/**
 * 
 * @author Yeshvant
 *
 */
public class AnswerPOSTagger extends AnswerScorer {

	public AnswerPOSTagger() {
	}
	public double scoreAnswer(Question q, Answer a) {
		String qtext = q.text.toLowerCase();
		String atext = a.text.toLowerCase();
		
		if (qtext.contains(atext))
			return 1.0;
		else
			return 0.0;		
	}
	
}
