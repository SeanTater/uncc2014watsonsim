/**
	*
	* @author Walid Shalaby
	*/

package uncc2014watsonsim.scoring;

public class AllEnginesResultsScorer extends QuestionResultsScorer {	
	public AllEnginesResultsScorer() {
		scorerModelPath = "data/scorer/models/allengines.model";
		scorerDatasetPath = "data/scorer/schemas/allengines-schema.arff";
	}
}
