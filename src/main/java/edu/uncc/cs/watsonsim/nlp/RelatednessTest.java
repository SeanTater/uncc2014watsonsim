package edu.uncc.cs.watsonsim.nlp;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.uncc.cs.watsonsim.Environment;

public class RelatednessTest {
	private Relatedness rel;
	@Before
	public void setUp() throws Exception {
		rel = new Relatedness(new Environment());
	}

	@Test
	public void testViaWikiLinks() {
		fail("Not yet implemented");
	}

	@Test
	public void testMatchViaSearch() {
		fail("Not yet implemented");
	}

	@Test
	public void testMatchViaLevenshtein() {
		fail("Not yet implemented");
	}

	@Test
	public void testViaDenseVectors() {
		assertEquals(rel.viaDenseVectors("diabetes", "retinopathy"), 0.54, 0.01);
		assertEquals(rel.viaDenseVectors("diabetes", "diabetic"), 0.78, 0.01);
		assertEquals(rel.viaDenseVectors("(*&(*&^(*&^", "diabetic"), 0.00, 0.01);
		assertEquals(rel.viaDenseVectors("diabetes", ""), 0.00, 0.01);
		assertEquals(rel.viaDenseVectors("diabetes", null), 0.00, 0.01);
	}

	@Test
	public void testImplies() {
		fail("Not yet implemented");
	}

}
