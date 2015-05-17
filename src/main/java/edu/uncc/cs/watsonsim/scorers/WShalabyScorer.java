/**
*
* @author Walid Shalaby
*/ 

package edu.uncc.cs.watsonsim.scorers;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;

public class WShalabyScorer extends PassageScorer {

	@Override
	/** Detect if the question matches the answer, score it appropriately
	 * This is to ease machine learning*/
	// TODO: Don't reassign for every passage
	public double scorePassage(Phrase q, Answer a, Passage p) {
		return 0.0;
	}
}
