package uncc2014watsonsim.search;

import java.util.ArrayList;
import java.util.List;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.ResultSet;
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
		
		// Either add the Indri index or die.
		try {
			q.addIndex(UserSpecificConstants.indriIndex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Indri index is missing or corrupt. Please check that you entered the right path in UserSpecificConstants.java.");
		}
	}

	public List<ResultSet> runQuery(String query) throws Exception {
		// Run the query
		ScoredExtentResult[] ser = IndriSearcher.q
				.runQuery(query, MAX_RESULTS);
		// Fetch all titles, texts
		String[] titles = IndriSearcher.q.documentMetadata(ser, "title");
		ParsedDocument[] full_texts = IndriSearcher.q.documents(ser);
		// Compile them into a uniform format
		List<ResultSet> results = new ArrayList<ResultSet>();
		for (int i=0; i<ser.length; i++) {
	    	results.add(new ResultSet(
				titles[i],          // Title
				full_texts[i].text, // Full Text
				"indri",            // Engine
				i,                  // Rank
				ser[i].score
			));
		}
		return results;
	}
}
