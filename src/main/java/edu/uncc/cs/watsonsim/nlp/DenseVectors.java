package edu.uncc.cs.watsonsim.nlp;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import edu.uncc.cs.watsonsim.KV;

public class DenseVectors {
	public static final int N = 300;
	private static final KV kv = new KV();
	
	/**
	 * Possibly get a vector context for a word (otherwise an empty Optional)
	 * @param word		The word in question
	 * @return			A Optional<float[]> for that word, or Optional.empty()
	 */
	public static Optional<float[]> vectorFor(String word) {
		if (word == null || word.isEmpty()) {
			return Optional.empty();
		} else {
			return kv.get("big-glove", word).map(KV::asVector);
		}
	}
	
	/**
	 * Find the cosine similarity of two vectors, which may or may not exist.
	 * This is pessimistic, saying that if we have never seen a word before, it
	 * is probably unrelated to everyone
	 * @return
	 */
	public static double sim(float[] left, float[] right) {
		/*
		 *         A.T * B
		 * -----------------------
		 * sqrt(A.T*A) sqrt(B.T*B)
		 */
		assert left.length == N;
		assert right.length == N;
		double ab = 0.0, aa = 0.0, bb = 0.0;
		for (int i=0; i<Math.min(left.length, right.length); i++) {
			ab += left [i] * right[i];
			aa += left [i] * left [i];
			bb += right[i] * right[i];
		}
		if (aa == 0.0 || bb == 0.0) return 0;
		else return ab / (Math.sqrt(aa) * Math.sqrt(bb));
	}
	
	/**
	 * Tiny wrapper around sim(float[], float[]) for optional-word situations
	 */
	public static double sim(Optional<float[]> left, Optional<float[]> right) {
		if (left.isPresent() && right.isPresent())
			return sim(left.get(), right.get());
		else
			return 0.0;
	}
	
	/**
	 * Average some vectors, as a multi-word model. This is not very meaningful
	 * and may do strange things for the semantics. (e.g. we plan to do better)
	 */
	public static float[] mean(List<float[]> vecs) {
		float[] mean = new float[N];
		int count = 0;
		for (float[] vec: vecs) {
			for (int i=0; i<N; i++) mean[i] += vec[i];
			count++;
		}
		if (count>0) for (int i=0; i<N; i++) mean[i] /= count;
		return mean;
	}
	
	/**
	 * Multiply many vectors, as a multi-word model. It can be better than mean
	 * but it's still not a syntactic parse.
	 */
	public static float[] logproduct(List<float[]> vecs) {
		float[] logprod = new float[N];
		int count = 0;
		for (float[] vec: vecs) {
			for (int i=0; i<N; i++) logprod[i] += Math.log(Math.abs(vec[i]));
			count++;
		}
		for (int i=0; i<N; i++) logprod[i] /= count;
		return logprod;
	}
}
