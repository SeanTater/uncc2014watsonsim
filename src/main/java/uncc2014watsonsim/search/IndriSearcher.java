package uncc2014watsonsim.search;

import java.util.ArrayList;
import java.util.List;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Document;
import uncc2014watsonsim.Translation;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

/**
 *
 * @author Phani Rahul
 */
public class IndriSearcher extends Searcher {
	private static QueryEnvironment q;
	static {
		// Only initialize the query environment and index once
		q = new QueryEnvironment();
	}

	public List<Answer> runQuery(String query) throws Exception {
		// Run the query
		
		// Either add the Indri index or die.
		try {
			q.addIndex(UserSpecificConstants.indriIndex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Indri index is missing or corrupt. Please check that you entered the right path in UserSpecificConstants.java.");
		}
		
		String exclusions = "";
		for (String term: query.split("\\W+")) {
			exclusions += String.format("10.0 #NOT(title: %s) ", term);
		}
		//String main_query = String.format("#WEIGHT(1.0 text:#combine(%s) %s)", query, exclusions);
		
		String main_query = Translation.getIndriQuery(query);
		
		ScoredExtentResult[] ser = IndriSearcher.q.runQuery(main_query, MAX_RESULTS);
		// Fetch all titles, texts
		String[] docnos = IndriSearcher.q.documentMetadata(ser, "docno");
		// Compile them into a uniform format
		List<Answer> results = new ArrayList<Answer>();
		for (int i=0; i<ser.length; i++) {
	    	results.add(new Answer(
    			"indri",         	// Engine
    			null,				// Title
				null, 				// Full Text
				docnos[i],          // Reference
				i,                  // Rank
				ser[i].score		// Score
			));
		}
		return fillFromSources(results);
	}

}
