package edu.uncc.cs.watsonsim;

import static org.junit.Assert.*;

import org.junit.Test;

import static edu.stanford.nlp.util.Triple.makeTriple;
import edu.uncc.cs.watsonsim.index.Edges;
import static java.util.Arrays.asList;

public class ReindexEdgesTest {

	@Test
	public void testSimpleExample() {
		Phrase p = new Phrase("This is an example.");

		assertEquals(asList(
				makeTriple("example","nsubj","This"),
				makeTriple("example","cop","is"),
				makeTriple("example","det","an")),
			Edges.generateEdges(p));
	}
	
	@Test
	public void testExtraLinks() {
		Phrase p = new Phrase("Donald Duck is a cool cartoon character. "
				+ "He sounds really funny.");
		System.out.println(Edges.generateEdges(p));
		
		assertTrue(Edges.generateEdges(p).containsAll(asList(
				makeTriple("Donald Duck","_isa","cartoon character"),
				makeTriple("Donald Duck","_gender","MALE"),
				makeTriple("Donald Duck","_animate","ANIMATE"),
				makeTriple("Donald Duck","_number","SINGULAR"),
				makeTriple("sound","nsubj","Donald Duck")
				)));
	}

}
