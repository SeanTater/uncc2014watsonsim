package uncc2014watsonsim.search;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import privatedata.UserSpecificConstants;

import org.apache.http.client.fluent.*;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import uncc2014watsonsim.PassageRef;
import uncc2014watsonsim.scorers.Scored;

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
	
	public List<Scored<PassageRef>> query(String query) {
		
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

	    List<Scored<PassageRef>> results = new ArrayList<>();
	    try {
	    	String resp = Executor
	    		.newInstance()
				.auth(UserSpecificConstants.bingAPIKey, UserSpecificConstants.bingAPIKey)
	    		.execute(Request.Get(uri))
	    		.returnContent().asString();
	    	
	    	Document doc = Jsoup.parse(resp);
	    	List<Element> elements = doc.select("entry");
	    	// Perhaps limit to MAX_RESULTS?
		    for (int i=0; i < elements.size(); i++) {
		    	Element e = elements.get(i);
	    		Scored<PassageRef> sref = Scored.mzero(new PassageRef(
        			"bing",         	// Engine
        			"URL:" + e.select("d|Url").text(), // Reference
        			Optional.of(e.select("d|Title").text()),	        // Title
        			Optional.of(e.select("d|Description").text()) // Full Text
        			));
    			sref.put("BING_RANK", (double) i);
	    		results.add(sref);
	    	}
		    System.out.print("B!");
	    } catch (IOException e) {
	    	System.out.println("Error while searching with Bing. Ignoring. Details follow.");
	        e.printStackTrace();
	    }
	    return results;
	}
}
