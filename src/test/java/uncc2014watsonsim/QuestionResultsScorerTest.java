/**
*
* @author Walid Shalaby
*/

package uncc2014watsonsim;
import uncc2014watsonsim.researchers.CombineScores;
import uncc2014watsonsim.scorers.AllEnginesResultsScorer;


public class QuestionResultsScorerTest {
	
	public static void main(String[] args) {
		try {
			CombineScores q = new AllEnginesResultsScorer();
			q.initialize();
			System.out.println("scoring: {indri-rank=1, indri-score=-1.582, lucene-rank=1, lucene-score=7.215, google-rank=1} ==> " + 
					q.score(new double[]{1,-1.582,1,7.215,1}));			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
