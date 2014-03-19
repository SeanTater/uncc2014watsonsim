/**
*
* @author Walid Shalaby
*/

package uncc2014watsonsim.scoring;

public class GoogleResultsScorer extends QuestionResultsScorer {
	public GoogleResultsScorer() {
		scorerModelPath = "data/scorer/models/google.model";
		scorerDatasetPath = "data/scorer/schemas/google-schema.arff";
	}
}
