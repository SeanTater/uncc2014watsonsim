import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
//import org.jsoup.Jsoup;
		
public class Overlord {
	public static void main(String[] foo)
	{
	    //--------------------------------------Bing search------------------------------
		 Scanner k = new Scanner(System.in);
		 System.out.println("Enter search text: ");
	     String searchText = k.nextLine();
	     System.out.println();
		
	    //String searchText = "tallest building in the world";
	    searchText = searchText.replaceAll(" ", "%20");
	    String accountKey="7zBtncCFxsW/JhUgHDHm56q/+21vN3JGRtGkNV8ykSg";
	  
	    byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
	    String accountKeyEnc = new String(accountKeyBytes);
	    URL url;
	    try {
	    	url = new URL("https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Web?Query=%27" + searchText + "%27&$top=50&$format=Atom");
	    	
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
		    int indexNumber = 50;//number of results 
		    String[] Titles = new String[indexNumber];
		    String[] Descriptions = new String[indexNumber];
		    String[] Urls = new String[indexNumber];
		    
		  //*************************Title*******************************		
		    Integer[] startPositions = new Integer[indexNumber];
		    Pattern ps = Pattern.compile("<d:Title");
		    Matcher ms = ps.matcher(sb);
		    while (ms.find() && count < indexNumber) {
		    	startPositions[count] = ms.start();
		    	count++;
		    }
		    
		    count = 0;
		    Integer[] endPosistions = new Integer[indexNumber];
		    Pattern pe = Pattern.compile("</d:Title>");
		    Matcher me = pe.matcher(sb);
		    while (me.find() && count < indexNumber) {
		    	endPosistions[count] = me.start();
		        count++;
		    }
		    
		    for (int x = 0; x < indexNumber; x++)
		    {
		    	sb.getChars(startPositions[x]+29, endPosistions[x], buffer, 0);
		    	Titles[x] = new String(buffer);
		    }
		    
		  //*************************Description*******************************		
		    count = 0;
		    buffer = new char[4096];
		    startPositions = new Integer[indexNumber];
		    ps = Pattern.compile("<d:Description");
		    ms = ps.matcher(sb);
		    while (ms.find() && count < indexNumber) {
		    	startPositions[count] = ms.start();
		    	count++;
		    }
		    
		    count = 0;
		    endPosistions = new Integer[indexNumber];
		    pe = Pattern.compile("</d:Description>");
		    me = pe.matcher(sb);
		    while (me.find() && count < indexNumber) {
		    	endPosistions[count] = me.start();
		        count++;
		    }
		    
		    for (int x = 0; x < indexNumber; x++)
		    {
		    	sb.getChars(startPositions[x]+35, endPosistions[x], buffer, 0);
		    	Descriptions[x] = new String(buffer);
		    }
		    
		  //*************************URL*******************************		
		    count = 0;
		    buffer = new char[4096];
		    startPositions = new Integer[indexNumber];
		    ps = Pattern.compile("<d:Url");
		    ms = ps.matcher(sb);
		    while (ms.find() && count < indexNumber) {
		    	startPositions[count] = ms.start();
		    	count++;
		    }
		    
		    count = 0;
		    endPosistions = new Integer[indexNumber];
		    pe = Pattern.compile("</d:Url>");
		    me = pe.matcher(sb);
		    while (me.find() && count < indexNumber) {
		    	endPosistions[count] = me.start();
		        count++;
		    }
		    
		    for (int x = 0; x < indexNumber; x++)
		    {
		    	sb.getChars(startPositions[x]+27, endPosistions[x], buffer, 0);
		    	Urls[x] = new String(buffer);
		    }
		    
		    for(int x = 0; x < Titles.length; x++)
		    {
		    	System.out.println("Rank " + (x+1));
		    	System.out.println("Title: " + Titles[x]);
		    	System.out.println("Description: " + Descriptions[x]);
		    	System.out.println("Url: " + Urls[x]);
		    	System.out.println();
		    }
		    
	    } catch (MalformedURLException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}
}
