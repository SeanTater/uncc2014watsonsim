package uncc2014watsonsim.search;

import java.util.ArrayList;
import java.util.List;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Score;
import uncc2014watsonsim.Translation;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

/**
 *
 * @author Matthew Gibson
 */
public class TerrierSearcher extends Searcher {
	private static QueryEnvironment q;
	private static boolean enabled = true;
	static {
		// Only initialize the query environment and index once
		q = new QueryEnvironment();
		try {
			q.addIndex(UserSpecificConstants.indriIndex);
		} catch (Exception e) {
			System.out.println("Setting up the Indri index failed."
					+ " Is the index in the correct location?"
					+ " Is indri_jni included?");
			e.printStackTrace();
			enabled=false;
		}
		Score.register("TERRIER_ANSWER_SCORE", Double.NaN, Merge.Mean);
		Score.register("TERRIER_ANSWER_RANK", Double.NaN, Merge.Mean);
	}
	
	public List<Passage> query(String query) {
		if (!enabled) return new ArrayList<>();
		// Run the query
		query = Translation.getIndriQuery(query);
		
		ScoredExtentResult[] ser;
		// Fetch all titles, texts
		String[] docnos;
		// If they have them, get the titles and full texts
		//ParsedDocument[] full_texts;
		String[] titles;
		try {
			ser = TerrierSearcher.q.runQuery(query, MAX_RESULTS);
			docnos = TerrierSearcher.q.documentMetadata(ser, "docno");
			//full_texts = IndriSearcher.q.documents(ser);
			titles = TerrierSearcher.q.documentMetadata(ser, "title");
		} catch (Exception e) {
			// If any other step fails, give a more general message but don't die.
			System.out.println("Querying Indri failed. Is the index in the correct location? Is indri_jni included?");
			e.printStackTrace();
			return new ArrayList<>();
		}

		// Compile them into a uniform format
		List<Passage> results = new ArrayList<Passage>();
		for (int i=0; i<ser.length; i++) {
	    	results.add(new Passage(
    			"indri",         	// Engine
    			titles[i],	        // Title
    			"", //full_texts[i].text, // Full Text
				docnos[i])          // Reference
			.score("INDRI_ANSWER_RANK", (double) i)
			.score("INDRI_ANSWER_SCORE", ser[i].score));
		}
		// Indri's titles and full texts could be empty. If they are, fill them from sources.db
		return fillFromSources(results);
	}
	
}
