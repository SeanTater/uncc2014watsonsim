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

import uncc2014watsonsim.Environment;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Score;
import uncc2014watsonsim.StringUtils;
import uncc2014watsonsim.scorers.Merge;

/**
 * @author Phani Rahul
 */
public class LuceneSearcher extends Searcher {
	private IndexSearcher searcher;
	
	public LuceneSearcher(Environment env) {

		IndexReader reader;
		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(env.getOrDie("lucene_index"))));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("The candidate-answer Lucene index failed to open.");
		}
		searcher = new IndexSearcher(reader);
	
		Score.register("LUCENE_ANSWER_RANK", -1, Merge.Mean);
		Score.register("LUCENE_ANSWER_SCORE", -1, Merge.Mean);
		Score.register("LUCENE_ANSWER_PRESENT", 0.0, Merge.Or);
	}
	
	/**
	 * Create a Lucene query using the bigrams in the given text
	 * @param text
	 */
	public BooleanQuery queryFromSkipBigrams(String text) {
		BooleanQuery q = new BooleanQuery();
		String prev_word = null;
		for (String word : text.split("\\W+")) {
			if (prev_word != null) {
				PhraseQuery pq = new PhraseQuery();
				pq.setSlop(1);
				pq.add(new Term("text", prev_word));
				pq.add(new Term("text", word));
				q.add(pq, BooleanClause.Occur.SHOULD);
			}
			q.add(new TermQuery(new Term("text", word)), BooleanClause.Occur.SHOULD);
			prev_word = word;
		}
		return q;
	}
	
	
	public List<Passage> query(String question_text) {
		List<Passage> results = new ArrayList<>();
		try {
			ScoreDoc[] hits = searcher.search(
					queryFromSkipBigrams(question_text),
					MAX_RESULTS).scoreDocs;
			// This isn't range based because we need the rank
			for (int i=0; i < hits.length; i++) {
				ScoreDoc s = hits[i];
				Document doc = searcher.doc(s.doc);
				results.add(new uncc2014watsonsim.Passage(
						"lucene", 			// Engine
						"",	// Title - filled in by shared db
						"", // Text - filled in by shared db
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
