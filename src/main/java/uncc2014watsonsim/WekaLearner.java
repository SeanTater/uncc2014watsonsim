package uncc2014watsonsim;

import uncc2014watsonsim.scoring.AllEnginesResultsScorer;
import uncc2014watsonsim.scoring.QuestionResultsScorer;

/** This is a temporary class made for Demo 3
 * It allows you to use the scorers in the scoring package without separating
 * the scores from the Answers yet.
 *
 */
public class WekaLearner extends Learner {
	
	public void test_implementation(Question question) throws Exception {
		QuestionResultsScorer q = new AllEnginesResultsScorer();
		q.initialize();
		for (Answer a: question) {
			Document indri = a.first("indri") != null ? a.first("indri") : new Document("indri", "","", null, 20, 0);
			Document lucene = a.first("lucene") != null? a.first("lucene") : new Document("lucene", "","", null, 20, 0);
			Document google = a.first("google") != null ? a.first("google") : new Document("google", "","", null, 20, 0);
			a.docs.add(new Document(
					"combined", // Engine name
					a.getTitle(), // Title
					a.getFullText(), // Full Text
					null, // Reference
					0, // Rank
					q.score(new double[]{
							indri.rank,
							indri.score,
							lucene.rank,
							lucene.score,
							google.rank}))); // Score
		}
		
	}

}
