package uncc2014watsonsim.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import privatedata.UserSpecificConstants;
import org.apache.http.client.fluent.*;
import org.apache.commons.codec.binary.Base64;
//import org.jsoup.Jsoup;


import uncc2014watsonsim.Answer;
		
public class BingSearcher extends Searcher {
	@Override
	public List<Answer> runQuery(String query) throws Exception {
		
		//TODO: Should this be done in StringUtils?
	    query = query.replaceAll(" ", "%20");
	    String url;
	    try {
	    	url = "https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Web?Query=%27" + query + "%27&$top=50&$format=Atom";
	    	String resp = Executor
	    		.newInstance()
	    		.auth(UserSpecificConstants.bingAPIKey, UserSpecificConstants.bingAPIKey)
	    		.execute(Request.Get(url))
	    		.returnContent()
	    		.asString();
		    // ?? conn.setRequestProperty("Accept", "application/json");
	    	
		    int count = 0;
		    List<String> Titles = new ArrayList<String>();
		    List<String> Descriptions = new ArrayList<String>();
		    List<String> Urls = new ArrayList<String>();
		    
		  //*************************Title*******************************		
		    List<Integer> startPositions = new ArrayList<Integer>();
		    Pattern ps = Pattern.compile("<d:Title");
		    Matcher ms = ps.matcher(resp);
		    while (ms.find() && count < MAX_RESULTS) {
		    	startPositions.add(ms.start());
		    	count++;
		    }
		    
		    count = 0;
		    List<Integer> endPositions = new ArrayList<Integer>();
		    Pattern pe = Pattern.compile("</d:Title>");
		    Matcher me = pe.matcher(resp);
		    while (me.find() && count < MAX_RESULTS) {
		    	endPositions.add(me.start());
		        count++;
		    }
		    
		    for (int x = 0; x < MAX_RESULTS; x++)
		    {
		    	Titles.add(resp.substring(startPositions.get(x)+29, endPositions.get(x)));
		    }
		    
		  //*************************Description*******************************		
		    count = 0;
		    startPositions = new ArrayList<Integer>();
		    ps = Pattern.compile("<d:Description");
		    ms = ps.matcher(resp);
		    while (ms.find() && count < MAX_RESULTS) {
		    	startPositions.add(ms.start());
		    	count++;
		    }
		    
		    count = 0;
		    endPositions = new ArrayList<Integer>();
		    pe = Pattern.compile("</d:Description>");
		    me = pe.matcher(resp);
		    while (me.find() && count < MAX_RESULTS) {
		    	endPositions.add(me.start());
		        count++;
		    }
		    
		    for (int x = 0; x < MAX_RESULTS; x++)
		    {
		    	Descriptions.add(resp.substring(startPositions.get(x)+35, endPositions.get(x)));
		    }
		    
		  //*************************URL*******************************		
		    count = 0;
		    startPositions = new ArrayList<Integer>();
		    ps = Pattern.compile("<d:Url");
		    ms = ps.matcher(resp);
		    while (ms.find() && count < MAX_RESULTS) {
		    	startPositions.add(ms.start());
		    	count++;
		    }
		    
		    count = 0;
		    endPositions = new ArrayList<Integer>();
		    pe = Pattern.compile("</d:Url>");
		    me = pe.matcher(resp);
		    while (me.find() && count < MAX_RESULTS) {
		    	endPositions.add(me.start());
		        count++;
		    }
		    
		    for (int x = 0; x < MAX_RESULTS; x++)
		    {
		    	Urls.add(resp.substring(startPositions.get(x)+27, endPositions.get(x)));
		    }
		    
		    for(int x = 0; x < Titles.size(); x++)
		    {
		    	System.out.println("Rank " + (x+1));
		    	System.out.println("Title: " + Titles.get(x));
		    	System.out.println("Description: " + Descriptions.get(x));
		    	System.out.println("Url: " + Urls.get(x));
		    	System.out.println();
		    }
		    
	    } catch (IOException e) {
	    	System.out.println("Error while searching with Bing. Ignoring. Details follow.");
	        e.printStackTrace();
	    }
	    return new ArrayList<Answer>();
	}
}
