package uncc2014watsonsim;

/*
 * Java Imports
 */
import java.io.IOException;
import java.util.ArrayList;



/*
 * Google API Imports
 */
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.Customsearch.Cse.List;
import com.google.api.services.customsearch.CustomsearchRequestInitializer;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

/**
 * @author Jonathan Shuman
 * @purpose To use Google's CustomSearch API to integrate
 * 			 with ITCS 4010 Watson Class at UNCC
 *
 */
public class WebSearchGoogle implements WebSearch {
	
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	
	//Initialized Key
	private static final GoogleClientRequestInitializer KEY_INITIALIZER =
                new CustomsearchRequestInitializer(PrivateGoogleCredentials.googleAPIKey);
	
	// Store results in object's own array
	private ArrayList<Result> resultsList = new ArrayList<Result>();
	
	/* (non-Javadoc)
	 * @see WebSearch#runQuery(java.lang.String)
	 */
	@Override
	public void runQuery(String query) {
		//Check empty query
		if(query == ""){
			System.out.println("Google Search: No query entered");
			return;
		}
		/*
		 * Build a customsearch object and initialize it with a
		 * builder object.
		 * We will use a Json Factory for data transport.
		 * 
		 */
		Customsearch customsearch = new Customsearch.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, null)
		.setApplicationName(PrivateGoogleCredentials.googleApplicationName)
		.setGoogleClientRequestInitializer(KEY_INITIALIZER)
		.build();
		
		try {
		   /* 
			* This is a Customsearch.cse.List object, not a Java List object.
			*  This object represents a search which has not yet been retrieved.
			*  
			*  We set the cx or search engine ID to a custom engine created for
			*  the purpose of this project which essentially just searches the
			*  entire web.
			*/
			List queryList = customsearch.cse().list(query);
			
			queryList.setCx(PrivateGoogleCredentials.googleCustomSearchID);
                       // queryList.setNum(new Long((long)30));
			Search results = queryList.execute(); //Fetch Data
			resultsList = (ArrayList<Result>) results.getItems(); //Store the data in our object as a Java ArrayList
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return; 

	}

	/* (non-Javadoc)
	 * @see WebSearch#getTitle(int)
	 */
	@Override
	public String getTitle(int index) {
		String title = "";
		try{
			//Get Google Result and Title for index.
			Result result = resultsList.get(index);
			title = result.getTitle();
		}catch(IndexOutOfBoundsException e){
			//Throws out of range
			System.out.println("Google Search: Index out of range!");
			return null;
		}
		return title;
	}

	/* (non-Javadoc)
	 * @see WebSearch#getDocumentPointer(int)
	 */
	@Override
	public String getDocumentPointer(int index) {
		String url = "";
		try{
			//Get Google Result and URL for index.
			Result result = resultsList.get(index);
			url = result.getFormattedUrl();
		}catch(IndexOutOfBoundsException e){
			//Throws out of range
			System.out.println("Google Search: Index out of range!");
			return null;
		}
		return url;
	}

	/* (non-Javadoc)
	 * @see WebSearch#getResultCount()
	 */
	@Override
	public int getResultCount() {
		return resultsList.size();
	}


}
