package uncc2014watsonsim;

//import org.apache.mahout.*;
public class AverageLearner extends Learner {
    /** Correlates search results for improved accuracy */
	public AverageLearner() {}
	
	@Override
	public void test_implementation(Question question) {
		for (Answer result : question) {
			double score = 0;
			int count = 0;
			for (String engine : result.scores.keySet()) {
				score += result.scores.get(engine);
				count++;
			}
			// Average and scale (to make the resulting confidence more realistic)
			score /= count;
			// Logistic function
			score = logistic(score);
			result.scores.put("combined", score);
		}
	}
	
	static double logistic(double score) {
		return score = 1.0/(1.0+Math.exp(-score));
	}
}
