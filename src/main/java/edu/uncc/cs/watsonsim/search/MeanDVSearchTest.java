package edu.uncc.cs.watsonsim.search;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;
import static org.fusesource.lmdbjni.Constants.*;

public class MeanDVSearchTest {

	MeanDVSearch mds;
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		mds = new MeanDVSearch(new Environment());
		List<Passage> frogstuff = mds.query("frog");
		assertTrue(frogstuff.size() > 0);
		assertTrue(frogstuff.get(0).title.contains("frog"));
	}
	
	@Test
	public void testBubble() {
		double[] sims = new double[5];
		byte[][] names = new byte[5][];
		byte[] name_e = bytes("e");
		byte[] name_f = bytes("f");
		byte[] name_g = bytes("g");
		
		sims[0]=0.8; sims[1]=0.5; sims[2]=0.0; sims[3]=-1;
		names[0]=bytes("a"); names[1]=bytes("b"); names[2]=bytes("c"); names[3]=bytes("d");
		
		MeanDVSearch.bubble(sims, names, 0.9, name_e, 4);
		assertEquals(0.9, sims[0], 0.01);
		assertEquals(0.8, sims[1], 0.01);
		assertEquals(0.5, sims[2], 0.01);
		assertEquals(0.0, sims[3], 0.01);
		assertEquals(name_e, names[0]);
		//----------------------------------------------------------------
		
		MeanDVSearch.bubble(sims, names, 0.1, name_f, 4);
		assertEquals(0.9, sims[0], 0.01);
		assertEquals(0.8, sims[1], 0.01);
		assertEquals(0.5, sims[2], 0.01);
		assertEquals(0.1, sims[3], 0.01);
		assertEquals(name_f, names[3]);
		//----------------------------------------------------------------
		
		MeanDVSearch.bubble(sims, names, 0.5, name_g, 4);
		assertEquals(0.9, sims[0], 0.01);
		assertEquals(0.8, sims[1], 0.01);
		assertEquals(0.5, sims[2], 0.01);
		assertEquals(0.5, sims[3], 0.01);
		assertEquals(name_g, names[3]);
		//----------------------------------------------------------------
	}

}
