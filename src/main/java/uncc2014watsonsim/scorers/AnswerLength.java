package uncc2014watsonsim.scorers;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;

/**
 * Return the length of the candidate text in chars.
 * @author Sean Gallagher
 */
public class AnswerLength extends AnswerScorer {
	
	public double scoreAnswer(Question q, Answer a) {
		return a.candidate_text.length();
	}

}
