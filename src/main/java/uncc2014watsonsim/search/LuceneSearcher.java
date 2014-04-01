package uncc2014watsonsim.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Score;
import uncc2014watsonsim.Translation;

/**
 *
 * @author Phani Rahul
 */
public class LuceneSearcher extends Searcher {
	private static IndexReader reader;
	private static IndexSearcher searcher = null;
	private static Analyzer analyzer;
	private static QueryParser parser;
	
	static {
		analyzer = new StandardAnalyzer(Version.LUCENE_46);
		parser = new QueryParser(Version.LUCENE_46, UserSpecificConstants.luceneSearchField, analyzer);
		parser.setAllowLeadingWildcard(true);		
		/*try {
			reader = DirectoryReader.open(FSDirectory.open(new File(UserSpecificConstants.luceneIndex)));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Lucene index is missing. Check that you filled in the right path in UserSpecificConstants.java.");
		}
		searcher = new IndexSearcher(reader);*/
	}

	public synchronized List<Passage> runQuery(String question_text) throws Exception {
		
		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(UserSpecificConstants.luceneIndex)));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Lucene index is missing. Check that you filled in the right path in UserSpecificConstants.java.");
		}
		searcher = new IndexSearcher(reader);
		
		
		String q = ""; 
		for (String term : question_text.split("\\W+")) {
			q += String.format("text:%s ", term, term);
		}
		
		//ScoreDoc[] hits = searcher.search(parser.parse(q+UserSpecificConstants.luceneResultsFilter), MAX_RESULTS).scoreDocs;
		
		ScoreDoc[] hits = searcher.search(parser.parse(Translation.getLuceneQuery(question_text)), MAX_RESULTS).scoreDocs;
		
		List<Passage> results = new ArrayList<Passage>(); 
		// This isn't range based because we need the rank
		for (int i=0; i < MAX_RESULTS; i++) {
			ScoreDoc s = hits[i];
			Document doc = searcher.doc(s.doc);
			results.add(new uncc2014watsonsim.Passage(
					"lucene", 			// Engine
					doc.get("title"),	// Title
					doc.get("text"), 	// Text
					doc.get("docno"))   // Reference
					.score(Score.LUCENE_RANK, (double) i)           // Rank
					.score(Score.LUCENE_SCORE, (double) s.score)	// Source
					);
		}
		
		// Fill any missing full text from sources
		return fillFromSources(results);
	}

}
