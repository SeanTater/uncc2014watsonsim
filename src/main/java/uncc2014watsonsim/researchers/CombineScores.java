/**
*
* @author Walid Shalaby
* Inspired from wekaexamples/classifiers/WekawekaClassifier.java
*/

package uncc2014watsonsim.researchers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * Combine the scores coming in from many scorers, in order to generate a
 * single combined score in the end.
 * 
 * Sorts and reverses the result, so that the top answer is at rank 0.
 * 
 * @author Walid Shalaby
 */
public class CombineScores extends Researcher {
	// This is the question set intended for all engines
	String scorerModelPath = "data/scorer/models/allengines.model";
	String scorerDatasetPath = "data/scorer/schemas/allengines-01-schema.arff";
	/* There are also models for several specific groups.
	 * Google:
	 * scorerModelPath = "data/scorer/models/google.model";
	 * scorerDatasetPath = "data/scorer/schemas/google-schema.arff";
	 * 
	 * Indri:
	 * scorerModelPath = "data/scorer/models/indri.model";
	 * scorerDatasetPath = "data/scorer/schemas/indri-schema.arff";
	 * 
	 * Lucene:
	 * scorerModelPath = "data/scorer/models/lucene.model";
	 * scorerDatasetPath = "data/scorer/schemas/lucene-schema.arff";
	 */
	Classifier scorerModel = null;
	Instances qResultsDataset = null;
	
