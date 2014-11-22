package uncc2014watsonsim.researchers;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.scorers.AnswerScorer;


/**
 * @author Sean Gallagher
 */
public class PassageCount extends AnswerScorer {
	public double scoreAnswer(Question q, Answer a) {
		return a.direct_passages.size();
	}
}

