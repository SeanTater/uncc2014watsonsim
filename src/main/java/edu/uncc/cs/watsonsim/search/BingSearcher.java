package edu.uncc.cs.watsonsim.search;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.fluent.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Score;
import edu.uncc.cs.watsonsim.scorers.Merge;

/**
 * Internet-enabled Searcher for Bing.
 * 
 * You will need a Bing api key, which you can (as of the time of this writing)
 * get from <a href="http://datamarket.azure.com">Microsoft</a>
 * 
 * Bing gives around 5000 queries per month, which means that in most cases for
 * sustained development you will need to use CachingSearcher.
 * 
 * @see CachingSearcher
 * @see privatedata.bingAPIKey
 * @author Sean Gallagher
 * @author Stephen Stanton
 * @author D Haval
 */
public class BingSearcher extends Searcher {
	private final String key;
	private final Logger log = Logger.getLogger(getClass());
	public BingSearcher(Environment env) {
		super(env);
		Score.register("BING_ANSWER_RANK", -1, Merge.Mean);
		Score.register("BING_ANSWER_PRESENT", 0.0, Merge.Or);
		key = env.getConfOrDie("bing_api_key");
	}
	
	public List<Passage> query(String query) {
		
	    URI uri = URI.create(""); // A bogus workaround for "may not have been initialized"
		try {
			uri = new URIBuilder()
				.setScheme("https")
				.setHost("api.datamarket.azure.com")
				.setPath("/Data.ashx/Bing/Search/v1/Web")
				.addParameter("Query", String.format("'%s'", query)).build(); // Should we place it in quotes?
				//.addParameter("$top", "50")
				//.addParameter("$format", "Atom").build();
		} catch (URISyntaxException e1) {
			/* This bogus block is required by Java,
			 * but strictly speaking new URIBuilder() can't actually throw
			 * this error because it has no input (so there can be no syntax
			 * error). Hence, this block is unreachable.
			 */
			e1.printStackTrace();
		}

	    List<Passage> results = new ArrayList<Passage>();
	    try {
	    	String resp = Executor
	    		.newInstance()
				.auth(key, key)
	    		.execute(Request.Get(uri))
	    		.returnContent().asString();
	    	
	    	Document doc = Jsoup.parse(resp);
	    	List<Element> elements = doc.select("entry");
	    	// Perhaps limit to MAX_RESULTS?
		    for (int i=0; i < elements.size(); i++) {
		    	Element e = elements.get(i);
		    	
	    		results.add(new Passage(
        			"bing",         	// Engine
        			e.select("d|Title").text(),	        // Title
        			e.select("d|Description").text(), // Full Text
        			e.select("d|Url").text())          // Reference
    				.score("BING_ANSWER_RANK", (double) i) // Score
    				.score("BING_ANSWER_PRESENT", 1.0)
    			);
	    	}
		    log.info("Retrieved " + elements.size() + " candidates from Bing.");
	    } catch (IOException e) {
	    	log.error("Error while searching with Bing. Ignoring. Details follow.");
	        log.error(e.getMessage());
	    }
	    return results;
	}
}
