package uncc2014watsonsim.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.postingshighlight.PostingsHighlighter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.Passage;

/**
 * @author Phani Rahul
 */
public class LucenePassageSearcher extends Searcher {
	private static IndexReader reader;
	private static IndexSearcher searcher = null;
	private static Analyzer analyzer;
	private static QueryParser parser;
	
	static {
		analyzer = new StandardAnalyzer(Version.LUCENE_46);
		parser = new QueryParser(Version.LUCENE_46, "text", analyzer);
		parser.setAllowLeadingWildcard(true);

		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(UserSpecificConstants.luceneIndex)));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Lucene index is missing. Check that you filled in the right path in UserSpecificConstants.java.");
		}
		searcher = new IndexSearcher(reader);
	}
	
	public synchronized List<Passage> query(String question_text) {
		List<Passage> results = new ArrayList<Passage>();
		try {
			BooleanQuery q = new BooleanQuery();
			String last_word = null;
			for (String word : question_text.split("\\W+")) {
				/*if (last_word != null) {
					PhraseQuery pq = new PhraseQuery();
					pq.setSlop(0);
					pq.add(new Term("text", last_word));
					pq.add(new Term("text", word));
					q.add(pq, BooleanClause.Occur.SHOULD);
				}*/
				q.add(new TermQuery(new Term("text", word)), BooleanClause.Occur.SHOULD);
				last_word = word;
			}
			
			/// Begin lucene reference code (from docs)
			// configure field with offsets at index time
			//FieldType offsetsType = new FieldType(TextField.TYPE_STORED);
			//offsetsType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
			//Field body = new Field("body", "foobar", offsetsType);

			// retrieve highlights at query time
			//PostingsHighlighter highlighter = new PostingsHighlighter();
			//Query query = new TermQuery(new Term("body", "highlighting"));
			TopDocs topDocs = searcher.search(q, MAX_RESULTS);
			//String[] highlights = highlighter.highlight("body", q, searcher, topDocs);
			/// end Lucene reference code
			
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
