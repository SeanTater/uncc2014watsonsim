package uncc2014watsonsim.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.PassageRef;
import uncc2014watsonsim.Score;
import uncc2014watsonsim.Translation;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

/**
 * IR based candidate answer generator based on Indri. It generates PassageRefs
 * and turns them into Passages using a builtin deref().
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class IndriSearcher extends Searcher {
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
		Score.registerAnswerScore("INDRI_ANSWER_SCORE");
		Score.registerAnswerScore("INDRI_ANSWER_RANK");
	}
	
	public List<Passage> query(String query) {
		if (!enabled) return new ArrayList<>();
		// Run the query
		query = Translation.getIndriQuery(query);
		
		ScoredExtentResult[] ser;
		// Fetch all titles, texts
		String[] docnos;
		// If they have them, get the titles and full texts
		ParsedDocument[] full_texts;
		String[] titles;
		try {
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
		List<PassageRef> refs = new ArrayList<>();
		for (int i=0; i<ser.length; i++) {
	    	refs.add(new PassageRef(
    			"indri",         	// Engine
				docnos[i],          // Reference
    			Optional.ofNullable(titles[i]),	        // Title
    			Optional.ofNullable(full_texts[i].text)) // Full Text
	    	//TODO: Fix these scores.
			//.score("INDRI_ANSWER_RANK", (double) i)
			//.score("INDRI_ANSWER_SCORE", ser[i].score)
	    	);
		}
		// Indri's titles and full texts could be empty. If they are, fill them from sources.db
		return deref(refs);
	}
	
}
