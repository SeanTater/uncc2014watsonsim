package edu.uncc.cs.watsonsim.scorers;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.uncc.cs.watsonsim.Environment;

public class EntropyTest {
	Entropy e;

	@Before
	public void setUp() throws Exception {
		Environment env = new Environment();
		e = new Entropy(env);
	}

	@Test
	public void testGetEntropy() {
		assertTrue(
				e.entropy(Arrays.asList("zucchini", "sepals"))
				> e.entropy(Arrays.asList("the", "of")));
		
		String w1 = "Subverting Randall’s editor’s admiral intentions, "
				+ "alternative enjoyment ensues composing complete "
				+ "paragraphs entirely shunning Randall’s thousand "
				+ "commonest dictionary terms. Bombastic prose "
				+ "frequently results.";
		List<String> ws1 = Arrays.asList(w1.split(" "));
		String w2 = "See spot run. Spot runs fast. Spot and Joey play in the "
				+ "park.";
		List<String> ws2 = Arrays.asList(w2.split(" "));
		assertTrue(e.entropy(ws1) > e.entropy(ws2));
	}

}
