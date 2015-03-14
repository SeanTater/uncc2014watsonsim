package edu.uncc.cs.watsonsim.scorers;
import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;


/**
 * @author Sean Gallagher
 */
public class PassageCount extends AnswerScorer {
	public double scoreAnswer(Question q, Answer a) {
		return a.passages.size();
	}
}

