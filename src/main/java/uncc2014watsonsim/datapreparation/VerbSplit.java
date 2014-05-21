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
import java.util.Properties;
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
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.parser.dvparser.DVParser;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

/* @author Wlodek
 * @author Sean Gallagher
 * 
 * Create a score based on how many parse trees the question, candidate answer
 * and passage have in common.
 * 
 * This scorer can be very slow.
 */

public class VerbSplit {
	StanfordCoreNLP scn;

	//initialize all models needed for processing a passage of text (multiple sentences)
	public VerbSplit() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		scn = new StanfordCoreNLP();
	}
	
	public static void main(String[] args) throws SQLException {
		VerbSplit v = new VerbSplit();
		
		SQLiteDB db = new SQLiteDB("watsonsim");
		// Fetch candidates
		//PreparedStatement candidate_stmt = db.prep("SELECT id, text FROM meta NATURAL JOIN content WHERE id NOT IN (SELECT doc FROM sentences) ORDER BY length(text) DESC LIMIT 100 OFFSET ?;");
		PreparedStatement candidate_stmt = db.prep("SELECT id, text FROM meta NATURAL JOIN content WHERE id NOT IN (SELECT doc FROM sentences) ORDER BY pageviews DESC LIMIT 100 OFFSET ?;");
		// Insert
		PreparedStatement insert = db.prep("INSERT INTO sentences(doc, subject, verb, predicate) VALUES (?, ?, ?, ?);");
		
		Random r = new Random();
		candidate_stmt.setInt(1, r.nextInt(100));
		ResultSet candidates = candidate_stmt.executeQuery();
		while (candidates.next()) {
			String text = candidates.getString("text");
			Annotation annot_doc = new Annotation(text);
			v.scn.annotate(annot_doc);
			for (CoreMap sentence : annot_doc.get(SentencesAnnotation.class)) {
				// this is the Stanford dependency graph of the current sentence
			    SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			    /*
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
				*/
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