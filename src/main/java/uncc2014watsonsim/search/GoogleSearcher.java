package uncc2014watsonsim.search;

/*
 * Java Imports
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import privatedata.UserSpecificConstants;


import uncc2014watsonsim.Answer;

/*
 * Google API Imports
 */
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.Customsearch.Cse;
import com.google.api.services.customsearch.CustomsearchRequestInitializer;
import com.google.api.services.customsearch.model.Result;

/**
 * @author Jonathan Shuman
 * @purpose To use Google's CustomSearch API to integrate
 * 			 with ITCS 4010 Watson Class at UNCC
 *
 */
public class GoogleSearcher extends Searcher {
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	//Initialized Key
	static final GoogleClientRequestInitializer KEY_INITIALIZER =
        new CustomsearchRequestInitializer(UserSpecificConstants.googleAPIKey);
	static final Cse customsearchengine;
	static {
		/*
		 * Build a customsearch object and initialize it with a
		 * builder object.
		 * We will use a Json Factory for data transport.
		 */
		customsearchengine = new Customsearch.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, null)
		.setApplicationName(UserSpecificConstants.googleApplicationName)
		.setGoogleClientRequestInitializer(KEY_INITIALIZER)
		.build().cse();
	}
	
	/* (non-Javadoc)
	 * @see WebSearch#runQuery(java.lang.String)
	 */
	public List<Answer> runQuery(String query) throws IOException {
		List<Answer> results = new ArrayList<Answer>();
		//Check empty query
		if (query.isEmpty())
			return results;
	   /* 
		* This is a Customsearch.cse.List object, not a Java List object.
		*  This object represents a search which has not yet been retrieved.
		*  
		*  We set the cx or search engine ID to a custom engine created for
		*  the purpose of this project which essentially just searches the
		*  entire web.
		*/
		Cse.List queryList = customsearchengine.list(query);
		
		queryList.setCx(UserSpecificConstants.googleCustomSearchID);
        // To choose how many results: queryList.setNum(new Long((long)30));
		List<Result> in_r = queryList.execute().getItems();
		// Not a range for because we need rank
		for (int i=0; i<in_r.size(); i++) {
			results.add(new Answer(
				"google",  // Title 
				in_r.get(i).getTitle(),// "Full" Text
				in_r.get(i).getSnippet(), // Reference
				in_r.get(i).getFormattedUrl(),                // Engine
				i,                       // Rank 
				(double) i
				));
		}
		return results; 
	}

}
