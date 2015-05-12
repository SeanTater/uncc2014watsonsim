package edu.uncc.cs.watsonsim.index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.fusesource.lmdbjni.BufferCursor;
import org.fusesource.lmdbjni.Database;
import org.fusesource.lmdbjni.Env;
import org.fusesource.lmdbjni.Transaction;

import static org.fusesource.lmdbjni.Constants.*;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.IterableIterator;
import edu.uncc.cs.watsonsim.Passage;

public class Edges implements Segment {
	private Env lmdb_env = new Env("data/edges-db"); 
	private Database lmdb = lmdb_env.openDatabase();
	private ConcurrentSkipListMap<String, Integer> relations = new ConcurrentSkipListMap<>();
	private final Logger log = Logger.getLogger(getClass());
	
	public void flush() {
		// Take a snapshot
		ConcurrentSkipListMap<String, Integer> rels = relations;
		relations = new ConcurrentSkipListMap<>();
		/*
		 * This feature does not come until 0.4.0 which is not on maven central
		
		try (Transaction tx = lmdb_env.createWriteTransaction();
				BufferCursor cursor = lmdb.bufferCursor(tx)){
		*/
		rels.forEach((key, value) -> {
			byte[] bkey = bytes(key);
			if (lmdb.get(bkey) != null)
				value += Integer.parseInt(string(lmdb.get(bkey)));
			lmdb.put(bkey, bytes(value.toString()));
			
			
			/* Sequential, but inserts don't make sense
			byte[] bkey = bytes(key);
			cursor.seek(bkey);
			
			if (Arrays.equals(bkey, cursor.keyBytes())) {
				cursor.valWriteInt(cursor.valInt(0) + value);
			} else {
				cursor.valWriteInt(value);
			}
			cursor.put();
			*/
		});
		/*
		// Make space-separated lines
		IterableIterator<String> lines = new IterableIterator<>(
				relations.entrySet().stream()
				.map((pair) ->
					pair.getKey() + " " + pair.getValue()).iterator());
		relations = new ConcurrentHashMap<>(100_000, (float) 0.75, 50);
		
		Files.write(
				Paths.get("/media/sean/DATA", "edges"),
				lines,
				StandardOpenOption.CREATE,
				StandardOpenOption.WRITE,
				StandardOpenOption.APPEND);*/
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
						e.getSource().lemma() + " "
							+ rel_name + " "
							+ e.getTarget().lemma(),
						1,
						(a, b) -> a+b);
			}
		// Try to keep it from absorbing all available memory
		if (relations.size() > 100_000) flush();
	}

}
