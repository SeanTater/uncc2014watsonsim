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
import java.util.Map;
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
import uncc2014watsonsim.Database;
import uncc2014watsonsim.StringUtils;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefAnnotation;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.dcoref.Mention;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.parser.dvparser.DVParser;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.DeterministicCorefAnnotator;
import edu.stanford.nlp.pipeline.ParserAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
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
	AnnotationPipeline pipeline;

	//initialize all models needed for processing a passage of text (multiple sentences)
	public VerbSplit() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		pipeline = new AnnotationPipeline();
		pipeline.addAnnotator(new StanfordCoreNLP(props));
		pipeline.addAnnotator(new ParserAnnotator(false, 50));
		pipeline.addAnnotator(new DeterministicCorefAnnotator(new Properties()));
	}
	
	public static void main(String[] args) throws SQLException {
		VerbSplit v = new VerbSplit();
		
		Database db = new Database();
		// Fetch candidates
		//PreparedStatement candidate_stmt = db.prep("SELECT id, text FROM meta NATURAL JOIN content WHERE id NOT IN (SELECT doc FROM sentences) ORDER BY length(text) DESC LIMIT 100 OFFSET ?;");
		PreparedStatement candidate_stmt = db.prep("SELECT id, text FROM meta NATURAL JOIN content WHERE id NOT IN (SELECT doc FROM sentences) ORDER BY pageviews DESC LIMIT 100 OFFSET ?;");
		// Insert
		PreparedStatement insert = db.prep("INSERT INTO sentences(doc, subject, predicate) VALUES (?, ?, ?);");
		
		Random r = new Random();
		candidate_stmt.setInt(1, r.nextInt(100));
		ResultSet candidates = candidate_stmt.executeQuery();
		while (candidates.next()) {
			// Get the doc text and annotate it.
			String text = candidates.getString("text");
			insert.setLong(1, candidates.getLong("id"));
			Annotation annot_doc = new Annotation(text);
			v.pipeline.annotate(annot_doc);
			
			// Process the annotated sentences
			for (CoreMap sentence : annot_doc.get(SentencesAnnotation.class)) {
				// Get the coreferences
			    Map<Integer, CorefChain> corefs = sentence.get(CorefChainAnnotation.class);
			    if (corefs == null) {
			    	System.err.println("Uh oh. No corefs in" + sentence.toString());
			    } else {
				    for (CorefChain chain : corefs.values()) {
				    	CorefMention representative = chain.getRepresentativeMention();
					    insert.setString(2, representative.mentionSpan);
				    	for (CorefMention ref : chain.getMentionsInTextualOrder()) {
				    		if (!ref.equals(representative)
				    			&& ref.mentionType != Dictionaries.MentionType.PRONOMINAL) {
				    			insert.setString(3, ref.mentionSpan);
				    			insert.addBatch();
				    			System.out.println("Found " + sentence);
				    		}
				    	}
				    }
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