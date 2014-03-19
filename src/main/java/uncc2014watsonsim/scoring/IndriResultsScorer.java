/**
*
* @author Walid Shalaby
*/

package uncc2014watsonsim.scoring;

public class IndriResultsScorer extends QuestionResultsScorer {
	public IndriResultsScorer() {
		scorerModelPath = "data/scorer/models/indri.model";
		scorerDatasetPath = "data/scorer/schemas/indri-schema.arff";
	}
}
