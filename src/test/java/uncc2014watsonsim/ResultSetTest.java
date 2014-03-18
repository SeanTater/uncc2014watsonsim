package uncc2014watsonsim;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class ResultSetTest {
	static Answer result;

	@Test
	public void testEqualsObject() {
		// Results are equal if their titles are similar.
		assertEquals(
			new Answer("engine", "duck duck goose", "text", "reference", 0, 0),
			new Answer("engine", "duck duck", "text", "reference", 0, 0));

		assertEquals(
			new Answer("engine", "Pete and repeat were sitting on a fence", "text", "reference", 0, 0),
			new Answer("engine", "Peteand repeat were sitting on a fence", "text", "reference", 0, 0));
	}

}
