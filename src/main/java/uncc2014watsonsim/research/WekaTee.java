package uncc2014watsonsim.research;

import java.io.File;
import java.io.IOException;

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
		for (String name : Answer.scoreNames())
			attributes.addElement(new Attribute(name));
		data = new Instances("Watsonsim captured question stream", attributes, 0);
	}
	
	@Override
	public void research(Question q) {
		question(q);
	}

	@Override
	public void question(Question q) {
		for (Answer a : q) {
			data.add(new Instance(1.0, a.scoresArray()));
		}
	}
	
	@Override
	public void complete() {
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
