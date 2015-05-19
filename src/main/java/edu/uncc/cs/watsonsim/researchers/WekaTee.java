package edu.uncc.cs.watsonsim.researchers;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.Score;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.Saver;


/** Pipe Answer scores to an ARFF file for Weka */
public class WekaTee extends Researcher {
	private final static List<Score> dataset = new ArrayList<>();
	private static ArffSaver saver;
	private static int saved_schema_version = -1;
	
	
	// Make every run unique, but overwrite between questions
	// This way, you still get /something/ if you interrupt it
	private final Timestamp start_time;
	/**
	 * Dump the training data to an ARFF file marked by the given timestamp
	 * @param start_time
	 */
	public WekaTee(Timestamp start_time) {
		this.start_time = start_time;
	}

	@Override
	/**
	 * Multithreaded counterpart to dump, which is synchronized
	 */
	public List<Answer> question(Question q, List<Answer> answers) {
		List<Score> new_entries = new ArrayList<>();
		for (Answer a : answers) {
			new_entries.add(a.scores.clone());
		}
		
		
		dump(new_entries, start_time);
		return answers;
	}
	
	/** File-writing serialized counterpart to question()
	 * 
	 * @param new_entries	The new arrays to dump
	 * @param start_time	The timestamp of the file to dump to
	 */
	private static synchronized void dump(List<Score> new_entries, Timestamp start_time) {
		dataset.addAll(new_entries);
		
		Collection<String> names = Score.latestSchema();
		try {
			if (names.size() != saved_schema_version) {
				dump_from_scratch(names, start_time);
			} else {
				// Only do a few quick updates
				for (Score row : new_entries)
					saver.writeIncremental(new Instance(1.0, row.getEach(names)));
			}
			// There are synchronization issues otherwise.
			saver.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to write Weka Log!");
		}
	}
	
	/**
	 *  When the score changes, rewrite the file.
	 *  This is really rare in practice, so don't bother optimizing it.
	 */
	private static void dump_from_scratch(Collection<String> names, Timestamp start_time) throws IOException {
		saved_schema_version = names.size();
		
		FastVector attributes = new FastVector();
		// Answer score names
		for (String name: names)
			attributes.addElement(new Attribute(name));
		Instances data = new Instances("Watsonsim captured question stream", attributes, 0);
		
		// Save the results to a file
		saver = new ArffSaver();
		saver.setStructure(data);
		saver.setRetrieval(Saver.INCREMENTAL);
		saver.setFile(new File("data/weka-log." + start_time + ".arff"));
		for (Score row : dataset)
			saver.writeIncremental(new Instance(1.0, row.getEach(names)));
	}

}
