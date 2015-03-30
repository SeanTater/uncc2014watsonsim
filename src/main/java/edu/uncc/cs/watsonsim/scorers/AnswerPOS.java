package edu.uncc.cs.watsonsim.scorers;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;

/**
 * 
 * @author Yeshvant
 *
 */
public class AnswerPOS extends AnswerScorer {

	public AnswerPOS() {
	}

	public double scoreAnswer(Question q, Answer a) {
		
		for (SemanticGraph graph : a.graphs) {

			if(!graph.getRoots().isEmpty())
			{
			if (graph.getFirstRoot().tag().contains("NN")) {
				for (SemanticGraphEdge edge : graph.edgeIterable()) {

					IndexedWord a1 = edge.getDependent();
					IndexedWord a2 = edge.getGovernor();

					if (a1.tag().contains("NN")) {
						return 1.0;
					}
					if (a2.tag().contains("NN")) {
						return 1.0;
					}

				}

			}
		   }
		  }
		return 0.0;
		}

		

	public static void main(String args[]) {
		Answer a = new Answer("For luck Kate will only knock on this wood");
		// System.err.println(a.graphs.size());
		// System.out.println("hello");
		double score = 0;
		for (SemanticGraph graph : a.graphs) {

			if (graph.getFirstRoot().tag().contains("NN")) {
				for (SemanticGraphEdge edge : graph.edgeIterable()) {

					 GrammaticalRelation rel = edge.getRelation(); 
					IndexedWord a1 = edge.getDependent();
					IndexedWord a2 = edge.getGovernor();

					// System.out.println(a1.originalText()+"Tag: "+a1.tag());
					// System.out.println(a2.originalText()+" Tag: "+a2.tag()+" "+rel.getShortName()+" Relation to "+a1.originalText()+" Tag: "+a1.tag());
					if (a1.tag().contains("NN")) {
						score = 1.0;
						// return

					}
					if (a2.tag().contains("NN")) {
						score = 1.0;
						// return

					}

				}

			}
		}

	}

}
