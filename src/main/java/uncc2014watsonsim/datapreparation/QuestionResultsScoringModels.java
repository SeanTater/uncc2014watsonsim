/**
*
* @author Walid Shalaby
*/

package uncc2014watsonsim.datapreparation;

import uncc2014watsonsim.scoring.QuestionResultsScorer;


public class QuestionResultsScoringModels {
	
	public static void main(String[] args) {
		try {
			// build indri model
			QuestionResultsScorer.buildScorerModel("data/scorer/training/indri.arff", "data/scorer/training/indri.log", "data/scorer/models/indri.model", "correct", true);
			
			// build lucene model
			QuestionResultsScorer.buildScorerModel("data/scorer/training/lucene.arff", "data/scorer/training/lucene.log", "data/scorer/models/lucene.model", "correct", true);
			
			// build google model
			QuestionResultsScorer.buildScorerModel("data/scorer/training/google.arff", "data/scorer/training/google.log", "data/scorer/models/google.model", "correct", true);

			// build all engines model
			QuestionResultsScorer.buildScorerModel("data/scorer/training/allengines.arff", "data/scorer/training/allengines.log", "data/scorer/models/allengines.model", "correct", true);						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done!");		
	}
	
}

