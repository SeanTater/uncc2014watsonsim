package edu.uncc.cs.watsonsim.scorers;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.scorers.AnswerScorer;

/**
 * Return how many unique LAT's there are for an answer. 
 * @author Sean
 *
 */
public class LATMentions extends AnswerScorer {
	
	@Override
	public double scoreAnswer(Question q, Answer a) {
		return a.lexical_types.size();
	}
}
