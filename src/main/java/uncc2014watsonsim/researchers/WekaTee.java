package uncc2014watsonsim.researchers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Collections;
import java.util.Vector;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.Saver;


/** Pipe Answer scores to an ARFF file for Weka */
public class WekaTee extends Researcher {
	private List<double[]> dataset = new ArrayList<>();
	private ArffSaver saver;
	private int saved_schema_version = -1;
	
	
	// Make every run unique, but overwrite between questions
	// This way, you still get /something/ if you interrupt it
	private Date start_time = new Date();
	
	@Override
	public void research(Question q) {
		question(q);
	}

	@Override
	public synchronized void question(Question q) {
		List<double[]> new_entries = new ArrayList<>();
		for (Answer a : q)
			new_entries.add(a.scores.clone());
		dataset.addAll(new_entries);
		
		// When the score changes, rewrite the file.
		String[] current_schema = Score.latestSchema();
		try {
			if (current_schema.length != saved_schema_version) {
				saved_schema_version = current_schema.length;
				
				FastVector attributes = new FastVector();
				// Answer score names
				for (String name: current_schema)
					attributes.addElement(new Attribute(name));
				Instances data = new Instances("Watsonsim captured question stream", attributes, 0);
				
				// Save the results to a file
				saver = new ArffSaver();
				saver.setStructure(data);
				saver.setRetrieval(Saver.INCREMENTAL);
				saver.setFile(new File("data/weka-log." + start_time + ".arff"));
				for (double[] row : dataset)
					saver.writeIncremental(new Instance(1.0, Score.update(row.clone())));
			} else {
				// Only do a few quick updates
				for (double[] row : new_entries)
					saver.writeIncremental(new Instance(1.0, Score.update(row.clone())));
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to write Weka Log!");
		}

	}

}
