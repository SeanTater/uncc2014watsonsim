package edu.uncc.cs.watsonsim.nlp;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

public class StringStackTest {

	@Test
	public void testSize() {
		assertEquals(0, new StringStack().size());
		assertEquals(1, new StringStack("moo").size());
		assertEquals(2, new StringStack("foo", "bar").size());
	}

	@Test
	public void testIsEmpty() {
		assertTrue(new StringStack().isEmpty());
		assertFalse(new StringStack("moo").isEmpty());
	}

	@Test
	public void testContains() {
		assertFalse(new StringStack().contains("moo"));
		assertTrue(new StringStack("moo").contains("moo"));
		assertFalse(new StringStack("moo").contains("foobar"));
		assertTrue(new StringStack("foo", "moo").contains("foo"));
	}

	@Test
	public void testAdd() {
		StringStack ss = new StringStack();
		assertEquals(0, ss.size());
		ss.add("moo");
		assertEquals(1, ss.size());
		assertFalse(ss.contains("erk"));
		assertTrue(ss.contains("moo"));
		ss.add("moo");
		assertEquals(2, ss.size());
		ss.add("erk");
		assertEquals(3, ss.size());
		assertTrue(ss.contains("erk"));
		assertTrue(ss.contains("moo"));
	}

	@Test
	public void testClear() {
		StringStack ss = new StringStack("moo");
		ss.clear();
		assertEquals(0, ss.size());
		assertFalse(ss.contains("moo"));
	}

	@Test
	public void testGet() {
		StringStack ss = new StringStack("moo", "far");
		assertEquals(null, ss.get(-1));
		assertEquals("moo", ss.get(0));
		assertEquals("far", ss.get(1));
		assertEquals(null, ss.get(2));
		
	}

	@Test
	public void testIndexOf() {
		StringStack ss = new StringStack("moo", "far");
		assertEquals(0, ss.indexOf("moo"));
		assertEquals(1, ss.indexOf("far"));
		assertEquals(-1, ss.indexOf("erk"));
		assertEquals(-1, ss.indexOf(null));
	}

	@Test
	public void testIterator() {
		StringStack ss = new StringStack("moo", "far");
		Iterator<String> iters = ss.iterator();
		assertTrue(iters.hasNext());
		assertEquals("moo", iters.next());
		assertTrue(iters.hasNext());
		assertEquals("far", iters.next());
		assertFalse(iters.hasNext());
		
		// Check that it's repeatable
		iters = ss.iterator();
		assertTrue(iters.hasNext());
		assertEquals("moo", iters.next());
		assertTrue(iters.hasNext());
		assertEquals("far", iters.next());
		assertFalse(iters.hasNext());
	}

}
