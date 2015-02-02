package uncc2014watsonsim;

import static org.junit.Assert.*;

import org.junit.Test;

public class LATDetectionTest {

	@Test
	public void testSimpleFetchLAT() {
		DetectLAT dlat = new DetectLAT();
		assertEquals(
			dlat.simpleFetchLAT("This man was the first to walk on the moon."),
			"man"
		);
	}

}
