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

import org.apache.commons.codec.binary.Base64;
//import org.jsoup.Jsoup;


import uncc2014watsonsim.Answer;
		
public class BingSearcher extends Searcher {
	@Override
	public List<Answer> runQuery(String query) throws Exception {
		
		//TODO: Should this be done in StringUtils?
	    query = query.replaceAll(" ", "%20");
	    byte[] accountKeyBytes = Base64.encodeBase64((UserSpecificConstants.bingAPIKey + ":" + UserSpecificConstants.bingAPIKey).getBytes());
	    String accountKeyEnc = new String(accountKeyBytes);
	    URL url;
	    try {
	    	url = new URL("https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Web?Query=%27" + query + "%27&$top=50&$format=Atom");
	    	
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.setRequestMethod("GET");
		    conn.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
		    conn.setRequestProperty("Accept", "application/json");
		    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    StringBuilder sb = new StringBuilder();
		    String output;
		    char[] buffer = new char[4096];
		    
		    while ((output = br.readLine()) != null) {
		        sb.append(output);
		    }
		   
		    conn.disconnect(); 
	    	
		    int count = 0;
		    List<String> Titles = new ArrayList<String>();
		    List<String> Descriptions = new ArrayList<String>();
		    List<String> Urls = new ArrayList<String>();
		    
		  //*************************Title*******************************		
		    List<Integer> startPositions = new ArrayList<Integer>();
		    Pattern ps = Pattern.compile("<d:Title");
		    Matcher ms = ps.matcher(sb);
		    while (ms.find() && count < MAX_RESULTS) {
		    	startPositions.add(ms.start());
		    	count++;
		    }
		    
		    count = 0;
		    List<Integer> endPositions = new ArrayList<Integer>();
		    Pattern pe = Pattern.compile("</d:Title>");
		    Matcher me = pe.matcher(sb);
		    while (me.find() && count < MAX_RESULTS) {
		    	endPositions.add(me.start());
		        count++;
		    }
		    
		    for (int x = 0; x < MAX_RESULTS; x++)
		    {
		    	sb.getChars(startPositions.get(x)+29, endPositions.get(x), buffer, 0);
		    	Titles.add(new String(buffer));
		    }
		    
		  //*************************Description*******************************		
		    count = 0;
		    buffer = new char[4096];
		    startPositions = new ArrayList<Integer>();
		    ps = Pattern.compile("<d:Description");
		    ms = ps.matcher(sb);
		    while (ms.find() && count < MAX_RESULTS) {
		    	startPositions.add(ms.start());
		    	count++;
		    }
		    
		    count = 0;
		    endPositions = new ArrayList<Integer>();
		    pe = Pattern.compile("</d:Description>");
		    me = pe.matcher(sb);
		    while (me.find() && count < MAX_RESULTS) {
		    	endPositions.add(me.start());
		        count++;
		    }
		    
		    for (int x = 0; x < MAX_RESULTS; x++)
		    {
		    	sb.getChars(startPositions.get(x)+35, endPositions.get(x), buffer, 0);
		    	Descriptions.add(new String(buffer));
		    }
		    
		  //*************************URL*******************************		
		    count = 0;
		    buffer = new char[4096];
		    startPositions = new ArrayList<Integer>();
		    ps = Pattern.compile("<d:Url");
		    ms = ps.matcher(sb);
		    while (ms.find() && count < MAX_RESULTS) {
		    	startPositions.add(ms.start());
		    	count++;
		    }
		    
		    count = 0;
		    endPositions = new ArrayList<Integer>();
		    pe = Pattern.compile("</d:Url>");
		    me = pe.matcher(sb);
		    while (me.find() && count < MAX_RESULTS) {
		    	endPositions.add(me.start());
		        count++;
		    }
		    
		    for (int x = 0; x < MAX_RESULTS; x++)
		    {
		    	sb.getChars(startPositions.get(x)+27, endPositions.get(x), buffer, 0);
		    	Urls.add(new String(buffer));
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
