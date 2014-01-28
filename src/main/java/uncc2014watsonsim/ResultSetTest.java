package uncc2014watsonsim;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class ResultSetTest {
	static ResultSet result;
	
	@BeforeClass
	public static void setUp() {
		 result = new ResultSet("title", 0.5, true, 1);
	}

	@Test
	public void testEqualsObject() {
		// Results are equal if their titles are equal.
		assertEquals(result, new ResultSet(result));
		assertEquals(result, new ResultSet("title", 0.5, true, 1));
	}

	@Test
	public void testSetScore() {
		assertEquals(0.5, result.getScore(), 0.001);
		// Unscaled
		result.setScore(0.8);
		assertEquals(0.8, result.getScore(), 0.001);
		// Scaled
		// Normally these would be overridden in inheritance
		result.best_score = 100;
		result.worst_score = 0;
		result.setScore(100);
		assertEquals(0, result.getScore(), 0.001);
		result.setScore(0);
		assertEquals(1, result.getScore(), 0.001);
		result.setScore(50);
		assertEquals(0.5, result.getScore(), 0.001);
	}

}
