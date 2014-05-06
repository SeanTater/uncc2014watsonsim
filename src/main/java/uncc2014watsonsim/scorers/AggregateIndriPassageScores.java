package uncc2014watsonsim.scorers;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.researchers.Researcher;


/**
*
* @author walid shalaby (adapted from GenerateSearchResultDataset)
*/
public class AggregateIndriPassageScores extends AnswerScorer {
	@Override
	/**
	 * return aggregate score for all the passages
	 * 
	 * */
	public double scoreAnswer(Question q, Answer a) {
		double score = 0;
		int count = a.passages.size();
		if(count>0) {
			for(Passage p : a.passages) {
				score += p.score("INDRI_SCORE");
			}
			score = score/count;
		}
		
		return score;
	}
}
