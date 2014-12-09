package uncc2014watsonsim.researchers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
	private Instances data;
	
	public WekaTee() {
		FastVector attributes = new FastVector();
		// Answer score names
		for (String name : Score.answer_score_names)
			attributes.addElement(new Attribute(name));
		// Passage score names
		for (int passage_i=0; passage_i<Score.MAX_PASSAGE_COUNT; passage_i++)
			for (String name : Score.passage_score_names)
				attributes.addElement(new Attribute(name + "_" + passage_i));
		data = new Instances("Watsonsim captured question stream", attributes, 0);
	}
	
	@Override
	public void research(Question q) {
		question(q);
	}

	@Override
	public void question(Question q) {
		
		for (Answer a : q) {
			data.add(new Instance(1.0, a.scoresArray(Score.answer_score_names)));
		}
	}
	
	@Override
	public void complete() {
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		try {
			
			saver.setFile(new File("data/weka-log." + new Date() + ".arff"));
			saver.writeBatch();
		} catch (IOException e) {
			System.out.println("Failed to write Weka log. Ignoring.");
			e.printStackTrace();
		}
	}

}
