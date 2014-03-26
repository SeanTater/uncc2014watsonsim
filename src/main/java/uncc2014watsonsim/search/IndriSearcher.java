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
	private boolean includes_fulltext;
	
	public IndriSearcher(boolean includes_fulltext) {
		this.includes_fulltext = includes_fulltext; 
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
		
		ScoredExtentResult[] ser = IndriSearcher.q.runQuery(query, MAX_RESULTS);
		// Fetch all titles, texts
		String[] docnos = IndriSearcher.q.documentMetadata(ser, "docno");
		
		if (includes_fulltext) {
			// If they have them, get the titles and full texts
			ParsedDocument[] full_texts = IndriSearcher.q.documents(ser);
			String[] titles = IndriSearcher.q.documentMetadata(ser, "title");

			// Compile them into a uniform format
			List<Answer> results = new ArrayList<Answer>();
			for (int i=0; i<ser.length; i++) {
		    	results.add(new Answer(
	    			"indri",         	// Engine
	    			full_texts[i].text,	// Title
					titles[i], // Full Text
					docnos[i],          // Reference
					i,                  // Rank
					ser[i].score		// Score
				));
			}

			return results;
		} else {

			// Compile them into a uniform format
			List<Answer> results = new ArrayList<Answer>();
			for (int i=0; i<ser.length; i++) {
		    	results.add(new Answer(
	    			"indri",         	// Engine
	    			null,	// Title
					null, // Full Text
					docnos[i],          // Reference
					i,                  // Rank
					ser[i].score		// Score
				));
			}

			return fillFromSources(results);
		}
	}

}
