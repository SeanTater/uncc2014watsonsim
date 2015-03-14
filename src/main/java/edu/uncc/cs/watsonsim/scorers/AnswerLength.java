package edu.uncc.cs.watsonsim.scorers;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;

/**
 * Return the length of the candidate text in chars.
 * @author Sean Gallagher
 */
public class AnswerLength extends AnswerScorer {
	
	public double scoreAnswer(Question q, Answer a) {
		return a.candidate_text.length();
	}

}
