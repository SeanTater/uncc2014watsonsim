/**
*
* @author Walid Shalaby
*/ 

package uncc2014watsonsim.research;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

public class WShalabyScorer extends Scorer {

	@Override
	/** Detect if the question matches the answer, score it appropriately
	 * This is to ease machine learning*/
	// TODO: Don't reassign for every passage
	public double passage(Question q, Answer a, Passage p) {
		return 0.0;
	}
}
