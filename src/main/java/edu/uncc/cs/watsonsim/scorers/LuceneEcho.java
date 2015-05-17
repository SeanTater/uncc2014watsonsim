package edu.uncc.cs.watsonsim.scorers;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.Score;

/**
 * Take advantage of the Scorer dimension reduction for Lucene passages
 */
public class LuceneEcho extends PassageScorer {

	@Override
	public double scorePassage(Phrase q, Answer a, Passage p) {
		return Score.get(p.scores, "LUCENE_SCORE", -1);
	}
	
}
