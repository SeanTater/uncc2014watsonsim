package edu.uncc.cs.watsonsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * A String, tokenized, parsed into Trees, and as a semantic graph.
 * 
 */
public class Phrase {
	public final String text;
	private static final Cache<String, Phrase> recent; 

	// Cached Fields
	public final List<String> tokens;
	public final List<Tree> trees;
	public final List<SemanticGraph> graphs;
	private final ConcurrentHashMap<Function<Phrase, ?>, Object> memos;
	
	// Create a pipeline
	static final StanfordCoreNLP pipeline;
	static {
		// Creates an NLP pipeline missing lemma, ner, and dcoref 
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, parse");
	    // Use the faster but slow-loading shift-reduce models
	    props.put("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
	    // When you find something untokenizable, delete it and don't whine
	    props.put("tokenize.options", "untokenizable=noneDelete");
	    pipeline = new StanfordCoreNLP(props);
	    // Save time by caching some, but not too many, recent parses.
	    recent = CacheBuilder.newBuilder()
	    	.concurrencyLevel(50)
	    	.maximumSize(10000)
	    	.weakValues()
	    	.build();
	}
	/**
	 * Create a new NLP parsed phrase.
	 * When finished, all public fields are final, immutable, and non-null.
	 * This will throw an NPE rather than take null text.
	 */
	public Phrase(String text) {
		if (text == null)
			throw new NullPointerException("Text cannot be null.");
		Phrase cache_entry = recent.getIfPresent(text);
		if (cache_entry != null) {
			this.text = cache_entry.text;
			this.tokens = cache_entry.tokens;
			this.trees = cache_entry.trees;
			this.graphs = cache_entry.graphs;
			// Memos are mutable but private and thread-safe.
			this.memos = cache_entry.memos;
		} else {
			this.memos = new ConcurrentHashMap<>();
			this.text = StringEscapeUtils.unescapeXml(text);
			
		    // create an empty Annotation just with the given text
		    Annotation document = new Annotation(text);
		    
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

			this.tokens = Collections.unmodifiableList(StringUtils.tokenize(this.text));
		    this.trees = Collections.unmodifiableList(trees);
		    this.graphs = Collections.unmodifiableList(graphs);
			recent.put(text, this);
		}
	}
	
	/**
	 * Lightweight functional annotations. Either apply the function and get
	 * the result, or if it has been done, return the existing value.
	 * This casts internally but it's type-safe.
	 */
	@SuppressWarnings("unchecked")
	public <X> X memo(Function<Phrase, X> app) {
		return (X) memos.computeIfAbsent(app, (key) -> app.apply(this));
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Phrase other = (Phrase) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
}
