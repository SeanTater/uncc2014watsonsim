package edu.uncc.cs.watsonsim.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.Score;
import edu.uncc.cs.watsonsim.Translation;
import edu.uncc.cs.watsonsim.scorers.Merge;
import lemurproject.indri.QueryAnnotation;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

/**
 *
 * @author Phani Rahul
 */
public class IndriSearcher extends Searcher {
	private final QueryEnvironment q = new QueryEnvironment();
	private boolean enabled = true;
	private final Logger log = Logger.getLogger(getClass());
	
	/**
	 * Setup the Indri Query Environment.
	 * The "indri_index" property is the Indri index path
	 * @param config  The configuration Properties
	 */
	public IndriSearcher(Environment env) {
		super(env);
		if (env.getConfOrDie("indri_enabled") == "false") {
			enabled = false;
		} else {
			try {
				q.addIndex(env.getConfOrDie("indri_index"));
			} catch (Exception e) {
				System.out.println("Setting up the Indri index failed."
						+ " Is the index in the correct location?"
						+ " Is indri_jni included?");
				e.printStackTrace();
				enabled=false;
			}
		}
		Score.register("INDRI_ANSWER_SCORE", -1, Merge.Mean);
		Score.register("INDRI_ANSWER_RANK", -1, Merge.Mean);
		Score.register("INDRI_ANSWER_PRESENT", 0.0, Merge.Or);
	}
	
	public List<Passage> query(String query){
		if (!enabled) return new ArrayList<>();
		// Run the query
		query = q.reformulateQuery(Translation.getIndriQuery(query));
		query = query.replaceAll("#combine", "#uw");
		//query = String.format("#band( %s %s )", query.replaceAll("#combine", "#uw"), query);
		log.info("executing query " + query);
		
		ScoredExtentResult[] ser;
		QueryAnnotation aq;
		// Fetch all titles, texts
		String[] docnos;
		// If they have them, get the titles and full texts
		//ParsedDocument[] full_texts;
		String[] titles;
		try {
			aq = q.runAnnotatedQuery(query, MAX_RESULTS);
			ser = aq.getResults();
			docnos = q.documentMetadata(ser, "docno");
			/*ser = new ScoredExtentResult[0];
			docnos = new String[0];
			ser = q.runQuery(query + " moo", MAX_RESULTS);
			docnos = q.documentMetadata(ser, "docno");
			ser = q.runQuery(query + " bar", MAX_RESULTS);
			docnos = q.documentMetadata(ser, "docno");*/
			//full_texts = IndriSearcher.q.documents(ser);
			//titles = q.documentMetadata(ser, "title");
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
    			"",//titles[i],	        // Title
    			"",                 // Full Text
				docnos[i])          // Reference
			.score("INDRI_ANSWER_RANK", (double) i)
			.score("INDRI_ANSWER_SCORE", ser[i].score)
			.score("INDRI_ANSWER_PRESENT", 1.0));
		}
		return fillFromSources(results);
	}
	
}
