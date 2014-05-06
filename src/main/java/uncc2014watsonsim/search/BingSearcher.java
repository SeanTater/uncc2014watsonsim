package uncc2014watsonsim.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import privatedata.UserSpecificConstants;

import org.apache.http.client.fluent.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import uncc2014watsonsim.Passage;

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
	
	public List<Passage> query(String query) {
		//TODO: Should this be done in StringUtils?
	    query = query.replaceAll(" ", "%20");
	    String url = "https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Web?Query=%27" + query + "%27&$top=50&$format=Atom";

	    List<Passage> results = new ArrayList<Passage>();
	    try {
	    	String resp = Executor
	    		.newInstance()
	    		.auth(UserSpecificConstants.bingAPIKey, UserSpecificConstants.bingAPIKey)
	    		.execute(Request.Get(url))
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
    				.score("BING_RANK", (double) i) // Score
    			);
	    	}
		    System.out.print("B!");
	    } catch (IOException e) {
	    	System.out.println("Error while searching with Bing. Ignoring. Details follow.");
	        e.printStackTrace();
	    }
	    return results;
	}
}
