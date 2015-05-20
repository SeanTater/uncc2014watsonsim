package edu.uncc.cs.watsonsim.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Log;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Score;
import edu.uncc.cs.watsonsim.scorers.Merge;

public class Anagrams extends Searcher {

	private final Map<String, List<String>> mp = new HashMap<>();
	private Log log;

	public Anagrams(Environment env)  {
		super(env);
		log = env.log.kid(getClass());
		try
		{
		for (String line : Files.readAllLines(Paths.get("data", "words"))) {
			// condition of different anagram questions:
			// usually anagram questions are coming for word coming after :
			// regular expression for searching if a : is coming in the question

			char[] charArray = line.toLowerCase().toCharArray();
			Arrays.sort(charArray);
			String source = String.valueOf(charArray);
			List<String> targets = mp.get(source);
			if (targets == null) {
				targets = new ArrayList<>();
				mp.put(source, targets);
			}
			targets.add(line);
		}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		Score.register("IS_ONLY_ANAGRAM", 0.0, Merge.Min);
	}

	public static void main(String args[]) throws IOException {
		Anagrams ta = new Anagrams(new Environment());
		System.out.println("Enter the Jeopardy Anagram Question:");
		BufferedReader br2 = new BufferedReader(
				new InputStreamReader(System.in));
		String question = br2.readLine();
		ta.query(question);
	}

	public static List<String> search_key(String keys,Map<String, List<String>> mp) 
	{
		char[] charArray = keys.toLowerCase().toCharArray();
		Arrays.sort(charArray);
		// String searchKey = String.valueOf(charArray);
		List<String> entries = mp.get(String.valueOf(charArray));
		if (entries == null)
		{
			entries = new ArrayList<>();
		}
		entries.remove(keys);
		return entries;
	}

	@Override
	public List<Passage> query(String query) {
		// Some anagrams come in a very clear syntax:
		//    either in quotes, or after a colon. Find them.
		Matcher matcher = Pattern.compile("\"([A-z ]+)\"|: ([A-z ]+)")
				.matcher(query);
		
		List<String> entries = new ArrayList<>();
		if (matcher.find() && matcher.group(1) != null) {
			// Good news. We found a quoted string to generate anagrams from.
			entries.addAll(search_key(matcher.group(1), mp));
			if (!entries.isEmpty()) {
				log.info("Found " + entries.size()
						+ " quoted anagrams");	
			}
		} else {
			// Bad news. We have to guess all the words.
			String[] words = query.split(" ");
			if (words.length <= 2) {
				// When there are so few words, the whole question is likely 
				// an anagram. For example, "Nuke Air" -> "Ukariane"
				entries.addAll(search_key(query.replace(" ", ""), mp));
			} else {
				// Otherwise, consider each word separately.
				for (String word : words) {
					entries.addAll(search_key(word, mp));
				}
			}
		}
		
		entries.removeAll(Arrays.asList("Si","shit","Ni"));
		
		List<Passage> results = new ArrayList<>();
		for (String text : entries) {
			results.add(new edu.uncc.cs.watsonsim.Passage("lucene", // Engine
					text, // Title
					text, // Text
					"anagram:" + text).score("IS_ONLY_ANAGRAM", 1.0));

		}
		
		
		
		return results;
	}
}
