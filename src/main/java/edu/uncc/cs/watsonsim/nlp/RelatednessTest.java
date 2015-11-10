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
	public void testImplies() {
		fail("Not yet implemented");
	}

}
