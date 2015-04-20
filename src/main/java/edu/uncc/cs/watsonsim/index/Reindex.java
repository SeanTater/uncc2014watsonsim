package edu.uncc.cs.watsonsim.index;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.dbutils.ResultSetIterator;

import com.google.common.collect.Queues;

import edu.stanford.nlp.util.Triple;
import static edu.stanford.nlp.util.Triple.makeTriple;
import edu.uncc.cs.watsonsim.Database;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;

/**
 *
 * @author Phani Rahul
 * @author Modifications: Jonathan Shuman
 * @author Later rewrite by Sean Gallagher
 * @purpose Index a database of plain-text sources using pluggable modules
 */
public class Reindex {
    /**
     * the input file which has to be indexed. This is a database made from TRECtext's
     */
    private final Database db;
	final List<Segment> indexers;
	
	public Reindex() throws IOException {
		db = new Database(new Environment());
    	// Read the configuration
		Properties props = null;
		for (String prefix : new String[]{"data/", ""}) {
			try (Reader s = new InputStreamReader(
					new FileInputStream(prefix + "config.properties"), "UTF-8")){
				// Make it, then link it if it works.
				Properties _local_props = new Properties();
				_local_props.load(s);
				props = _local_props;
			} catch (FileNotFoundException e) {
				// This is only an error if none are found.
			}
		}
		// If it didn't link, all the reads failed.
		if (props == null) {
			throw new IOException("Failed to read config.properties in either "
					+ "data/ or "
					+ System.getProperty("user.dir") // CWD
					+ " You can create one by making a copy of"
					+ " config.properties.sample. Check the README as well.");
		}
		
		// Now make properties immutable, and call it a Map<String, String>
		Map<Object, Object> m = new HashMap<>();
		m.putAll(props);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, String> config = Collections.unmodifiableMap((Map) m);
		
		indexers = Arrays.asList(
				//new Lucene(Paths.get(config.get("lucene_index"))),
				//new Indri(config.get("indri_index"))
				new Bigrams(),
				new Edges()
				);
		
	}

    /**
     * Index collected datasources using Lucene and Indri 
     */
    public static void main(String[] args) throws SQLException, IOException {
    	new Reindex().run();
    }
    
    public void run() throws SQLException {
    	try {
	        indexAll("SELECT title, text, reference FROM sources;");
	        
	        /*indexAll("SELECT "
	        			+ "title, "
	        			+ "string_agg(text, ' ') as text,"
	        			+ "min(reference) as reference "
	    			+ "FROM sources "
					+ "GROUP BY title;");*/
		} finally {
			// Even if the process is interrupted, save the indices!
			indexers.forEach(i -> { 
				try {
					i.close();
				} catch (Exception e) {
					e.printStackTrace();
				}	
			});
		}
        System.out.println("Done indexing.");
    }
    
    private void indexAll(String query) throws SQLException {
    	// TODO: turn off autocommit
    	PreparedStatement statements = db.prep(query);
    	statements.setFetchSize(10000);
    	ResultSet rs = statements.executeQuery();
    	AtomicInteger c = new AtomicInteger();
    	StreamSupport.stream(
			ResultSetIterator.iterable(rs).spliterator(), true)
			.forEach( row -> {
				try {
					Passage pass = new Passage(
						"none", (String)row[0], (String)row[1], (String)row[2]);
				
		    		for (Segment i : indexers) {
		    			i.accept(pass);
		    		}
		    		int count = c.getAndIncrement();
		    		if (count % 1000 == 0) {
		    			System.out.println("Indexed " + count);
		    		}
				} catch (IllegalArgumentException ex) {
					/*
					 *  On extremely rare occasions (< 0.00000593% of passages)
					 *  it will throw an error like the following:
					 *  
					 *  Exception in thread "main" java.lang.IllegalArgumentException: No head rule defined for SYM using class edu.stanford.nlp.trees.SemanticHeadFinder in SYM-10
        at edu.stanford.nlp.trees.AbstractCollinsHeadFinder.determineNonTrivialHead(AbstractCollinsHeadFinder.java:233)
        at edu.stanford.nlp.trees.SemanticHeadFinder.determineNonTrivialHead(SemanticHeadFinder.java:409)
        at edu.stanford.nlp.trees.AbstractCollinsHeadFinder.determineHead(AbstractCollinsHeadFinder.java:187)
        at edu.stanford.nlp.trees.TreeGraphNode.percolateHeads(TreeGraphNode.java:292)
        at edu.stanford.nlp.trees.TreeGraphNode.percolateHeads(TreeGraphNode.java:290)
        at edu.stanford.nlp.trees.TreeGraphNode.percolateHeads(TreeGraphNode.java:290)
        at edu.stanford.nlp.trees.TreeGraphNode.percolateHeads(TreeGraphNode.java:290)
        at edu.stanford.nlp.trees.TreeGraphNode.percolateHeads(TreeGraphNode.java:290)
        at edu.stanford.nlp.trees.GrammaticalStructure.<init>(GrammaticalStructure.java:103)
        at edu.stanford.nlp.trees.EnglishGrammaticalStructure.<init>(EnglishGrammaticalStructure.java:86)
        at edu.stanford.nlp.trees.EnglishGrammaticalStructure.<init>(EnglishGrammaticalStructure.java:66)
        at edu.stanford.nlp.trees.EnglishGrammaticalStructureFactory.newGrammaticalStructure(EnglishGrammaticalStructureFactory.java:29)
        at edu.stanford.nlp.trees.EnglishGrammaticalStructureFactory.newGrammaticalStructure(EnglishGrammaticalStructureFactory.java:5)
        at edu.stanford.nlp.pipeline.ParserAnnotatorUtils.fillInParseAnnotations(ParserAnnotatorUtils.java:50)
        at edu.stanford.nlp.pipeline.ParserAnnotator.finishSentence(ParserAnnotator.java:249)
        at edu.stanford.nlp.pipeline.ParserAnnotator.doOneSentence(ParserAnnotator.java:228)
        at edu.stanford.nlp.pipeline.SentenceAnnotator.annotate(SentenceAnnotator.java:95)
        at edu.stanford.nlp.pipeline.AnnotationPipeline.annotate(AnnotationPipeline.java:67)
        at edu.stanford.nlp.pipeline.StanfordCoreNLP.annotate(StanfordCoreNLP.java:847)
        at edu.uncc.cs.watsonsim.Phrase.<init>(Phrase.java:80)
        at edu.uncc.cs.watsonsim.Passage.<init>(Passage.java:24)
        at edu.uncc.cs.watsonsim.index.Reindex.lambda$indexAll$3(Reindex.java:122)
        at edu.uncc.cs.watsonsim.index.Reindex$$Lambda$1/766572210.accept(Unknown Source)
        at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
        at java.util.Spliterators$ArraySpliterator.forEachRemaining(Spliterators.java:948)
        at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:512)
        at java.util.stream.ForEachOps$ForEachTask.compute(ForEachOps.java:290)
        at java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:731)
        at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
        at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:902)
        at java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1689)
        at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1644)
        at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
					 */
					return;
				}
	    	});
    }
}
