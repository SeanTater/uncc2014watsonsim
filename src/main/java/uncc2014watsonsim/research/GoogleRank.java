package uncc2014watsonsim.research;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.research.PassageScorer;

/**
 * This scorer is an echo. It is here to take advantage of the dimensionality
 * reduction that comes implicitly as part of scorers.
 * @author Sean
 */
public class GoogleRank extends PassageScorer {
	public double scorePassage(Question q, Answer a, Passage p) {
		return p.score("Google_RANK");
	}
}
