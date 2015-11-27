package edu.uncc.cs.watsonsim.nlp;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

public class ApproxStringIntMapTest {
	ApproxStringIntMap asim;
	@Before
	public void setUp() {
		asim = new ApproxStringIntMap(new StringStack("moo", "far"));
	}

	@Test
	public void testSize() {
		assertEquals(0, asim.size());
		asim.put("moo", 1);
		assertEquals(1, asim.size());
	}

	@Test
	public void testIsEmpty() {
		assertTrue(asim.isEmpty());
		asim.put("moo", 1);
		assertFalse(asim.isEmpty());
	}

	@Test
	public void testContainsKey() {
		assertFalse(asim.containsKey("moo"));
		asim.put("moo", 1);
		assertTrue(asim.containsKey("moo"));
		assertFalse(asim.containsKey("far"));
		asim.put("erk", 7);
		assertTrue(asim.containsKey("moo"));
		assertTrue(asim.containsKey("erk"));
		assertFalse(asim.containsKey("far"));
	}

	@Test
	public void testGetPut() {
		assertEquals(0, asim.get("moo")); // ! Keep this in mind!
		asim.put("far", 1);
		assertEquals(0, asim.get("moo"));
		assertEquals(1, asim.get("far"));
		asim.put("erk",  2);
		assertEquals(0, asim.get("moo"));
		assertEquals(2, asim.get("erk"));
	}
	
	@Test
	public void testAddTo() {
		assertEquals(0, asim.get("moo"));
		asim.addTo("moo", 4);
		assertEquals(4, asim.get("moo"));
		asim.addTo("moo", 4);
		assertEquals(8, asim.get("moo"));
	}

	@Test
	public void testRemove() {
		asim.put("moo", 1);
		asim.put("far", 2);
		assertTrue(asim.containsKey("far"));
		asim.remove("far");
		assertFalse(asim.containsKey("far"));
	}

	@Test
	public void testClear() {
		asim.put("moo", 1);
		asim.put("far", 2);
		assertTrue(asim.containsKey("far"));
		asim.clear();
		assertFalse(asim.containsKey("far"));
	}

	@Test
	public void testIterator() {
		asim.put("moo", 1);
		asim.put("far", 2);
		Iterator<Pair<String, Integer>> pairs = asim.iterator();
		assertTrue(pairs.hasNext());
		assertEquals(Pair.of("moo", 1), pairs.next());
		assertTrue(pairs.hasNext());
		assertEquals(Pair.of("far", 2), pairs.next());
		assertFalse(pairs.hasNext());
	}

}
