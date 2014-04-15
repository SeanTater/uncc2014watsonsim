package uncc2014watsonsim.research;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;


/**
 * @author Sean Gallagher
 */
public class PassageCount extends PassageScorer {
	public double scorePassage(Question q, Answer a, Passage p) {
		return a.passages.size();
	}
}

