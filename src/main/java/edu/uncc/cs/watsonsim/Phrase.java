package edu.uncc.cs.watsonsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.uncc.cs.watsonsim.nlp.Trees;

/**
 * A String, tokenized, parsed into Trees, and as a semantic graph.
 * 
 */
public class Phrase {
	public final String text;

	// Cached Fields
	private final Annotation document;
	public final List<String> tokens;
	public final List<Tree> trees;
	public final List<SemanticGraph> graphs;
	
	// Create a pipeline
	static final StanfordCoreNLP pipeline;
	static {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, parse");
	    props.put("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
	    pipeline = new StanfordCoreNLP(props);
	}
	
	public Phrase(String text) {
		if (text == null)
			throw new NullPointerException("Text cannot be null.");
		this.text = StringEscapeUtils.unescapeXml(text);
		this.tokens = Collections.unmodifiableList(StringUtils.tokenize(this.text));
		
	    // create an empty Annotation just with the given text
	    document = new Annotation(text);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    List<Tree> trees = new ArrayList<>();
	    List<SemanticGraph> graphs = new ArrayList<>();
	    
	    
	    for(CoreMap sentence: sentences) {
	      // this is the parse tree of the current sentence
	    	trees.add(sentence.get(TreeAnnotation.class));
	    	graphs.add(sentence.get(CollapsedCCProcessedDependenciesAnnotation.class));
	    }
	    
	    this.trees = Collections.unmodifiableList(trees);
	    this.graphs = Collections.unmodifiableList(graphs);
	}

}
