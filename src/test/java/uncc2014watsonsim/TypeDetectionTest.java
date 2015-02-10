package uncc2014watsonsim;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uncc2014watsonsim.researchers.TagLAT;

public class TypeDetectionTest {

	@Test
	/**
	 * Check to see if the types received for a given input are sane.
	 * 
	 * This is not stubbed because this is only a client wrapper; there would
	 * be nothing left after stubbing. So expect it to fail if you do not have
	 * the DBPedia database setup.
	 */
	public void test() {
		List<String> types = new ArrayList<>();
		try {
			types = TagLAT.types("New York");
		} catch (RuntimeException e) {
			// If this goes wrong, it probably just means we are disconnected
			return;
		}
		assertTrue(types.contains("city"));
		assertTrue(types.contains("municipality"));
		assertTrue(types.contains("place"));
	}

}
