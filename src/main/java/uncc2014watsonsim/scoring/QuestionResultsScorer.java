/**
*
* @author Walid Shalaby
* Inspired from wekaexamples/classifiers/WekawekaClassifier.java
*/

package uncc2014watsonsim.scoring;

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

import uncc2014watsonsim.Answer;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * @author wshalaby
 *
 */
public class QuestionResultsScorer {
	protected String scorerModelPath = "";
	protected String scorerDatasetPath = "";
	protected Classifier scorerModel = null;
	protected Instances qResultsDataset = null;
	
	public QuestionResultsScorer() {
		
	}
	public void initialize() throws ClassNotFoundException, IOException {
		LoadModel(scorerModelPath);
		qResultsDataset = new Instances(new BufferedReader(new FileReader(scorerDatasetPath)));
		qResultsDataset.setClassIndex(qResultsDataset.numAttributes()-1);		
	}
	public static QuestionResultsScorer prepareGenericScorer(String schemapath, String modelpath) {
		QuestionResultsScorer scorer = new QuestionResultsScorer();
		scorer.scorerModelPath = modelpath;
		scorer.scorerDatasetPath = schemapath;
		return scorer;
	}
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
		//String classifierName = "weka.classifiers.functions.Logistic";
		//String[] classifierOptions = new String[]{"-R", "1.0E-8", "-M", "-1"};
		String classifierName = "weka.classifiers.lazy.KStar";
		String[] classifierOptions = new String[]{"-B", "20", "-M", "a"};
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
