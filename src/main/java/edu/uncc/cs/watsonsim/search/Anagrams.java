package edu.uncc.cs.watsonsim.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.StringUtils;

public class Anagrams extends Searcher {

	private final Map<String, List<String>> mp = new HashMap<>();

	public Anagrams()  {
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
	}

	public static void main(String args[]) throws IOException {
		Anagrams ta = new Anagrams();
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
		String command1 = "";

		Pattern pattern = Pattern.compile("\"([A-z ]+)\"|: ([A-z ]+)");
		Matcher matcher = pattern.matcher(query);

		if (matcher.find() && matcher.group(1) != null) {
			command1 = matcher.group(1);
			System.out.println(command1);
		}
		List<String> entries = search_key(command1, mp);
		// for generic case
		String[] s = query.split(" ");
		if (s.length < 2) {
			// this words for questions like "Nuke Air" -> Ukariane no need to
			// split
			query = query.replace(" ", "");
			entries.addAll(search_key(query, mp));
		} else {
			for (String s1 : s) {
				entries.addAll(search_key(s1, mp));
			}
		}
		List<Passage> results = new ArrayList<>();
		for (String text : entries) {
			results.add(new edu.uncc.cs.watsonsim.Passage("lucene", // Engine
					text, // Title
					text, // Text
					"anagram:" + text));

		}
		return results;
	}
}
