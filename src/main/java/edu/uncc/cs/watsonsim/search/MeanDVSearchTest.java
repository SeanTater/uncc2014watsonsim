package edu.uncc.cs.watsonsim.search;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;

public class MeanDVSearchTest {

	MeanDVSearch mds;
	@Before
	public void setUp() throws Exception {
		 mds = new MeanDVSearch(new Environment());
	}

	@Test
	public void test() {
		List<Passage> frogstuff = mds.query("frog");
		assertTrue(frogstuff.size() > 0);
		assertTrue(frogstuff.get(0).title.contains("frog"));
	}

}
