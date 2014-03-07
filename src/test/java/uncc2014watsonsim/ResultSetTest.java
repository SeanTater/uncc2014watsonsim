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
			new Answer("duck duck goose", "text", "engine", 0, 0),
			new Answer("duck duck", "text", "engine", 0, 0));

		assertEquals(
			new Answer("Pete and repeat were sitting on a fence", "text", "engine", 0, 0),
			new Answer("Peteand repeat were sitting on a fence", "text", "engine", 0, 0));
	}

}
