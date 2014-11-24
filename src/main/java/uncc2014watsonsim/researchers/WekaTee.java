package uncc2014watsonsim.researchers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;
import uncc2014watsonsim.scorers.Scored;
import uncc2014watsonsim.scorers.QScore;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;


/** Pipe Answer scores to an ARFF file for Weka */
public class WekaTee {
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
	
	public void question(QScore q) {
		// TODO: Maybe this should use (Score.answer_score_names) again
		for (Scored a : q.values()) {
			data.add(new Instance(1.0, Scored.orderedScores(a)));
		}

		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		try {
			saver.setFile(new File("data/weka-log.arff"));
			saver.writeBatch();
		} catch (IOException e) {
			System.out.println("Failed to write Weka log. Ignoring.");
			e.printStackTrace();
		}
	}

}
