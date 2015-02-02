package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;

/* @author Wlodek
 * @author Sean Gallagher
 * 
 */

public class DetectLAT {
	StanfordCoreNLP pipeline;
	public DetectLAT() {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, parse");
	    props.put("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
	    pipeline = new StanfordCoreNLP(props);
	}
	
	public List<Tree> parseToTrees(String text) {
	    
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    List<Tree> trees = new ArrayList<>();
	    
	    for(CoreMap sentence: sentences) {
	      // this is the parse tree of the current sentence
	      trees.add(sentence.get(TreeAnnotation.class));
	    }
	    return trees;
	}
	
	/**
	 * Intermediate results from LAT detection
	 *
	 */
	private static class LATStatus {
		public LATStatus(Tree d, Tree n){
			dt = d; nn = n;
		}
		public LATStatus(LATStatus a, LATStatus b) {
			if (a.dt == null)	dt = b.dt;
			else				dt = a.dt;
			if (a.nn == null)	nn = b.nn;
			else				nn = a.nn;
		}
		public final Tree dt;	// Determiner
		public final Tree nn;	// Noun
	}
	
	private String treeAsString(Tree t) {
		StringBuilder b = new StringBuilder("");
		for (Tree l : t.getLeaves()) {
			b.append(l.value());
			b.append(' ');
		}
		b.deleteCharAt(b.length()-1);
		return b.toString();
	}
	
	/**
	 * A very simple LAT detector. It wants the lowest subtree with both a determiner and a noun
	 * @param t
	 */
	public LATStatus simpleFetchLAT(Tree t) {
		switch (t.value()) {
		case "DT": return new LATStatus(t, null);
		case "NN": return new LATStatus(null, t);
		default:
			LATStatus l = new LATStatus((Tree) null, null);
			for (Tree kid : t.children())
				l = new LATStatus(l, simpleFetchLAT(kid));
			return l;
		}
	}
	
	public String simpleFetchLAT(String s) {
		for (Tree t : parseToTrees(s)) {
			LATStatus lat = simpleFetchLAT(t);
			if (lat.nn != null) return treeAsString(lat.nn);
		}
		return "";
	}
}

