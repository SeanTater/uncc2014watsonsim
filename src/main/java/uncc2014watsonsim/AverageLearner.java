package uncc2014watsonsim;

//import org.apache.mahout.*;
public class AverageLearner extends Learner {
    /** Correlates search results for improved accuracy */
	public AverageLearner() {}
	
	@Override
	public void test_implementation(Question question) {
		for (ResultSet result : question) {
			double score = 0;
			int count = 0;
			for (Engine engine : result.engines) {
				score += engine.score;
				count++;
			}
			// Average and scale (to make the resulting confidence more realistic)
			score /= count;
			// Logistic function
			score = logistic(score);
			result.engines.add(new Engine("combined", count, score));
		}
	}
	
	static double logistic(double score) {
		return score = 1.0/(1.0+Math.exp(-score));
	}
}
