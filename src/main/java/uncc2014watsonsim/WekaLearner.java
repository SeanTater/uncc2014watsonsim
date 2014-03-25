package uncc2014watsonsim;

import uncc2014watsonsim.scoring.AllEnginesResultsScorer;
import uncc2014watsonsim.scoring.QuestionResultsScorer;

/** Combines scores using machine learning from Weka */
public class WekaLearner extends Learner {
	private final String[] names = {"indri_rank", "indri_score", "lucene_rank", "lucene_score", "google_rank"};
	
	public void test_implementation(Question question) throws Exception {
		QuestionResultsScorer q = new AllEnginesResultsScorer();
		q.initialize();
		
		for (Answer a: question) {
			double[] scores = {20.0, -15.0, 20.0, -1.0, 20.0};
			
			for (int i=0; i<names.length; i++) {
				if (a.scores.containsKey(names[i])) {
					scores[i] = a.scores.get(names[i]);
				}
			}
			
			a.scores.put("combined", q.score(scores));
		}
		
	}

}
