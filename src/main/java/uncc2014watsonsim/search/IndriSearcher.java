package uncc2014watsonsim.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;
import uncc2014watsonsim.Translation;
import uncc2014watsonsim.qAnalysis.FITBAnnotations;
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
		Score.registerPassageScore("INDRI_RANK");
		Score.registerPassageScore("INDRI_SCORE");
		
	}
	
	public List<Passage> runTranslatedQuery(String query) {
		return runBaseQuery(Translation.getIndriQuery(query));
	}

	public List<Passage> runBaseQuery(String query) {
		// Run the query
		
		ScoredExtentResult[] ser;
		// Fetch all titles, texts
		String[] docnos;
		// If they have them, get the titles and full texts
		ParsedDocument[] full_texts;
		String[] titles;
		try {
			q.addIndex(UserSpecificConstants.indriIndex);
			ser = IndriSearcher.q.runQuery(query, MAX_RESULTS);
			docnos = IndriSearcher.q.documentMetadata(ser, "docno");
			full_texts = IndriSearcher.q.documents(ser);
			titles = IndriSearcher.q.documentMetadata(ser, "title");
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
    			full_texts[i].text, // Full Text
				docnos[i])          // Reference
			.score("INDRI_RANK", (double) i+1) 	// Rank+1 (1 based)
			.score("INDRI_SCORE", ser[i].score));
		}
		// Indri's titles and full texts could be empty. If they are, fill them from sources.db
		return fillFromSources(results);
	}
	
}