package uncc2014watsonsim.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import uncc2014watsonsim.Passage;
import uncc2014watsonsim.StringUtils;

/**
 * @author Phani Rahul
 */
public class LucenePassageSearcher extends Searcher {
	private IndexReader reader;
	private IndexSearcher searcher = null;
	private Analyzer analyzer;
	private QueryParser parser;
	
	public LucenePassageSearcher(Properties config) {
		analyzer = new StandardAnalyzer(Version.LUCENE_47);
		parser = new QueryParser(Version.LUCENE_47, "text", analyzer);
		parser.setAllowLeadingWildcard(true);

		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(StringUtils.getOrDie(config, "lucene_index"))));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Lucene index is missing. Check that you filled in the right path for lucene_index.");
		}
		searcher = new IndexSearcher(reader);
	}
	
	public List<Passage> query(String question_text) {
		List<Passage> results = new ArrayList<Passage>();
		try {
			BooleanQuery q = new BooleanQuery();
			for (String word : question_text.split("\\W+")) {
				q.add(new TermQuery(new Term("text", word)), BooleanClause.Occur.SHOULD);
			}
			TopDocs topDocs = searcher.search(q, MAX_RESULTS);
			
			ScoreDoc[] hits = topDocs.scoreDocs;
			// This isn't range based because we need the rank
			for (int i=0; i < hits.length; i++) {
				ScoreDoc s = hits[i];
				Document doc = searcher.doc(s.doc);
				results.add(new uncc2014watsonsim.Passage(
						"lucene", 			// Engine
						doc.get("title"),	// Title
						doc.get("text"), 	// Text
						doc.get("docno"))   // Reference
						.score("LUCENE_RANK", (double) i)           // Rank
						.score("LUCENE_SCORE", (double) s.score)	// Source
						);
			}
		} catch (IOException e) {
			System.out.println("Failed to query Lucene. Is the index in the correct location?");
			e.printStackTrace();
		}
		
		// Fill any missing full text from sources
		return fillFromSources(results);
	}

}
