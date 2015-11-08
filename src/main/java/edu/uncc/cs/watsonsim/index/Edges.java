package edu.uncc.cs.watsonsim.index;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import org.apache.log4j.Logger;
import org.iq80.leveldb.*;
import org.junit.Test;

import static org.fusesource.leveldbjni.JniDBFactory.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.*;

import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.dcoref.Dictionaries.Animacy;
import edu.stanford.nlp.dcoref.Dictionaries.Gender;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.Triple;
import edu.uncc.cs.watsonsim.Database;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.nlp.Trees;

public class Edges implements Segment {
	private ConcurrentSkipListMap<String, Integer> all_edges = new ConcurrentSkipListMap<>();
	private final Logger log = Logger.getLogger(getClass());
	private final DB ldb;
	private final Database sqldb;
	
	public static final class Edge extends Triple<String, String, String> {
		public Edge(String a, String b, String c) {
			super(a, b, c);
		}
	}
	
	public Edges(Database sqldb) {
		
		// Setup LevelDB
		Options options = new Options();
		options.createIfMissing(true);
		try {
			ldb = factory.open(new File("data/edges-leveldb-depparse-lemma0"), options);
		} catch (IOException e) {
			// If we can't open the database we're toast.
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		// Setup the SQL db
		this.sqldb = sqldb;
		
	}
	
	/**
	 * Write the contents to disk
	 * @throws IOException
	 */
	public synchronized void flush() throws IOException {
		try (WriteBatch batch = ldb.createWriteBatch()) {
			// Take a snapshot
			ConcurrentSkipListMap<String, Integer> rels = all_edges;
			all_edges = new ConcurrentSkipListMap<>();
			System.out.println("writing out  " + rels.size() + " edges.");
			rels.forEach((key, value) -> {
				byte[] bkey = bytes(key);
				byte[] dbval = ldb.get(bkey);
				if (dbval != null)
					value += Integer.parseInt(asString(dbval));
				batch.put(bkey, bytes(value.toString()));
			});
			ldb.write(batch);
		}
	}

	@Override
	public synchronized void close() throws IOException {
		flush();
		
		/* Now populate the relational database using the leveldb
		 * This strange two-step process comes because:
		 *  1) Leveldb is about 10x faster for batched writes
		 *  2) Sqlite & Postgresql support concurrent readers
		 *  Otherwise, I would be thrilled to use either all the way.
		 */
		System.out.println("Pushing histograms into the main database.");
		try {
			sqldb.prep("DELETE FROM semantic_graph;").execute();
			sqldb.prep("PRAGMA synchronous=OFF;").execute();
			// source, tag, dest, count
			PreparedStatement graph = sqldb.prep("INSERT INTO semantic_graph VALUES (?, ?, ?, ?);");
			DBIterator i = ldb.iterator();
			i.seekToFirst(); // for() doesn't work
			Map.Entry<byte[],byte[]> entry;
			int queue=0;
			while ((entry = i.next()) != null) {
				String[] words = asString(entry.getKey()).split("\t");
				try {
					graph.setString(1, words[0]);
					graph.setString(2, words[1]);
					graph.setString(3, words[2]);
					graph.setInt(4, Integer.parseInt(asString(entry.getValue())));
					graph.addBatch();
					if (++queue % 1000000 == 0) {
						System.out.println("Enqueued " + queue + " rows");
						graph.executeBatch();
						//sqldb.commit();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			i.close();
			System.out.println("SQL batch " + graph.executeBatch().length);
		} catch (SQLException e) {
			e.printStackTrace();
			// Call it an IO exception
			throw new IOException(e);
		}	
	}
	
	/**
	 * Returns some new rules learned about a pronoun given its match
	 * context from anaphora resolution.
	 * 
	 * Specifically, we fill in the tags
	 * 
	 * _animate(main mention, ___).
	 * _gender(main mention, ___).
	 * _number(main mention, ___).
	 * 
	 * Basically, we can tell if it is animate, it's gender, and it's count.
	 * @return A list of semantic notes.
	 */
	public static List<Edge> generatePronounEdges(
			SemanticGraph g, IndexedWord w, Phrase t) {
		List<Edge> edges = new ArrayList<>();
		if (t.getUnpronoun().containsKey(w.index())) {
			// Use what we know about the pronoun
			Pair<CorefMention, CorefMention> mention_edge = t.getUnpronoun().get(w.index());
			String main_noun = Trees.concatNoun(g, g.getNodeByIndex(mention_edge.second.headIndex));
			
			Animacy is_animate = mention_edge.first.animacy;
			if (is_animate != Animacy.UNKNOWN) {
				edges.add(new Edge(
					main_noun, "_animate", is_animate.toString()));
			}
			
			Gender gender = mention_edge.first.gender;
			if (gender != Gender.UNKNOWN) {
				edges.add(new Edge(
					main_noun, "_gender", gender.toString()));
			}
			
			Dictionaries.Number number = mention_edge.first.number;
			if (number != Dictionaries.Number.UNKNOWN) {
				edges.add(new Edge(
					main_noun, "_number", number.toString()));
			}
		}
		return edges;
	}
	
	/**
	 * Get the full text of the main mention of a particular word, if it has a
	 * better mention. Otherwise just get it's segment of the tree using
	 * concatNoun()
	 * 
	 * @param phrase
	 * @param w
	 * @return
	 */
	public static String getMainMention(
			Phrase phrase, SemanticGraph graph, IndexedWord word) {
		Pair<CorefMention, CorefMention> linked_refs =
				phrase.getUnpronoun().get(word.index());
		if (linked_refs == null) {
			return Trees.concatNoun(graph, word);
		} else {
			return linked_refs.second.mentionSpan;
		}
	}

	
	/**
	 * Take a passage and find relevant semantic edges in it.
	 * 
	 * 1) We know that handling these words one at a time yields very
	 * boring results. We can connect "Donald" with "Duck," which is neat, but
	 * we can't tell anything Donald does that other ducks do not because any
	 * verb will be attached to "duck" but know nothing about Donald.
	 * 
	 * So to solve this, we connect all the [nn, cd] links, and invert
	 * "prep_of" links, prepending them. (This is the Trees.concatNoun method).
	 * How many of these are worth joining is up for debate. But it has to be
	 * consistent for indexing and later querying. Suppose we found "Donald
	 * duck is a cool cartoon character." We'll get a higher level relation
	 * like nsubj("Donald duck", "cartoon character") -> 1
	 * 
	 * 2) We find that a lot of the links are to pronouns. So in the links,
	 * we replace the pronouns with their "representative mentions", using
	 * CoreNLP's dcoref.
	 * 
	 * 3) For good measure, we include a few fake tags to indicate other
	 * tidbits we learned about relations. For example, we know gender
	 * and animation based on the pronouns used with something.
	 * 
	 * 	tag      | meaning
	 *  -------------------
	 *  _gender  | he / she, if available
	 *  _animate | (he/she), it
	 *  _number  | how many there are
	 *  _isa     | lexical type
	 * 
	 * 
	 * ## Possible investigation for later
	 * We can also join relations. Where we have relations that look like this:
	 * 
	 *  tagname(words, words)
	 *  
	 * We may want something that looks more like this:
	 * 
	 *  tagname [word tagname]* (words, words)
	 *  
	 * That way we can bridge across common concepts and get to the more
	 * interesting links they bridge. It sounds logical to me but I can't come
	 * up with any convincing examples where it would actually be useful to
	 * know, and I seem to find many transitive connections that are
	 * irrelevant.
	 * 
	 */
	public static List<Edge> generateEdges(Phrase phrase) {
		List<Edge> edges = new ArrayList<>();
		phrase.getGraphs().forEach(g -> {
			g.edgeIterable().forEach(e -> {
				if (e.getRelation().getShortName() != "nn") {
					// "nn" is garbled by the concatNoun() anyway
					
					// Dcoref on the source and target
					//edges.addAll(generatePronounEdges(g, e.getSource(), phrase));
					
					// Find the main mention and optionally concat it 
					String source = getMainMention(phrase, g, e.getSource());
					String target = getMainMention(phrase, g, e.getTarget());
					
					edges.add(new Edge(
							source,
							Trees.getSpecificPreps(e.getRelation()),
							target));
				}
			});
		});
		
		// Also extract the types while we are at it.
		/*SupportCandidateType.extract(phrase).forEach(nt -> {
			edges.add(new Edge(nt.first, "_isa", nt.second));
		});*/
		return edges;
	}
	
	@Override
	/**
	 * Stores the edges resulting from generateEdges into a database,
	 * delimiting the keys by tabs, since spaces are taken by concatNoun().
	 */
	public void accept(Passage t) {
		generateEdges(t).forEach(edge -> {
			all_edges.merge(
					edge.first + "\t"
						+ edge.second + "\t"
						+ edge.third,
					1,
					(a, b) -> a+b);
		});
			
		// Try to keep it from absorbing all available memory
		if (all_edges.size() > 1_000_000)
			try {
				flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
	

	@Test
	public void testGenerateEdges() {
		Phrase p = new Phrase("This is an example.");
		assertEquals(null, Edges.generateEdges(p));
		fail("Not yet implemented");
	}

}
