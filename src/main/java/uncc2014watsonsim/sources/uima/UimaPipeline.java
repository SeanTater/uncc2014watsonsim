/**
 * 
 */
package uncc2014watsonsim.sources.uima;

import java.net.URL;
import java.util.Collection;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.CasIterator;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

import com.google.api.services.customsearch.Customsearch.Cse.List;

import uncc2014watsonsim.uima.QueryCollectionReader;
import uncc2014watsonsim.uima.types.QueryString;
import uncc2014watsonsim.uima.types.SearchResult;
import uncc2014watsonsim.uima.types.searchResultList;

/**
 * @author Jonathan Shuman
 * @purpose A pipeline for asking questions with using UIMA
 * Currently this pipeline uses a QueryCollectionReader to read from a file with queries
 * and put these into a cas which is used in the searching of results.
 *
 */
public class UimaPipeline {
	
	/**
	 * Constructor. Initializes the Pipeline
	 * Initializes Query Collection Reader and Analysis Engines.
	 * @throws Exception
	 */
	public UimaPipeline() throws Exception{
		initializeUIMA();
	}
	
	//Constant
	private final String INPUT_FILE = "data/uimaTestQueries.txt";
	
	private AnalysisEngine topLevelEngine;
	private CollectionReader queryCollectionReader; // Currently just pulling from a text file, but can bet setup to pull from the databas
	
 /**
 * @purpose To initialize this pipeline. Called on constructor
 */
private void initializeUIMA() throws Exception{ 
	
	 // Initialize the collection reader
	    System.err.println("Initializing collection reader");
	  queryCollectionReader = null;
      CollectionReaderDescription queryReaderDescription = QueryCollectionReader.getDescription();
      queryReaderDescription.getMetaData().getConfigurationParameterSettings()
              .setParameterValue(QueryCollectionReader.PARAM_INPUT_FILE, INPUT_FILE);
      queryCollectionReader = UIMAFramework.produceCollectionReader(queryReaderDescription);
	
		// Load descriptor
		URL descriptorURL = UimaPipeline.class.getResource("documentSearch/documentSearchApplicationDescriptor.xml");
		if(descriptorURL == null){
			throw new Exception("Could not load Search Descriptor");
		}
		
		AnalysisEngineDescription tld = UIMAFramework.getXMLParser()
				.parseAnalysisEngineDescription(new XMLInputSource(descriptorURL));
		setTopLevelEngine(UIMAFramework.produceAnalysisEngine(tld));
		
	}

	/**
	 * Starts the process of finding queries and their answers.
	 * @throws Exception
	 */
	public void runQueryDemo() throws Exception{
		CAS cas = topLevelEngine.newCAS(); //create a new cas
		while (queryCollectionReader.hasNext()){
			queryCollectionReader.getNext(cas);
			JCas queryView = cas.getJCas().getView("QUERY");
			QueryString qString = UimaTools.getSingleton(queryView, QueryString.type);
			System.out.println("Starting query for: " + qString.getQuery());
			CasIterator outputCASesIterator = topLevelEngine.processAndOutputNewCASes(cas);
			
			//Print them all
			while(outputCASesIterator.hasNext()){
				CAS nextCas = outputCASesIterator.next();
				// These CASes have the document text in them.
				//printCAS(nextCas);
				nextCas.release();
			}
			//print this cas with the search results list:
			printCAS(cas);
			//new cas
			cas.reset();
		}
		topLevelEngine.collectionProcessComplete();
		
	}

	/**
	 * @return the toplevelEngine
	 */
	public AnalysisEngine getTopLevelEngine() {
		return topLevelEngine;
	}
	/**
	 * @param topLevelEngine
	 */
	public void setTopLevelEngine(AnalysisEngine topLevelEngine) {
		this.topLevelEngine = topLevelEngine;
	}
	
	/**
	 * To print a CAS with a query and document view
	 * @param cas - the CAS to print query and document information from.
	 */
	private static void printCAS(CAS cas) throws Exception{
		CAS query = cas.getView("QUERY");
		QueryString qs = UimaTools.getSingleton(query.getJCas(), QueryString.type);
		
		//CAS doc = cas.getView("DOCUMENT");
		
		System.out.println("Query: " + qs.getQuery());
		
		searchResultList resultList = UimaTools.getSingleton(query.getJCas(), searchResultList.type);
		java.util.List<SearchResult> srList = UimaTools.getFSList(resultList.getList());
		
		int i = 0;
		for(SearchResult sr : srList){
			++i;
			System.out.println(i + ": " + sr.getTitle());
		}
	}


}
