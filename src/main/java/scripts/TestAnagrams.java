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
	
	public static void main(String args[]) throws IOException
	{
	Map<String,String> mp = new HashMap<>();
	BufferedReader br = new BufferedReader(new FileReader(new File("data/wordlist.txt")));
	String line;
	
	while((line = br.readLine())!= null)
	{
		//condition of different anagram questions:
		//usally anagram questions are coming for word coming after :
		// regular expression for searching if a : is coming in the question
		
		char[] charArray=line.toLowerCase().toCharArray();
		Arrays.sort(charArray);
		mp.put(line,String.valueOf(charArray));	
	}
	System.out.println("Enter the Jeopardy Anagram Question:");
    BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
    String question = br2.readLine();
    String command1="";
    
    	Pattern pattern = Pattern.compile("\"([A-z ]+)\"|: ([A-z ]+)");
    	 Matcher matcher = pattern.matcher(question);
    	
    	 if(matcher.find())
    	 {
    		 command1 = matcher.group(1);
    		 System.out.println(command1);
    		
    	 }
    	search_key(command1,mp);
    	// for generic case
    	String[] s = question.split(" ");
    	if(s.length<2)
    	{
    		// this words for questions like "Nuke Air" -> Ukariane no need to split 
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
