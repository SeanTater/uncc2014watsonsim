package scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uncc2014watsonsim.StringUtils;

public class TestAnagrams  {
	
	public class Candidate {
		public final String text;
		public final boolean in_quotes;
		public Candidate(String text, boolean in_quotes) {
			this.text = text;
			this.in_quotes = in_quotes;
		}
	}
	
	public static void main(String args[]) throws IOException
	{
	ArrayList<String> wordList = new ArrayList<>();
	ArrayList<String> sortedList = new ArrayList<>();
	Map<String,String> mp = new HashMap<>();
	BufferedReader br = new BufferedReader(new FileReader(new File("wordlist.txt")));
	String line;
	
	while((line = br.readLine())!= null)
	{
		//condition of different anagram questions:
		//usally anagram questions are coming for word coming after :
		// regular expression for searching if a : is coming in the question
		
		char[] charArray=line.toLowerCase().toCharArray();
		Arrays.sort(charArray);
		//System.out.println(String.valueOf(charArray));
		mp.put(line,String.valueOf(charArray));	
	}
	System.out.println("Enter the Jeopardy Anagram Question:");
    BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
    String question = br2.readLine();
    String command;
    command = StringUtils.filterRelevant(question);
    
    
    String command1="";
    if (question.contains(":")||question.contains("\""))
	{
    	Pattern pattern = Pattern.compile("\"([A-z ]+)\"|: ([A-z ]+)");
    	 Matcher matcher = pattern.matcher(question);
    	
    	 if(matcher.find())
    	 {
    		 command1 = matcher.group(1);
    		 System.out.println(command1);
    		
    	 }
    	/*command1 = question.replace(" ","");
    	command1 = command1.replace(",","");
    	String line1 = null;
    	if(question.contains(":"))
         line1 = command1.substring(command1.indexOf(":")+1);
    	else if(question.contains("\""))
    	 line1 = command1.substring(command1.indexOf("\"")+1,command1.lastIndexOf("\""));
    	command1 = line1;*/
    	search_key(command1,mp);
	}
  
    //for every 
    	String[] s = question.split(" ");
    	if(s.length<2)
    	{
    		question  = question.replace(" ","");
    		search_key(question,mp);
    	}
    	
    	else
    	{
    	for(String s1:s)
    	{
    		search_key(s1,mp); 
    	}
    }
    }
   
	public static void search_key(String keys, Map<String,String> mp)
	{
		char[] charArray=keys.toLowerCase().toCharArray();
		Arrays.sort(charArray);
		String searchKey =  String.valueOf(charArray);
		
		Set<Map.Entry<String, String>> entries = mp.entrySet();
        for(Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(searchKey.equals(value))
            {
            	if(!key.equals(keys))
            	System.out.println(key);
            }
        }
	}
}
