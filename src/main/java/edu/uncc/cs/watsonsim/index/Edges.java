package edu.uncc.cs.watsonsim.index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.uncc.cs.watsonsim.Passage;

public class Edges implements Segment {
	private final ConcurrentHashMap<String, Integer> relations = new ConcurrentHashMap<>();
	public Edges() {
		
	}
	
	public void flush() throws IOException {
		// Make space-separated lines
		List<String> lines = relations.entrySet().stream()
				.map((pair) ->
					pair.getKey() + " " + pair.getValue())
				.collect(Collectors.toList());
		relations.clear();
		Files.write(
				Paths.get("data", "edges"),
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
			for (SemanticGraphEdge e : g.edgeIterable())
				relations.merge(
						e.getSource().word() + " "
							+ e.getRelation().getShortName() + " "
							+ e.getTarget().word(),
						1,
						(a, b) -> a+b);
		if (relations.size() > 100_000_000) {
			try {
				flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
