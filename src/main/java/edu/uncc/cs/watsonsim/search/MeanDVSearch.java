package edu.uncc.cs.watsonsim.search;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fusesource.lmdbjni.BufferCursor;
import org.fusesource.lmdbjni.Database;
import org.fusesource.lmdbjni.Env;
import org.fusesource.lmdbjni.Transaction;
import static org.fusesource.lmdbjni.Constants.*;

import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.KV;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.nlp.DenseVectors;

public class MeanDVSearch extends Searcher {
	private static final int K = 20;  // How many results to return
	private static final int N = DenseVectors.N; // Dimensions in a dense vector
	private static final int LEN = K+1; // How many entries in a result vector
	private static final String wiki_vectors_location = "data/wiki-vectors.lmdb";
	private static Env wiki_vectors_env = new Env();
	static {
		wiki_vectors_env.open(wiki_vectors_location);
	}
	
	public MeanDVSearch(Environment env) {
		super(env);
	}
	
	/**
	 * Pick the top K of M results, for tiny K (20) and huge M (like 50mil)
	 * @param sims		Array of similarities (looking for max)
	 * @param names		Array of utf8-names
	 * @param sim		This similarity
	 * @param cursor	The cursor to copy the name from, if necessary
	 * 
	 * This is based on bubble sort, because we know nearly every entry will
	 * be worse than the worst of sims, and this is both simple and has nice
	 * best-case complexity.
	 */
	private static void bubble(double[] sims, byte[][] names, double this_sim, BufferCursor cursor) {
		// Bubble up the list as far as necessary
		// Trick: the array is one longer than necessary
		// That way there is no special case at the end.
		int i = K-1;
		for (; this_sim >= sims[i] && i>=0; i--) {
			// Still percolating upward?
			// Shift this entry down
			sims[i+1] = sims[i];
			// Shift the name too
			names[i+1] = names[i];
		}
		// We passed it.
		sims[i+1] = this_sim;
		names[i+1] = cursor.keyBytes();
	}
	
	/**
	 * Optimized _linear_search_ for the best N documents by cosine similarity.
	 * Be warned: This will be slow.
	 */
	public List<Passage> query(Question question) {
		// Convert the question to a vector.
		float[] query_vector = DenseVectors.mean(
				question.memo(Phrase.simpleTokens)
				.stream().map(DenseVectors::vectorFor)
				.filter(v -> v.isPresent())
				.map(v -> v.get())
				.collect(Collectors.toList()));
		
		// Now look for (almost) that vector!
		// This is a little ugly because we desperately avoid copying.
		byte[][] winners = new byte[LEN][];
		double[] sims = new double[LEN];
		try (Transaction tx = wiki_vectors_env.createReadTransaction();
				Database doc_vectors = wiki_vectors_env.openDatabase(tx, "wiki-vectors", 0);
				BufferCursor cursor = doc_vectors.bufferCursor(tx)) {
			cursor.first();
			while (cursor.next()) {
				double this_sim = sim(query_vector, cursor);
				bubble(sims, winners, this_sim, cursor);
			}
		}
		
		// Now get the passages for the top entries.
		List<Passage> passages = new ArrayList<>();
		for (byte[] id : winners) {
			String ids = string(id);
			passages.add(new Passage("meandv", "", "", ids));
			System.out.println("value is : "+ids);
		}
		
		/*try{
			Process p = Runtime.getRuntime().exec("python /home/sean/yeshvant/top100vectorSimilarDocs.py " + query );
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while((line = in.readLine())!= null)
			{
				String[] sim_id = line.split(" "); 
				passages.add(new Passage("meandv", "", "", sim_id[1]));
				System.out.println("value is : "+sim_id[1]);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}*/
		return fillFromSources(passages);
	}

	private static double sim(float[] left, BufferCursor right) {
		/*
		 *         A.T * B
		 * -----------------------
		 * sqrt(A.T*A) sqrt(B.T*B)
		 */
		assert left.length == N;
		// assert right.length == N; // You can't tell. Fingers crossed.
		double ab = 0.0, aa = 0.0, bb = 0.0;
		for (int i=0; i<left.length; i++) {
			ab += left [i] * right.valFloat(i*4);
			aa += left [i] * left [i];
			bb += right.valFloat(i*4) * right.valFloat(i*4);
		}
		if (aa == 0.0 || bb == 0.0) return 0;
		else return ab / (Math.sqrt(aa) * Math.sqrt(bb));
	}
}
