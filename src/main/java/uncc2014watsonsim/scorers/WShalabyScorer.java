/**
*
* @author Walid Shalaby
*/ 

package uncc2014watsonsim.scorers;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

public class WShalabyScorer extends PassageScorer {

	@Override
	/** Detect if the question matches the answer, score it appropriately
	 * This is to ease machine learning*/
	// TODO: Don't reassign for every passage
	public double scorePassage(Question q, Answer a, Passage p) {
		return 0.0;
	}
}
