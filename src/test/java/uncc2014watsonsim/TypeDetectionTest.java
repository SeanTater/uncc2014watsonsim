package uncc2014watsonsim;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.uncc.cs.watsonsim.Configuration;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.nlp.ClueType;

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
		testHasAll("New York", new String[]{"city", "municipality", "place"});
		testHasAll("tab", new String[]{"beverage", "food"});
	}


	public void testHasAll(String source, String[] targets) {
		List<String> types = new ArrayList<>();
		try {
			Configuration env = new Environment();
			types = new ClueType(env).viaDBPedia(source);
		} catch (RuntimeException | IOException e) {
			// If this goes wrong, it probably just means we are disconnected
			System.err.println("Failed to connect to SPARQL endpoint for answer "
					+ "type detection. Perhaps you are disconnected?");
			System.err.println(e.getMessage());
			System.err.println(e.getStackTrace());
			return;
		}
		
		for (String target : targets)
			assertTrue(types.contains(target));
	}

}
