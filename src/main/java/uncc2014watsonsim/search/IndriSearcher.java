package uncc2014watsonsim.search;

import java.util.ArrayList;
import java.util.List;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Document;
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
		
		ScoredExtentResult[] ser = IndriSearcher.q
				.runQuery(String.format(UserSpecificConstants.indriResultsFilter,query), 
						MAX_RESULTS);
		// Fetch all titles, texts
		String[] docnos = IndriSearcher.q.documentMetadata(ser, "docno");
		// Compile them into a uniform format
		List<Document> results = new ArrayList<Document>();
		for (int i=0; i<ser.length; i++) {
	    	results.add(new Document(
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
