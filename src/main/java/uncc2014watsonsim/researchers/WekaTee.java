package uncc2014watsonsim.researchers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;


/** Pipe Answer scores to an ARFF file for Weka */
public class WekaTee extends Researcher {
	
	// This is the sceme we will use for exporting
	// Note it's inherently sorted
	Set<String> schema = new TreeSet<>();
	// But in memory there is no schema because it changes
	List<Map<String, Double>> dataset = new ArrayList<>();
	
	
	// Make every run unique, but overwrite between questions
	// This way, you still get /something/ if you interrupt it
	private Date start_time = new Date();
	
	@Override
	public void research(Question q) {
		question(q);
	}

	@Override
	public void question(Question q) {

		// Update the schema and append the data
		for (Answer a : q) {
			schema.addAll(a.scores.keySet());
			Map<String, Double> answer_map = new HashMap<>();
			answer_map.putAll(a.scores);
			dataset.add(answer_map);
		}
		
		exportToFile();
	}
	
	/**
	 * Export the current dataset to an Arff file
	 */
	private void exportToFile() {
		FastVector attributes = new FastVector();
		// Answer score names
		for (String name: schema)
			attributes.addElement(new Attribute(name));
		Instances data = new Instances("Watsonsim captured question stream", attributes, 0);
		
		// Fill in all the rows in sorted order, then export.
		int schema_len = schema.size();
		for (Map<String, Double> dataset_row : dataset) {
			double[] row = new double[schema_len];
			Arrays.fill(row, Double.NaN);
			int col_idx = 0;
			for (String column : schema) {
				Double value = dataset_row.get(column);
				row[col_idx++] = value == null ? Double.NaN : value;
			}
			data.add(new Instance(1.0, row));
		}
		
		// Save the results to a file
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		try {
			saver.setFile(new File("data/weka-log." + start_time + Thread.currentThread().getId() + ".arff"));
			saver.writeBatch();
		} catch (IOException e) {
			System.out.println("Failed to write Weka log. Ignoring.");
			e.printStackTrace();
		}
	}

}
