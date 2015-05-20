package edu.uncc.cs.watsonsim.scorers;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.Tree;
import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;

/**
 * Simple hashed POS-tag, mod 100 and scaled to between 0 and 1.
 */
public class TopPOS extends AnswerScorer {
	private final Logger log = Logger.getLogger(getClass());

	public double scoreAnswer(Question q, Answer a) {
		for (Tree tree : a.getTrees()) {
			for (Tree child : tree.children()) {
				log.debug(a.text + " is a " + child.label().value() + " : " + (child.label().value().hashCode() % 100) / 100.0);
				return (child.label().value().hashCode() % 10) / 10.0;	
			}
		}
		return 0.0;
	}
}
