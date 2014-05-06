/**
*
* @author Walid Shalaby
*/

package uncc2014watsonsim.datapreparation;

import uncc2014watsonsim.researchers.CombineScores;


public class QuestionResultsScoringModels {
	
	public static void main(String[] args) {
		try {
			// build all engines model
			CombineScores.buildScorerModel("data/scorer/training/allengines.arff", "data/scorer/training/allengines.log", "data/scorer/models/allengines.model", "correct", true);						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done!");		
	}
	
}

