/**
*
* @author Walid Shalaby
* Inspired from wekaexamples/classifiers/WekawekaClassifier.java
*/

package edu.uncc.cs.watsonsim.researchers;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.Score;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * Combine the scores coming in from many scorers, in order to generate a
 * single combined score in the end.
 * 
 * Sorts and reverses the result, so that the top answer is at rank 0.
 * 
 * As of April 23, the best SVM settings at are at C=16 gamma=0.1
 * 
 * @author Walid Shalaby
 */
public class CombineScores extends Researcher {
	static String scorerModelPath = "data/scorer/models/allengines.model";
	static String scorerDatasetPath = "data/scorer/schemas/allengines-01-schema.arff";
	Classifier scorerModel = null;
	Instances qResultsDataset = null;
	List<String> names = new ArrayList<>();
	
	public CombineScores() {
		try {
			LoadModel(scorerModelPath);
			qResultsDataset = new Instances(new BufferedReader(new FileReader(scorerDatasetPath)));
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
		

		// Get the attribute's names as a string
		@SuppressWarnings("unchecked")
		List<Attribute> attributes = Collections.list((Enumeration<Attribute>) qResultsDataset.enumerateAttributes());
		for (Attribute a : attributes)
			names.add(a.name());
		
		// Only decide the class attribute afterward because otherwise weka
		// will cut it out and the length will not match
		qResultsDataset.setClassIndex(qResultsDataset.attribute("CORRECT").index());
		
	}
	
	@Override
	public List<Answer> question(Question question, List<Answer> answers) {
		for (Answer a: answers) {
			try {
				a.setOverallScore(score(Score.getEach(a.scores, names)));
			} catch (Exception e) {
				System.out.println("An unknown error occured while scoring with Weka. Some results may be scored wrong.");
				e.printStackTrace();
				a.setOverallScore(0.0);
			}
		}

		Collections.sort(answers);
		Collections.reverse(answers);
		return answers;
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
		return scorerModel.distributionForInstance(inst)[1];
		//return scorerModel.classifyInstance(inst);
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
		String classifierName = "weka.classifiers.functions.MultilayerPerceptron";
		String[] classifierOptions = new String[]{"-L", "0.3", "-M", "0.2", "-N", "500", "-V", "0", "-S", "0", "-E", "20", "-H", "a"};
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
	
	/** Train the classifier 
	 * @throws Exception */
	public static void main(String[] arguments) throws Exception {
		buildScorerModel("data/weka-log.arff", "data/model.log", scorerModelPath, "CORRECT", false);
	}
}
