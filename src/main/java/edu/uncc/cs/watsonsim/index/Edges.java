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
import org.iq80.leveldb.*;
import static org.fusesource.leveldbjni.JniDBFactory.*;
import java.io.*;

import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.IterableIterator;
import edu.uncc.cs.watsonsim.Passage;

public class Edges implements Segment {
	private ConcurrentSkipListMap<String, Integer> relations = new ConcurrentSkipListMap<>();
	private final Logger log = Logger.getLogger(getClass());
	private DB db;
	
	public Edges() {
		Options options = new Options();
		options.createIfMissing(true);
		try {
			db = factory.open(new File("data/edges-leveldb"), options);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void flush() throws IOException {
		try (WriteBatch batch = db.createWriteBatch()) {
			// Take a snapshot
			ConcurrentSkipListMap<String, Integer> rels = relations;
			relations = new ConcurrentSkipListMap<>();
			
			rels.forEach((key, value) -> {
				byte[] bkey = bytes(key);
				byte[] dbval = db.get(bkey);
				if (dbval != null)
					value += Integer.parseInt(asString(dbval));
				batch.put(bkey, bytes(value.toString()));
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
			db.write(batch);
		}
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
		if (relations.size() > 100_000)
			try {
				flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}

}
