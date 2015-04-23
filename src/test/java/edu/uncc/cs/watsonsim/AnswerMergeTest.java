package edu.uncc.cs.watsonsim;

import static org.junit.Assert.*;

import org.junit.Test;

public class AnswerMergeTest {

	@Test
	public void testMatches() {
		// Results are equal if their titles are similar. This uses match_subset.
		/*assertTrue(
			new Answer("engine", "duck duck", "text", "reference", 0, 0).matches(
			new Answer("engine", "duck duck goose", "text", "reference", 0, 0)));
		
		assertFalse(
			new Answer("engine", "duck duck goose", "text", "reference", 0, 0).matches(
			new Answer("engine", "duck duck", "text", "reference", 0, 0)));

		assertTrue(
			new Answer("engine", "sitting on a fence", "text", "reference", 0, 0).matches(
			new Answer("engine", "Pete and repeat were sitting on a fence", "text", "reference", 0, 0)));
			*/
	}

}
