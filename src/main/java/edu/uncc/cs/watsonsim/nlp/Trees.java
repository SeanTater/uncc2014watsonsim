package edu.uncc.cs.watsonsim.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
//import edu.stanford.nlp.util.*;

/* @author Wlodek
 * @author Sean Gallagher
 * 
 * Namespace for the preconfigured NLP pipeline
 */

public class Trees {
	static final StanfordCoreNLP pipeline;
	
	static {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, parse");
	    props.put("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
	    pipeline = new StanfordCoreNLP(props);
	}
	
	public static List<CoreMap> parse(String text) {
	    
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    List<Tree> trees = new ArrayList<>();
	    List<Tree> dependencies = new ArrayList<>();
	    
	    for(CoreMap sentence: sentences) {
	      // this is the parse tree of the current sentence
	    	Tree t = sentence.get(TreeAnnotation.class);
	    	SemanticGraph graph = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
	    	trees.add(t);
	    }
	    return sentences;
	}
	
	/**
	 * Concatenate the leaves of a parse tree, interspersed with spaces
	 * @param t	The input tree
	 * @return The resulting space-delimited string
	 */
	public static String concat(Tree t) {
		StringBuilder b = new StringBuilder("");
		for (Tree l : t.getLeaves()) {
			b.append(l.value());
			b.append(' ');
		}
		b.deleteCharAt(b.length()-1);
		return b.toString();
	}
	
	/**
	 * Most of the time it's more helpful to know the more specific grammatical
	 * relations like "prep_of" than it is to bridge two words across an "of".
	 * @param A more specific relation, if one is known. Otherwise it's just
	 *        the result of .getRelation()
	 */
	public static String getSpecificPreps(GrammaticalRelation rel) {
		String rel_name = rel.getShortName();
		if (rel.getSpecific() != null) {
			rel_name += "_" + rel.getSpecific();
		}
		return rel_name;
	}

	/**
	 * The bare one-word nouns are usually not very good.
	 * "group", "set" and such are especially bad as they are basically just
	 * container types: "group of diseases", "set of rules"
	 * So we concat a few kinds of links to nouns.
	 */
	public static String concatNoun(SemanticGraph graph, IndexedWord rightmost) {
		if (rightmost.tag().startsWith("NN")) {
			StringBuilder phrase = new StringBuilder();
			// Only actually build on nouns
			for (SemanticGraphEdge edge : graph.outgoingEdgeIterable(rightmost)) {
				switch (edge.getRelation().getShortName()) {
				case "nn":
				case "cd":
				//case "amod":
					phrase.append(edge.getDependent().lemma());
					phrase.append(' ');
					break;
				case "prep":
					if (getSpecificPreps(edge.getRelation())
							.equals("prep_of")) {
						phrase.append(edge.getDependent().lemma());
						phrase.append(' ');
					}
					break;
				}
			}
			return phrase.append(rightmost.originalText()).toString();
		} else {
			return rightmost.originalText();
		}
	}
}

