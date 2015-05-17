package edu.uncc.cs.watsonsim.scorers;

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

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.StringUtils;
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

/* @author Wlodek
 * @author Sean Gallagher
 * 
 * Create a score based on how many parse trees the question, candidate answer
 * and passage have in common.
 * 
 * This scorer can be very slow.
 */

public class SentenceSimilarity extends PassageScorer {

	public  String modelsPath="data/"; //models directory
	private File parserMFile; 
	private File sentDetectorMFile;
	private File posMFile;

	public SentenceModel sentenceModel; //sentence detection model 
	public ParserModel parserModel; //parsing model
	public POSTaggerME tagger;
	
	// Prevent unnecessary reinstantiation
	SentenceDetectorME sentenceDetector;
	Parser parser;

	//initialize all models needed for processing a passage of text (multiple sentences)
	//TODO: allow partial initialization parserInit() and chunkerInit()
	public SentenceSimilarity() {
		File modelsDir = new File(this.modelsPath);

		this.parserMFile = new File(modelsDir, "en-parser-chunking.bin");
		this.sentDetectorMFile = new File(modelsDir, "en-sent.bin");
		this.posMFile = new File(modelsDir,"en-pos-maxent.bin");

		InputStream sentModelIn = null;
		FileInputStream parserStream;
		try {
			//for finding sentences
			sentModelIn = new FileInputStream(sentDetectorMFile);
			this.sentenceModel = new SentenceModel(sentModelIn);
			//for finding POS
			FileInputStream posModelStream = new FileInputStream(posMFile);
			POSModel model = new POSModel(posModelStream);
			this.tagger = new POSTaggerME(model);
			//for parsing
			parserStream = new FileInputStream(parserMFile);
			this.parserModel = new ParserModel(parserStream);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sentenceDetector = new SentenceDetectorME(this.sentenceModel);
		parser = ParserFactory.create(
				this.parserModel,
				20, // beam size
				0.95); // advance percentage
	}

	/** Turn one tokenized sentence into one top-ranked parse tree. */
	public Parse parseSentence(List<String> tokens) {
		//StringTokenizer st = new StringTokenizer(tks[i]); 
		//There are several tokenizers available. SimpleTokenizer works best
		System.out.print(";");
		String sent= StringUtils.join(tokens," ");
		return ParserTool.parseLine(sent,parser, 1)[0];
	}
	
	/** Turn a tokenized paragraph into a list of parses */
	public List<Parse> parseParagraph(List<List<String>> paragraph) {
		//find sentences, tokenize each, parse each, return top parse for each
		List<Parse> results = new ArrayList<>(paragraph.size());
		for (List<String> sentence : paragraph) {
			//StringTokenizer st = new StringTokenizer(tks[i]); 
			//There are several tokenizers available. SimpleTokenizer works best
			results.add(parseSentence(sentence));
		}
		return results;
	}
	
	/** Tokenize a paragraph into sentences, then into words. */
	public List<List<String>> tokenizeParagraph(String paragraph) {
		List<List<String>> results = new ArrayList<>();
		// Find sentences, tokenize each, parse each, return top parse for each
		for (String unsplit_sentence : sentenceDetector.sentDetect(paragraph)) {
			results.add(Arrays.asList(
					SimpleTokenizer.INSTANCE.tokenize(unsplit_sentence)
					));
		}
		return results;
	}

	/** Enumerate all of the child parses of a parse tree */
	public List<Parse> getAllChildren(List<Parse> parses){
		List<Parse> doneChildren = new ArrayList<>(parses.size());
		Deque<Parse> nextChildren = new ArrayDeque<>(100);
		nextChildren.addAll(parses);
		while (!nextChildren.isEmpty()) {
			Parse child = nextChildren.remove();
			doneChildren.add(child);
			nextChildren.addAll(Arrays.asList(child.getChildren()));
		}
		return doneChildren;		
	}

	/** Enumerate all the child parses of a single-root parse tree */
	private List<Parse> getAllChildren(Parse parse){
		List<Parse> p = new ArrayList<>(1);
		p.add(parse);
		return getAllChildren(p);
	}

    /** Compute the number of matches between two sets of parses
     *  where a match means same label over the same string 
     * @param pa1  One Parse forest
     * @param pa2  Another parse forest
     * @param verbose Whether to print progress to stdout
     * @return score
     */
	public double compareParseChunks(List<Parse> pa1, List<Parse> pa2, boolean verbose){
		
		HashSet<String> bag1 = new HashSet<>();
		HashSet<String> bag2 = new HashSet<>();
		
		for (Parse p : pa1) {
			bag1.add(p.getCoveredText()+"\n"+p.getLabel());
		}
		for (Parse p : pa2) {
			bag2.add(p.getCoveredText()+"\n"+p.getLabel());
		}
		
		bag2.retainAll(bag1);
		return bag2.size();
	}
	
	/**
	 * Flatten a paragraph into a set of unique tokens
	 * @param paragraph
	 * @return the flattened set
	 */
	public HashSet<String> flatten(List<List<String>> paragraph) {
		HashSet<String> results = new HashSet<>();
		for (List<String> sentence : paragraph)
			for (String word : sentence)
				results.add(word.toLowerCase());
		return results;
	}
	

	/** Generare a normalized score.
	 * 
	 */
	//TODO divide by passage length containing the matches, not the full passage length
	public double scorePassage(Phrase q, Answer a, Passage p) {
		boolean verbose = true;
		
		// Tokenize the text, necessary for simple and NLP searches
		List<List<String>> ca_sentences = tokenizeParagraph(a.text);
		List<List<String>> q_sentences = tokenizeParagraph(q.text);
		List<List<String>> passage_sentences = tokenizeParagraph(p.text);
		
		// Run NLP on the question and candidate answer
		List<Parse> ca_children = getAllChildren(parseParagraph(ca_sentences));
		List<Parse> q_children = getAllChildren(parseParagraph(q_sentences));
		List<Parse> p_children = new ArrayList<>();
		
		// Speedup: Look for these tokens before running NLP
		HashSet<String> target_tokens = flatten(ca_sentences);
		//target_tokens.addAll(flatten(q_sentences));
		// Free stop filtering (costs no more than what we were
		//  already doing)
		target_tokens.removeAll(Arrays.asList(new String[]{
				"i", "me", "you", "he", "she", "him", "they", "them",
				"his", "her", "hers", "my", "mine", "your", "yours", "their", "theirs",
				"of", "a", "the",
				"and", "or", "not", "but",
				"this", "that",	"these", "those",
				"on", "in", "from", "to", "over", "under", "with", "by", "for",
				"without", "beside", "between",
				"has", "have", "had", "will", "would", "gets", "get", "got",
				"be", "am", "been", "was", "were", "being", "is", 
				".", ",", ":", ";", "[", "{", "}", "]", "(", ")", "<", ">",
				"?", "/", "\\", "-", "_", "=", "+", "~", "`", "@", "#", "$",
				"%", "^", "&", "*"
		}));
		
		for (List<String> sentence : passage_sentences) {
			// Does it have the right tokens?
			for (String word : sentence) {
				if (target_tokens.contains(word.toLowerCase())) {
					// Found a common word. Now compare the sentences.
					p_children.addAll(getAllChildren(parseSentence(sentence)));
					break;
				}
			}
		}

		double q_score = compareParseChunks(
				q_children,
				p_children,
				verbose);
		double ca_score = compareParseChunks(
				ca_children,
				p_children,
				verbose);
		return q_score*ca_score/p.text.length();
	}
}

