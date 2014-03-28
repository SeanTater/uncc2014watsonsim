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
			double[] scores = {20.0, -15.0, 20.0, -1.0, 20.0, 55.0, 10.0};
			for (Score name : a.scores.keySet())
				scores[name.ordinal()] = a.scores.get(name);
			a.scores.put(Score.COMBINED, q.score(scores));
		}
		
	}

}
