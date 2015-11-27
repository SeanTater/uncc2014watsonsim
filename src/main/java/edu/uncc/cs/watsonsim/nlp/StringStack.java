package edu.uncc.cs.watsonsim.nlp;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Iterator;

import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.IntArrayList;

/** An append-only, compact string stack. */
public class StringStack implements Iterable<String> {
	/** start_byte[i] = x --> word i starts at block[x], ends at block[start_byte[i+1]]
	 * The last element is where the free space begins. */
	IntArrayList start_byte = IntArrayList.from(0);
	ByteArrayList block = new ByteArrayList();
	
	/** Create a string stack from some existing strings */
	public StringStack(String... xs) {
		for (String x: xs)
			add(x);
	}
	
	/** Create a string stack from some existing strings */
	public StringStack(Iterable<String> xs) {
		for (String x: xs)
			add(x);
	}

	/** How many strings are inside? */
	public int size() {
		return start_byte.size() - 1;
	}

	/** Does it have at least one string? */
	public boolean isEmpty() {
		return size() == 0;
	}

	/** Does this contain string x? (O(n) - and expensive)*/
	public boolean contains(String o) {
		for (String x: this) {
			if (x.equals(o)) return true;
		}
		return false;
	}

	/** Add a string */
	public boolean add(String e) {
		block.add(e.getBytes(UTF_8));
		start_byte.add(block.size());
		return true;
		
	}

	/** Remove all contents */
	public void clear() {
		start_byte.clear();
		start_byte.add(0);
		block.clear();
	}

	/** Get a string by index */
	public String get(int index) {
		if (0 <= index && index + 1 < start_byte.size()) {
			int offset = start_byte.get(index);
			int length = start_byte.get(index+1) - offset;
			return new String(block.buffer, offset, length);
		} else {
			return null;
		}
	}

	/** Find string x (O(n) - and expensive) */
	public int indexOf(String o) {
		int i = 0;
		for (String x: this) {
			if (x.equals(o)) return i;
			else i++;
		}
		return -1;
	}

	/** Iterate a StringList */
	public Iterator<String> iterator() {
		return new StringListIterator(this);
	}
	
	private class StringListIterator implements Iterator<String> {
		private int index = 0;
		private final StringStack sl;
		
		public StringListIterator(StringStack sl) {
			this.sl = sl;
		}
		
		@Override
		public boolean hasNext() {
			return index < sl.size();
		}

		@Override
		public String next() {
			return sl.get(index++);
		}
		
	}

}
