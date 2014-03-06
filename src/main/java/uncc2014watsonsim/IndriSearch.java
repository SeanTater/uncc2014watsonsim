package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.List;

import privatedata.UserSpecificConstants;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

/**
 *
 * @author Phani Rahul
 */
public class IndriSearch{
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

	public static List<ResultSet> runQuery(String query) throws Exception {
		// Run the query
		ScoredExtentResult[] ser = IndriSearch.q
				.runQuery(query, LocalSearch.MAX_RESULTS);
		// Fetch all titles, texts
		String[] titles = IndriSearch.q.documentMetadata(ser, "title");
		ParsedDocument[] full_texts = IndriSearch.q.documents(ser);
		// Compile them into a uniform format
		List<ResultSet> results = new ArrayList<ResultSet>();
		for (int i=0; i<ser.length; i++) {
	    	results.add(new ResultSet(
				titles[i],          // Title
				full_texts[i].text, // Full Text
				"indri",            // Engine
				i,                  // Rank
				ser[i].score,       // Score
				false               // Correct? We don't know yet.
			));
		}
		return results;
	}
}
