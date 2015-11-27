package edu.uncc.cs.watsonsim.nlp;

import java.util.Iterator;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.codecs.bloom.MurmurHash2;

import com.carrotsearch.hppc.IntIntOpenHashMap;

/**
 * A memory-efficient String-int map: only stores hash->int relations,
 * and later when you iterate it guesses the hash->String relation using a
 * dictionary.
 * @author Sean
 */
public class ApproxStringIntMap implements Iterable<Pair<String, Integer>> {
	StringStack dict;
	IntIntOpenHashMap map = new IntIntOpenHashMap();
	
	private int hash(String x) {
		byte[] b = x.getBytes();
		return MurmurHash2.hash(b, 0, 0, b.length);
	}
	
	/** Create an approximate String-int map using a shared dictionary */
	public ApproxStringIntMap(StringStack dictionary) {
		dict = dictionary;
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean containsKey(String key) {
		return map.containsKey(hash(key));
	}

	public int get(String key) {
		return map.get(hash(key));
	}

	public int put(String key, int value) {
		return map.put(hash(key), value);
	}
	
	public int addTo(String key, int amount) {
		return map.addTo(hash(key), amount);
	}

	public int remove(String key) {
		return map.remove(hash(key));
	}

	public void clear() {
		map.clear();
	}

	/**
	 * Iterate the entries in this map - linear in complexity to the vocabulary
	 * size!
	 */
	public Iterator<Pair<String, Integer>> iterator() {
		return new StringIntMapIterator(this);
	}
	
	private class StringIntMapIterator implements Iterator<Pair<String, Integer>> {
		private final Iterator<String> dictiter;
		private Pair<String, Integer> next_item;
		private ApproxStringIntMap asim;
		StringIntMapIterator(ApproxStringIntMap asim) {
			this.dictiter = asim.dict.iterator();
			this.asim = asim;
		}
		
		@Override
		public boolean hasNext() {
			while (next_item == null && dictiter.hasNext()) {
				String key = dictiter.next();
				if (asim.containsKey(key))
					next_item = Pair.of(key, asim.get(key));
			}
			return next_item != null;
		}

		@Override
		public Pair<String, Integer> next() {
			Pair<String,Integer> item = next_item;
			next_item = null;
			return item;
		}
		
	}
}
