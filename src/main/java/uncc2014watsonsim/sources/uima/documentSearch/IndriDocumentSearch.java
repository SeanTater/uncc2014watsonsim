package uncc2014watsonsim.uima.documentSearch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

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
import uncc2014watsonsim.Translation;
import uncc2014watsonsim.uima.types.*;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;

/**
 * This is a sample primary document search AE. It expects a CAS with a QUERY view, and uses the
 * contents of the QUERY view to perform a document search. The output from this AE is a
 * SearchHitList added to the QUERY view of the CAS.
 * 
 * This one is currently not implemented for demoing purposes. It can be very easily integrated.
 *
 * @author Jonathan Shuman 

 */
public class IndriDocumentSearch extends JCasAnnotator_ImplBase {

  private static final String COMPONENT_ID = "IndriDocumentSearch";

  /**
   * How many results should Lucene and Indri return?
   */
  public final int MAX_RESULTS = 20;
  public static QueryEnvironment q;
	
  /**
   * Any initializations of data structures/engines (e.g., a retrieval engine) would go into the
   * initialize method. In this dummy class, we don't actually use a retrieval engine.
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
	  q = new QueryEnvironment();
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
	// Either add the Indri index or die.
		try {
			q.addIndex(UserSpecificConstants.indriIndex);
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
		
		
			String main_query = Translation.getIndriQuery(qString.getQuery());
			
			ScoredExtentResult[] ser = q.runQuery(main_query, MAX_RESULTS);
	
			// Fetch all titles, texts
			String[] docnos = q.documentMetadata(ser, "docno");
			
			// If they have them, get the titles and full texts
			ParsedDocument[] full_texts = q.documents(ser);
			String[] titles = q.documentMetadata(ser, "title");
	
			// Compile them into a uniform format
			for (int i=0; i<ser.length; i++) {
				SearchResult uimaResult = new SearchResult(queryView);
		    	uimaResult.setEngine("indri");
		    	uimaResult.setTitle(titles[i]);
		    	uimaResult.setFullText(full_texts[i].text);
		    	uimaResult.setRank((long) i);
		    	uimaResult.setScore(ser[i].score);
		    	
		    	FSList expandedList;
				try {
					expandedList = UimaTools.addToFSList(hits.getList(), uimaResult);
				} catch (UimaToolsException e) {
					throw new AnalysisEngineProcessException(e);
				}
				hits.setList(expandedList);
			}
		// Indri's titles and full texts could be empty. If they are, fill them from sources.db TODO
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Indri Error!!");
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
