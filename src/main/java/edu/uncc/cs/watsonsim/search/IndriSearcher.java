package edu.uncc.cs.watsonsim.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.Score;
import edu.uncc.cs.watsonsim.StringUtils;
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
	private final boolean strict;
	
	/**
	 * Setup the Indri Query Environment.
	 * The "indri_index" property is the Indri index path
	 * @param config  The configuration Properties
	 */
	public IndriSearcher(Environment env, boolean strict) {
		super(env);
		this.strict = strict;
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
		Score.register("INDRI_ANSWER_PRESENT", 0.0, Merge.Sum);
	}
	
	public List<Passage> query(Question question){
		if (!enabled) return new ArrayList<>();
		// Develop the query
		String query = q.reformulateQuery(StringUtils.sanitize(
        		question.getCategory() + " " + question.text
        ));
		if (strict) query = query.replaceAll("#combine", "#uw");
		log.info("Executing query " + query);
		
		ScoredExtentResult[] ser;
		QueryAnnotation aq;
		// Fetch all titles, texts
		String[] docnos;
		try {
			aq = q.runAnnotatedQuery(query, MAX_RESULTS);
			ser = aq.getResults();
			docnos = q.documentMetadata(ser, "docno");
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
    			"",			        // Title
    			"",                 // Full Text
				docnos[i])          // Reference
			.score("INDRI_ANSWER_RANK", (double) i)
			.score("INDRI_ANSWER_SCORE", ser[i].score)
			.score("INDRI_ANSWER_PRESENT", 1.0));
		}
		return fillFromSources(results);
	}
	
}
