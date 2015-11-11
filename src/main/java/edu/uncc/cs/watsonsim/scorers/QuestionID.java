package edu.uncc.cs.watsonsim.scorers;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;

/**
 * A bogus scorer whose purpose is to collate answers to the same question
 * @author Sean Gallagher
 */
public class QuestionID extends AnswerScorer {

	@Override
	public double scoreAnswer(Question q, Answer a) {
		return q.text.hashCode();
	}

}
