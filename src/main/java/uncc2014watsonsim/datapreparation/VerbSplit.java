package uncc2014watsonsim.datapreparation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

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
import opennlp.tools.util.Span;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.SQLiteDB;
import uncc2014watsonsim.StringUtils;

/* @author Wlodek
 * @author Sean Gallagher
 * 
 * Create a score based on how many parse trees the question, candidate answer
 * and passage have in common.
 * 
 * This scorer can be very slow.
 */

public class VerbSplit {

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
	public VerbSplit() {
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
	public Parse parseSentence(List<String> tokens) throws InterruptedException {
		//StringTokenizer st = new StringTokenizer(tks[i]); 
		//There are several tokenizers available. SimpleTokenizer works best
		System.out.print(";");
		String sent= StringUtils.join(tokens," ");
		return ParserTool.parseLine(sent,parser, 1)[0];
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
	
	/**
	 * Get the highest tag on the tree with this type.
	 * 
	 * This uses a BFS.
	 * @param type 
	 * @return 
	 */
	public static Parse getTag(Parse parse, String type, boolean exact) {
		ArrayDeque<Parse> pending = new ArrayDeque<>();
		pending.add(parse);
		while (!pending.isEmpty()) {
			Parse p = pending.pop();

			if (exact && p.getType().equals(type)
					|| (!exact && p.getType().startsWith(type))) {
				return p;
			} else {
				pending.addAll(Arrays.asList(p.getChildren()));
			}
		}
		// Null Object Pattern
		return new Parse("", new Span(0, 0), "UH", 0, 0);
	}
	
	public static void main(String[] args) throws SQLException {
		VerbSplit v = new VerbSplit();
		Heartbeat hb = new Heartbeat(Thread.currentThread());
		hb.start();
		
		SQLiteDB db = new SQLiteDB("watsonsim");
		// Fetch candidates
		PreparedStatement candidate_stmt = db.prep("SELECT id, text FROM meta NATURAL JOIN content WHERE id NOT IN (SELECT doc FROM sentences) ORDER BY length(text) DESC LIMIT 100 OFFSET ?;");
		// Insert
		PreparedStatement insert = db.prep("INSERT INTO sentences(doc, subject, verb, predicate) VALUES (?, ?, ?, ?);");
		
		Random r = new Random();
		candidate_stmt.setInt(1, r.nextInt(100));
		ResultSet candidates = candidate_stmt.executeQuery();
		while (candidates.next()) {
			List<List<String>> paragraph = v.tokenizeParagraph(candidates.getString("text"));
			for (List<String> sentence : paragraph) {
				hb.beat();
				Parse p = null;
				try {
					p = v.parseSentence(sentence);
				} catch (InterruptedException e) {
					hb.beat();
					System.err.println("Stalled while parsing sentence. Moving on.");
					continue;
				}
				Parse vp = VerbSplit.getTag(p, "VP", true);
				
				// Look for a very basic subject, verb, object pattern
				String subject = VerbSplit.getTag(p, "NP", true).getCoveredText();
				String verb = VerbSplit.getTag(vp, "VB", false).getCoveredText();
				String object = VerbSplit.getTag(vp, "NP", true).getCoveredText();
				
				if (!(subject.isEmpty() || verb.isEmpty() || object.isEmpty())) {
					insert.setLong(1, candidates.getLong("id"));
					insert.setString(2, VerbSplit.getTag(p, "NP", true).getCoveredText());
					insert.setString(3, VerbSplit.getTag(vp, "VB", false).getCoveredText());
					insert.setString(4, VerbSplit.getTag(vp, "NP", true).getCoveredText());
					insert.addBatch();
				}
			}
			insert.executeBatch();
			db.commit();
			
			// Get the next random top-100 article
			candidate_stmt.setInt(1, r.nextInt(100));
			candidates.close();
			candidates = candidate_stmt.executeQuery();
		}
	}
}

class Heartbeat extends Thread {
	private long last_heartbeat = new Date().getTime();
	private boolean alive = true;
	private Thread patient;
	
	public Heartbeat(Thread patient) {
		this.patient = patient;
	}
	
	public void run() {
		while (alive) {
			if (last_heartbeat + 30 < new Date().getTime()) {
				// 30 seconds are up
				patient.interrupt();
				beat();
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	public void beat() {
		last_heartbeat = new Date().getTime();
	}
	
	public void done() {
		alive = false;
	}
}

