package uncc2014watsonsim.sources.uima.documentSearch;

import java.io.File;
import java.io.IOException;

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
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.uima.types.*;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;

/**
 * This is a sample primary document search AE. It expects a CAS with a QUERY view, and uses the
 * contents of the QUERY view to perform a document search. The output from this AE is a
 * SearchHitList added to the QUERY view of the CAS.
 * 
 * This is currently not doing anything, it is simply here so we can generate the rest of the xml files at this time (3/19/14)
 *
 * @author Jonathan Shuman 

 */
public class LuceneDocumentSearch extends JCasAnnotator_ImplBase {

  private static final String COMPONENT_ID = "LuceneDocumentSearch";

  /**
   * How many results should Lucene and Indri return?
   */
  public final int MAX_RESULTS = 20;
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

  /**
   * Any initializations of data structures/engines (e.g., a retrieval engine) would go into the
   * initialize method. In this dummy class, we don't actually use a retrieval engine.
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
  }

  /**
   * The process method receives one "query" CAS at a time, and performs a document search for each.
   */
  @Override
  public void process(JCas cas) throws AnalysisEngineProcessException {
		JCas queryView;
		searchResultList hits;
		QueryString qString;
		try {
			queryView = cas.getView("QUERY");
			if (queryView == null) throw new Exception("Expecting QUERY view in CAS for primary document search");
			//if (UimaTools.casContainsView(cas, "EXPANDED")) throw new Exception("Flow problem: found EXPANDED view in CAS; primary search CASes must not have EXPANDED view");

			hits = UimaTools.getSingleton(queryView, searchResultList.type);
			if (hits == null) {
				hits = new searchResultList(queryView);
				hits.setList(new EmptyFSList(queryView));
				hits.addToIndexes();
			}
			
			qString = UimaTools.getSingleton(queryView, QueryString.type);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}

		// Code from LuceneSearcher.java
		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(UserSpecificConstants.luceneIndex)));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Lucene index is missing. Check that you filled in the right path in UserSpecificConstants.java.");
		}
		searcher = new IndexSearcher(reader);
		
		try{ 
			String q = ""; 
			for (String term : qString.getQuery().split("\\W+")) {
				q += String.format("text:%s ", term, term);
			}
			
			ScoreDoc[] scoreDocs = searcher.search(parser.parse(q+UserSpecificConstants.luceneResultsFilter), MAX_RESULTS).scoreDocs;
			
			// This isn't range based because we need the rank
			int v = 0;
			for (ScoreDoc scoreDoc : scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				SearchResult uimaResult = new SearchResult(queryView);
				uimaResult.setEngine("lucene");
				uimaResult.setRank(++v);
				String title = doc.get("title");
				String text = doc.get("text");  // this is the text field of the document
				uimaResult.setFullText(text);
				uimaResult.setTitle(title);
				
				FSList expandedList;
				try {
					expandedList = UimaTools.addToFSList(hits.getList(), uimaResult);
				} catch (UimaToolsException e) {
					throw new AnalysisEngineProcessException(e);
				}
				hits.setList(expandedList);
			}// for loop
		// after getting all hits, we can close the reader
					reader.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
		
	}

  /**
   * Any cleanup to be performed at the end of processing should go into this
   * collectionProcessComplete() method (e.g., closing any open files/connections, etc.)
   */
  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    super.collectionProcessComplete();
  }

}
