package uncc2014watsonsim;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class ResultSetTest {
	static ResultSet result;

	@Test
	public void testEqualsObject() {
		// Results are equal if their titles are similar.
		assertEquals(
			new ResultSet("duck duck goose", "text", "engine", 0, 0),
			new ResultSet("duck duck", "text", "engine", 0, 0));

		assertEquals(
			new ResultSet("Pete and repeat were sitting on a fence", "text", "engine", 0, 0),
			new ResultSet("Peteand repeat were sitting on a fence", "text", "engine", 0, 0));
	}

}
