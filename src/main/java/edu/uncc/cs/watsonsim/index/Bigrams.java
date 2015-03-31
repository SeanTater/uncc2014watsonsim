package edu.uncc.cs.watsonsim.index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import edu.uncc.cs.watsonsim.Passage;

/**
 * Count the bigrams in all passages for entropy based scorers
 * @author Sean Gallaghers
 */
public class Bigrams implements Segment {
	private final ConcurrentHashMap<String, Integer> unigrams = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Integer> bigrams = new ConcurrentHashMap<>();
	
	public Bigrams() {
	}

	@Override
	public void close() throws IOException {
		// Make space-separated lines
		List<String> lines = unigrams.entrySet().stream()
				.map((pair) ->
					pair.getKey() + " " + pair.getValue())
				.collect(Collectors.toList());
		Files.write(
				Paths.get("data", "unigrams"),
				lines);
		
		lines = bigrams.entrySet().stream()
				.map((pair) ->
					pair.getKey() + " " + pair.getValue())
				.collect(Collectors.toList());
		Files.write(
				Paths.get("data", "bigrams"),
				lines);
	}

	@Override
	public void accept(Passage t) {
		if (!t.tokens.isEmpty()) {
			unigrams.merge(t.tokens.get(0), 1, (a, b) -> a+b); 
		}
		for (int i=0; i < t.tokens.size() - 1; i++) {
			String key = t.tokens.get(i) + " " + t.tokens.get(i+1);
			bigrams.merge(key, 1, (a, b) -> a+b);
			unigrams.merge(t.tokens.get(i+1), 1, (a, b) -> a+b);
		}
	}

}
