package uncc2014watsonsim.scorers;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;

/**
 * Returns 1.0 if the answer text is found in the question and 0.0 otherwise
 * @author Ken Overholt
 *
 */
public class AnswerInQuestionScorer extends AnswerScorer {
	
	@Override
	public double scoreAnswer(Question q, Answer a) {
		String qtext = q.getRaw_text().toLowerCase();
		String atext = a.candidate_text.toLowerCase();
		
		if (qtext.contains(atext))
			return 1.0;
		else
			return 0.0;		
	}
	
}
