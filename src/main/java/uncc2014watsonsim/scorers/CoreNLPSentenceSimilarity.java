package uncc2014watsonsim.scorers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;

/* @author Wlodek
 * @author Sean Gallagher
 * 
 * Create a score based on how many parse trees the question, candidate answer
 * and passage have in common.
 * 
 * This scorer can be very slow.
 */

public class CoreNLPSentenceSimilarity extends PassageScorer {
	
	Properties props;
	StanfordCoreNLP pipeline;
	public CoreNLPSentenceSimilarity() {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
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
	 * Score the similarity of two sentences according to
	 * sum([ len(x) | x of X, y of Y, if x == y ])
	 * where X and Y are the sets of subtrees of the parses of s1 and s2.  
	 * @param x
	 * @param y
	 * @return
	 */
	public double scorePhrases(String s1, String s2) {
		List<Tree> t1 = parseToTrees(s1);
		List<Tree> t2 = parseToTrees(s2);
		
		HashSet<Tree> t1_subtrees = new HashSet<>();
		HashSet<Tree> t2_subtrees = new HashSet<>();
		for (Tree x : t1) t1_subtrees.addAll(x);
		for (Tree y : t2) t2_subtrees.addAll(y);
		t1_subtrees.retainAll(t2_subtrees);
		
		double score = 0.0;
		// x.getLeaves().size() may also be a good idea.
		// I don't have any intuition for which may be better.
		for (Tree x : t1_subtrees) score += x.size();
		return score;
	}
		

	/** Generate a simple score based on scorePhrases.
	 * 
	 */
	public double scorePassage(Question q, Answer a, Passage p) {
		return scorePhrases(p.getText(), a.candidate_text);
	}
}

