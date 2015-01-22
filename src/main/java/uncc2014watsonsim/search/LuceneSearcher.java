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
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Score;
import uncc2014watsonsim.StringUtils;
import uncc2014watsonsim.scorers.Merge;

/**
 * @author Phani Rahul
 */
public class LuceneSearcher extends Searcher {
	private IndexReader reader;
	private IndexSearcher searcher;
	private Analyzer analyzer;
	private QueryParser parser;
	
	public LuceneSearcher(Properties config) {
		analyzer = new StandardAnalyzer(Version.LUCENE_47);
		parser = new QueryParser(Version.LUCENE_47, "text", analyzer);
		parser.setAllowLeadingWildcard(true);

		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(StringUtils.getOrDie(config, "lucene_index"))));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Lucene index is missing. Check that you filled in the right path in UserSpecificConstants.java.");
		}
		searcher = new IndexSearcher(reader);
	
		Score.register("LUCENE_ANSWER_RANK", Double.NaN, Merge.Mean);
		Score.register("LUCENE_ANSWER_SCORE", Double.NaN, Merge.Mean);
		Score.register("LUCENE_ANSWER_PRESENT", 0.0, Merge.Or);
	}
	
	public List<Passage> query(String question_text) {
		List<Passage> results = new ArrayList<Passage>();
		try {
			/*DisjunctionMaxQuery q = new DisjunctionMaxQuery((float) 0.1);
			
			// Text
			//PhraseQuery pq = new PhraseQuery();
			//q.setSlop(9);
			//pq.setBoost(1);
			Deque<String> history = new ArrayDeque<String>(3);
			for (String word : question_text.split("\\W+")) {
				q.add(new TermQuery(new Term("text", word)));
			}
			q.add(pq, BooleanClause.Occur.SHOULD);
			
			// Title
			pq = new PhraseQuery();
			pq.setSlop(2);
			for (String word : question_text.split("\\W+")) {
				pq.add(new Term("title", word));
			}
			q.add(pq, BooleanClause.Occur.SHOULD);
			*/
			BooleanQuery q = new BooleanQuery();
			String last_word = null;
			for (String word : question_text.split("\\W+")) {
				if (last_word != null) {
					PhraseQuery pq = new PhraseQuery();
					pq.setSlop(1);
					pq.add(new Term("text", last_word));
					pq.add(new Term("text", word));
					q.add(pq, BooleanClause.Occur.SHOULD);
				}
				q.add(new TermQuery(new Term("text", word)), BooleanClause.Occur.SHOULD);
				last_word = word;
			}
			ScoreDoc[] hits = searcher.search(q, MAX_RESULTS).scoreDocs;
			// This isn't range based because we need the rank
			for (int i=0; i < hits.length; i++) {
				ScoreDoc s = hits[i];
				Document doc = searcher.doc(s.doc);
				results.add(new uncc2014watsonsim.Passage(
						"lucene", 			// Engine
						doc.get("title"),	// Title
						doc.get("text"), 	// Text
						doc.get("docno"))   // Reference
						.score("LUCENE_ANSWER_RANK", (double) i)           // Rank
						.score("LUCENE_ANSWER_SCORE", (double) s.score)	// Source
						.score("LUCENE_ANSWER_PRESENT", 1.0)
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
