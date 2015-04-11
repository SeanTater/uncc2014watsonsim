package edu.uncc.cs.watsonsim.index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import edu.stanford.nlp.util.IterableIterator;
import edu.uncc.cs.watsonsim.Passage;

/**
 * Count the bigrams in all passages for entropy based scorers
 * @author Sean Gallaghers
 */
public class Bigrams implements Segment {
	private ConcurrentHashMap<String, Integer> unigrams = new ConcurrentHashMap<>(1_000_000, (float) 0.75, 50);
	private ConcurrentHashMap<String, Integer> bigrams = new ConcurrentHashMap<>(1_000_000, (float) 0.75, 50);
	private final Logger log = Logger.getLogger(getClass());
	
	public Bigrams() {
	}

	@Override
	public void close() throws IOException {
		flush();
	}
	
	public void flush() throws IOException {
		// Make space-separated lines
		Stream<String> lines = unigrams.entrySet().stream()
				.map((pair) ->
					pair.getKey() + " " + pair.getValue());
		unigrams= new ConcurrentHashMap<>(1_000_000, (float) 0.75, 50);
		Files.write(
				Paths.get("/media/sean/DATA", "unigrams"),
				new IterableIterator<String>(lines.iterator()),
				StandardOpenOption.CREATE,
				StandardOpenOption.WRITE,
				StandardOpenOption.APPEND);
		// Make space-separated lines
		lines = bigrams.entrySet().stream()
				.map((pair) ->
					pair.getKey() + " " + pair.getValue());
		bigrams =new ConcurrentHashMap<>(1_000_000, (float) 0.75, 50);
		Files.write(
				Paths.get("/media/sean/DATA", "bigrams"),
				new IterableIterator<String>(lines.iterator()),
				StandardOpenOption.CREATE,
				StandardOpenOption.WRITE,
				StandardOpenOption.APPEND);
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
		// Try to keep it from absorbing all available memory
		if (unigrams.size() > 1_000_000
				|| bigrams.size() > 1_000_000) {
			try {
				flush();
			} catch (IOException failed_flush) {
				log.error(failed_flush);
			}
		}
	}

}
