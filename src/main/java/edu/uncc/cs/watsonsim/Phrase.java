package edu.uncc.cs.watsonsim;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import static edu.stanford.nlp.util.Pair.makePair;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;

/**
 * A String, tokenized, parsed into Trees, and as a semantic graph.
 * 
 */
public class Phrase {
	public final String text;
	private static final Cache<String, Phrase> recent; 

	// Cached Fields
	private transient ConcurrentHashMap<Function<? extends Phrase, ?>, Object> memos;
	public transient Log log = Log.NIL;
	
	// Create a pipeline
	private static final StanfordCoreNLP pipeline;
	private static final StanfordCoreNLP constituency_parse_pipeline;
	static {
		pipeline = makeCoreNLPPipeline("tokenize, cleanxml, ssplit, pos, lemma, parse");
		constituency_parse_pipeline = makeCoreNLPPipeline("tokenize, cleanxml, ssplit, pos, lemma, parse");
		// Save time by caching some, but not too many, recent parses.
	    recent = CacheBuilder.newBuilder()
	    	.concurrencyLevel(50)
	    	.maximumSize(10000)
	    	.weakValues()
	    	.build();
	}
	
	/** We still need to use pipelines from other systems. So we make them
	 * statically and use them elsewhere.
	 * @param annotators
	 * @return
	 */
	private static StanfordCoreNLP makeCoreNLPPipeline(String annotators) {
		// Creates an NLP pipeline missing ner, and dcoref 
	    Properties props = new Properties();
	    props.put("annotators", annotators);
	    // Use the faster parsing but slower loading shift-reduce models
	    props.put("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
	    // When you find something untokenizable, delete it and don't whine
	    props.put("tokenize.options", "untokenizable=noneDelete");
	    return new StanfordCoreNLP(props);
	}
	
	/**
	 * This no-args constructor exists solely for deserialization
	 */
	private Phrase() {
		text = "";
		memos = new ConcurrentHashMap<>();
		log = Log.NIL;
	}
	
	/**
	 * Create a new NLP parsed phrase.
	 * This will throw an NPE rather than take null text.
	 * The memo system is lazy and phrases are cached so this is quite cheap.
	 */
	public Phrase(String text) {
		if (text == null)
			throw new NullPointerException("Text cannot be null.");
		Phrase cache_entry = recent.getIfPresent(text);
		if (cache_entry != null) {
			this.text = cache_entry.text;
			// Memos are mutable but private and thread-safe.
			this.memos = cache_entry.memos;
		} else {
			this.memos = new ConcurrentHashMap<>();
			this.text = StringEscapeUtils.unescapeXml(text);
			recent.put(text, this);
		}
	}
	
	/**
	 * Lightweight functional annotations. Either apply the function and get
	 * the result, or if it has been done, return the existing value.
	 * 
	 * Here's the cute part: You to annotate recursively to make pipelines.
	 * 
	 * There are caveats: You need to be sure your function input type matches
	 * the type you are annotating or you will get runtime errors. The output
	 * types, however, are compile time type checked. This is fixable but makes
	 * the API uglier so we don't enforce it.
	 * Also, if your annotator returns null, the result will not be cached. So
	 * if your annotator is expensive, return some singleton instead.
	 */
	@SuppressWarnings("unchecked")
	public <X, T extends Phrase> X memo(Function<T, X> app) {
		/*
		 * Atomicity is not necessary here because the functions are
		 * idempotent. Enforcing atomicity can cause a deadlock, because
		 * memo() needs to be reentrant. Instead, just allow duplicate put()'s
		 */
		X output = (X) memos.get(app);
		if (output == null)
			output = app.apply((T) this);
		if (output != null)
			memos.put(app, output);
		return output;
	}
	
	/*
	 * Convenience functions for common annotations
	 */
	private static final Function<Phrase, Annotation> coreNLP = Phrase::_coreNLP;
	private static Annotation _coreNLP(Phrase p) {
		// create an empty Annotation just with the given text
	    Annotation document = new Annotation(p.text);
	    
	    try{
	    	// run all Annotators on this text
	    	pipeline.annotate(document);
		} catch (IllegalArgumentException | NullPointerException ex) {
			/*
			 *  On extremely rare occasions (< 0.00000593% of passages)
			 *  it will throw an error like the following:
			 *  
			 *  Exception in thread "main" java.lang.IllegalArgumentException:
			 *  No head rule defined for SYM using class edu.stanford.nlp.trees.SemanticHeadFinder in SYM-10
			 *  
			 *  On more frequent occasions, you get the following:
			 *  Exception in thread "main" java.lang.NullPointerException
    		 *  at edu.stanford.nlp.dcoref.RuleBasedCorefMentionFinder.findHead(RuleBasedCorefMentionFinder.java:276)
    		 *  
    		 *  Both of these are fatal for the passage.
    		 *  Neither are a big deal for the index. Forget them.
			 */
		}
	    return document;
	}
	
	/**
	 * Return CoreNLP sentences.
	 * Never returns null, only empty collections.
	 */
	private static final Function<Phrase, List<CoreMap>> sentences = Phrase::_sentences;
	private static List<CoreMap> _sentences(Phrase p) {
	    return Optional.ofNullable(
	    			p.memo(Phrase.coreNLP)
	    				.get(SentencesAnnotation.class))
    				.orElse(Collections.emptyList());
	    			
	}
	
	/**
	 * Return CoreNLP constituency trees
	 */
	public static final Function<Phrase, List<Tree>> trees = Phrase::_trees;
	private static List<Tree> _trees(Phrase p) {
		// create an empty Annotation just with the given text
	    Annotation document = p.memo(Phrase.coreNLP);
	    
	    try{
	    	// Run the full parse on this text
	    	constituency_parse_pipeline.annotate(document);
		} catch (IllegalArgumentException | NullPointerException ex) {
			/*
			 *  On extremely rare occasions (< 0.00000593% of passages)
			 *  it will throw an error like the following:
			 *  
			 *  Exception in thread "main" java.lang.IllegalArgumentException:
			 *  No head rule defined for SYM using class edu.stanford.nlp.trees.SemanticHeadFinder in SYM-10
			 *  
			 *  On more frequent occasions, you get the following:
			 *  Exception in thread "main" java.lang.NullPointerException
    		 *  at edu.stanford.nlp.dcoref.RuleBasedCorefMentionFinder.findHead(RuleBasedCorefMentionFinder.java:276)
    		 *  
    		 *  Both of these are fatal for the passage.
    		 *  Neither are a big deal for the index. Forget them.
			 */
		}
		return p.memo(Phrase.sentences)
				.stream()
				.map(s -> s.get(TreeAnnotation.class))
				.filter(Objects::nonNull)
				.collect(toList());
	}
	
	/**
	 * Return Lucene tokens
	 */
	public static Function<Phrase, List<String>> tokens = Phrase::_tokens;
	private static List<String> _tokens(Phrase p) {
		return StringUtils.tokenize(p.text);
	}
	
	/**
	 * Return very lightly processed tokens.
	 * TODO: Imitate the token processing in Glove
	 */
	public static Function<Phrase, List<String>> simpleTokens = Phrase::_simpleTokens;
	private static List<String> _simpleTokens(Phrase p) {
		return Arrays.asList(p.text.split("\\W+"));
	}
	
	/**
	 * Return CoreNLP dependency trees
	 */
	public static final Function<Phrase, List<SemanticGraph>> graphs = Phrase::_graphs;
	private static List<SemanticGraph> _graphs(Phrase p) {
		return p.memo(Phrase.sentences)
				.stream()
				.map(s -> s.get(CollapsedCCProcessedDependenciesAnnotation.class))
				.filter(Objects::nonNull)
				.collect(toList());
	}
	
	/**
	 * Annotation for lemmatized tokens 
	 */
	public static final Function<Phrase, List<String>> lemmas = Phrase::_lemmas;
	private static List<String> _lemmas(Phrase p) {
		return p.memo(Phrase.sentences)
				.stream()
				.flatMap(s -> s.get(TokensAnnotation.class).stream())
				.map( t -> t.get(LemmaAnnotation.class))
				.collect(toList());
	}
	
	/**
	 * Get a map for finding the main mention of any Coref
	 */
	public static final Function<Phrase, Map<Integer, Pair<CorefMention, CorefMention>>> unpronoun = Phrase::_unpronoun;
	private static Map<Integer, Pair<CorefMention, CorefMention>> _unpronoun(Phrase p) {
		Stream<Pair<CorefMention, CorefMention>> s =
				Stream.of(p.memo(Phrase.coreNLP).get(CorefChainAnnotation.class))
			.filter(Objects::nonNull)  // Do nothing with an empty map
			.flatMap(chains -> chains.entrySet().stream()) // Disassemble the map
		    .flatMap(entry -> {
				// Link each entry to it's main mention
				CorefMention main = entry.getValue().getRepresentativeMention();
				return entry.getValue().getMentionsInTextualOrder().stream()
					.filter(mention -> mention != main)
					.map(mention -> makePair(mention, main));
			});
		// Type inference chokes here so write it down then return.
		return s.collect(HashMap::new,
				(m, pair) -> m.put(pair.first.headIndex, pair),
				(l, r) -> {});
	}

	/**
	 * Transitional shortcut for memo(Phrase:tokens)
	 * @deprecated
	 */
	public List<String> getTokens() {
		return memo(Phrase.tokens);
	}

	/**
	 * Transitional shortcut for memo(Phrase:trees)
	 * @deprecated
	 */
	public List<Tree> getTrees() {
		return memo(Phrase.trees);
	}

	/**
	 * Transitional shortcut for memo(Phrase:graphs)
	 * @deprecated
	 */
	public List<SemanticGraph> getGraphs() {
		return memo(Phrase.graphs);
	}

	/**
	 * Transitional shortcut for memo(Phrase:unpronoun)
	 * @deprecated
	 */
	public Map<Integer, Pair<CorefMention, CorefMention>> getUnpronoun() {
		return memo(Phrase.unpronoun);
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


	/**
	 * Deserialize JSON into a Phrase.
	 * SemanticGraph, Tree and friends have cycles and we can regenerate them
	 * anyway so just mark them transient and reparse the Phrase later.
	 
	public static class Deserializer implements JsonDeserializer<Phrase> {
		@Override
		public Phrase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return new Phrase(json.getAsJsonObject().get("text").getAsString());
		}
	}*/
}