	public CombineScores() {
		try {
			LoadModel(scorerModelPath);
			qResultsDataset = new Instances(new BufferedReader(new FileReader(scorerDatasetPath)));
			qResultsDataset.setClassIndex(qResultsDataset.numAttributes()-1);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Weka learners are missing. "
					+ "Did you install Weka correctly?");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Weka models appear to be missing. "
					+ "Do you have data/scorers? It is not possible to run "
					+ "without them.");
		}
		
	}
	
	@Override
	public void question(Question question) {
		List<String> MODEL_ANSWER_DIMENSIONS = Arrays.asList(new String[]{"BING_RANK_MAX", "BING_RANK_MEAN", "BING_RANK_MEDIAN", "BING_RANK_MIN", "CORRECT", "FITB_EXACT_MATCH_SCORE", "GOOGLE_RANK_MAX", "GOOGLE_RANK_MEAN", "GOOGLE_RANK_MEDIAN", "GOOGLE_RANK_MIN", "INDRI_ANSWER_RANK", "INDRI_ANSWER_SCORE", "INDRI_RANK_MAX", "INDRI_RANK_MEAN", "INDRI_RANK_MEDIAN", "INDRI_RANK_MIN", "INDRI_SCORE_MAX", "INDRI_SCORE_MEAN", "INDRI_SCORE_MEDIAN", "INDRI_SCORE_MIN", "LATTYPE_MATCH_SCORER", "LUCENE_ANSWER_RANK", "LUCENE_ANSWER_SCORE", "LUCENE_RANK_MAX", "LUCENE_RANK_MEAN", "LUCENE_RANK_MEDIAN", "LUCENE_RANK_MIN", "LUCENE_SCORE_MAX", "LUCENE_SCORE_MEAN", "LUCENE_SCORE_MEDIAN", "LUCENE_SCORE_MIN", "NGRAM_MAX", "NGRAM_MEAN", "NGRAM_MEDIAN", "NGRAM_MIN", "PASSAGE_COUNT", "PASSAGE_QUESTION_LENGTH_RATIO_MAX", "PASSAGE_QUESTION_LENGTH_RATIO_MEAN", "PASSAGE_QUESTION_LENGTH_RATIO_MEDIAN", "PASSAGE_QUESTION_LENGTH_RATIO_MIN", "PASSAGE_TERM_MATCH_MAX", "PASSAGE_TERM_MATCH_MEAN", "PASSAGE_TERM_MATCH_MEDIAN", "PASSAGE_TERM_MATCH_MIN", "PERCENT_FILTERED_WORDS_IN_COMMON_MAX", "PERCENT_FILTERED_WORDS_IN_COMMON_MEAN", "PERCENT_FILTERED_WORDS_IN_COMMON_MEDIAN", "PERCENT_FILTERED_WORDS_IN_COMMON_MIN", "QUESTION_IN_PASSAGE_SCORER_MAX", "QUESTION_IN_PASSAGE_SCORER_MEAN", "QUESTION_IN_PASSAGE_SCORER_MEDIAN", "QUESTION_IN_PASSAGE_SCORER_MIN", "SKIP_BIGRAM_MAX", "SKIP_BIGRAM_MEAN", "SKIP_BIGRAM_MEDIAN", "SKIP_BIGRAM_MIN", "WORD_PROXIMITY_MAX", "WORD_PROXIMITY_MEAN", "WORD_PROXIMITY_MEDIAN", "WORD_PROXIMITY_MIN", "WPPAGE_VIEWS"});
		
		for (Answer a: question) {
			try {
				a.scores.put("COMBINED", score(a.scoresArray(MODEL_ANSWER_DIMENSIONS)));
			} catch (Exception e) {
				System.out.println("An unknown error occured while scoring with Weka. Some results may be scored wrong.");
				e.printStackTrace();
				a.scores.put("COMBINED", 0.0);
			}
		}

		Collections.sort(question);
		//Collections.reverse(question);
	}
	
	/*public static QuestionResultsScorer prepareGenericScorer(String schemapath, String modelpath) {
		QuestionResultsScorer scorer = new QuestionResultsScorer();
		scorer.scorerModelPath = modelpath;
		scorer.scorerDatasetPath = schemapath;
		return scorer;
	}*/
	
	public void LoadModel(String modelpath) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelpath));
		scorerModel = (Classifier) ois.readObject();
		ois.close();
	}
	/**
	 * @param attributesValues: one or more attributes used to score the result e.g., indri rank  
	 * @throws Exception 
	 */	
	public double score(double[] attributesValues) throws Exception {
		Instance inst = new Instance(1, attributesValues);
		inst.setDataset(qResultsDataset);
		//System.out.println(qResultsDataset.classAttribute().value((int)scorerModel.classifyInstance(inst)));
		//double[] ds = scorerModel.distributionForInstance(inst);
		return scorerModel.classifyInstance(inst);
	}
	/**
	 * @param inputpath: path of arff file containing results training instances
	 * @param outputpath: path to write training statistics
	 * @param modelpath: path to write trained model
	 * @param targetAttributeName: attribute name of target e.g., "correct" 
	 * @param doEvaluate: perform evaluation of model after training  
	 * @throws Exception 
	 */
	public static void buildScorerModel(String inputpath, String outputpath, String modelpath, 
			String targetAttributeName, boolean doEvaluate) throws Exception {
		// use logistic regression as default classifier
		//String classifierName = "weka.classifiers.functions.SimpleLogistic";
		//String[] classifierOptions = new String[]{"-I", "0", "-M", "500", "-H", "100", "-W", "0.0"};
		String classifierName = "weka.classifiers.functions.Logistic";
		String[] classifierOptions = new String[]{"-R", "1.0E-8", "-M", "-1"};
		//String classifierName = "weka.classifiers.lazy.KStar";
		//String[] classifierOptions = new String[]{"-B", "20", "-M", "a"};
		buildScorerModel(inputpath, outputpath, modelpath, classifierName, classifierOptions, 
				targetAttributeName, doEvaluate);
	}
	/**
	 * @param inputpath: path of arff file containing results training instances
	 * @param outputpath: path to write training statistics
	 * @param modelpath: path to write trained model
	 * @param classifierName: Weka classifier class name
	 * @param classifierOptions: parameters of classifier 
	 * @param targetAttributeName: attribute name of target e.g., "correct" 
	 * @param doEvaluate: perform 10 fold cross-validation evaluation of model after training  
	 * @throws Exception 
	 */
	public static void buildScorerModel(String inputpath, String outputpath, String modelpath, 
			String classifierName, String[] classifierOptions, String targetAttributeName, boolean doEvaluate) throws Exception {
		// initialize the classifier
		Classifier classifier = Classifier.forName(classifierName, classifierOptions);
		try {
			
			// load training instances
			Instances qResults = new Instances(new BufferedReader(new FileReader(inputpath)));
			
			// set target attribute
			qResults.setClass(qResults.attribute(targetAttributeName));
			
			// build classifier
			classifier.buildClassifier(qResults);
			Evaluation evaluation = null;
			if(doEvaluate) {
				// 10fold CV with seed=1
				evaluation = new Evaluation(qResults);
			    evaluation.crossValidateModel(classifier, qResults, 10, qResults.getRandomNumberGenerator(1));
			}
			
			// Write training statistics to output file
			writeStatistics(classifier, qResults, evaluation, outputpath);
			
			// write model
			writeModel(classifier, modelpath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void writeModel(Classifier classifier, String modelpath) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelpath));
	    oos.writeObject(classifier);
	    oos.close();
	}
	/**
	 * @param classifier
	 * @param qResults 
	 * @param evaluation
	 * @param outputpath 
	 * @throws IOException 
	 */
	private static void writeStatistics(Classifier classifier, Instances qResults, Evaluation evaluation, 
			String outputpath) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputpath)));
		writer.append("Classifier...: " + classifier.getClass().getName() + " " 
				+ Utils.joinOptions(classifier.getOptions()) + "\n");
		writer.append("Relation: " + qResults.relationName() + "\n");
		writer.append("Instances: " + qResults.numInstances() + "\n");
		writer.append("Attributes: " + qResults.numAttributes() + "\n");
		    if(qResults.numAttributes()<=100) {
		    	for(int i=0; i<qResults.numAttributes(); i++)
		    		writer.append("            " + qResults.attribute(i).name() + "\n");
		    }
		    writer.append("\n\n");
		    
		    // model weights
		    writer.append(classifier.toString() + "\n");
		    
		if(evaluation!=null) {
			// some statistics
			writer.append(evaluation.toSummaryString() + "\n");
		    try {
		    	// per class statistics
		    	writer.append(evaluation.toClassDetailsString() + "\n");
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		    }
		    try {
		    	// confusion matrix
		    	writer.append(evaluation.toMatrixString() + "\n");
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		    }		    
		}
		writer.close();
	}
}
