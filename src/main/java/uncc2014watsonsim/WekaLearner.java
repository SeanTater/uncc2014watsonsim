package uncc2014watsonsim;

import uncc2014watsonsim.scoring.AllEnginesResultsScorer;
import uncc2014watsonsim.scoring.QuestionResultsScorer;
import uncc2014watsonsim.Score;

/** Combines scores using machine learning from Weka */
public class WekaLearner extends Learner {
	
	public void test_implementation(Question question) throws Exception {
		QuestionResultsScorer q = new AllEnginesResultsScorer();
		q.initialize();
		
		for (Answer a: question) {
			a.scores.put(Score.COMBINED, q.score(a.scoresArray()));
		}
		
	}

}
