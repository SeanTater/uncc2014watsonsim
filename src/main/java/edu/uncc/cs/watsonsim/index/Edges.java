package edu.uncc.cs.watsonsim.index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.IterableIterator;
import edu.uncc.cs.watsonsim.Passage;

public class Edges implements Segment {
	private ConcurrentHashMap<String, Integer> relations = new ConcurrentHashMap<>(1_000_000, (float) 0.75, 50);
	private final Logger log = Logger.getLogger(getClass());
	
	public void flush() throws IOException {
		// Make space-separated lines
		IterableIterator<String> lines = new IterableIterator<>(
				relations.entrySet().stream()
				.map((pair) ->
					pair.getKey() + " " + pair.getValue()).iterator());
		relations = new ConcurrentHashMap<>(1_000_000, (float) 0.75, 50);
		Files.write(
				Paths.get("/media/sean/DATA", "edges"),
				lines,
				StandardOpenOption.CREATE,
				StandardOpenOption.WRITE,
				StandardOpenOption.APPEND);
	}

	@Override
	public void close() throws IOException {
		flush();
	}

	@Override
	public void accept(Passage t) {
		for (SemanticGraph g : t.graphs)
			for (SemanticGraphEdge e : g.edgeIterable()) {
				// These lines make "prep_of," "conj_and" and friends
				String rel_name = e.getRelation().getShortName();
				if (e.getRelation().getSpecific() != null) {
					rel_name += "_" + e.getRelation().getSpecific();
				}
				relations.merge(
						e.getSource().word() + " "
							+ rel_name + " "
							+ e.getTarget().word(),
						1,
						(a, b) -> a+b);
			}
		// Try to keep it from absorbing all available memory
		if (relations.size() > 1_000_000) {
			try {
				flush();
			} catch (IOException failed_flush) {
				log.error(failed_flush);
			}
		}
	}

}
