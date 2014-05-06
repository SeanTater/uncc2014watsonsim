package uncc2014watsonsim.research;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;


/**
 * @author Sean Gallagher
 */
public class PassageCount extends AnswerScorer {
	@Override
	public double scoreAnswer(Question q, Answer a) {
		return a.passages.size();
	}
}

