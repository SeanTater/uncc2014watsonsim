/**
*
* @author Walid Shalaby
*/

package uncc2014watsonsim.scoring;

public class LuceneResultsScorer extends QuestionResultsScorer {
	public LuceneResultsScorer() {
		scorerModelPath = "data/scorer/models/lucene.model";
		scorerDatasetPath = "data/scorer/schemas/lucene-schema.arff";
	}
}
