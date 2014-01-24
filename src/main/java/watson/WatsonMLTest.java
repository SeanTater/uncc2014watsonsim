package watson;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class WatsonMLTest {

	@Test
	public void testPush() {
		// Setup possible inputs
		Resultset yahoo = new Resultset("Yahoo");
		yahoo.add(new Result("Alligators", "title", "They're Aunt Annie's", 0.25));
		Resultset bing = new Resultset("Bing");
		bing.add(new Result("Alligators", "title", "They're Aunt Annie's", 0.75));
		
		Resultset output_set;
		WatsonML ml = new WatsonML();
		
		// Make an exact copy when there is 1 result
		output_set = ml.push(yahoo);
		assertEquals(output_set.get(0), yahoo.get(0));
		assertEquals(output_set.get(0).getScore(), yahoo.get(0).getScore(), 0.001);
		
		// Average two results
		output_set = ml.push(yahoo, bing);
		assertEquals(output_set.get(0), yahoo.get(0));
		assertEquals(0.5, output_set.get(0).getScore(), 0.001);
		
		// Sort unique results
		yahoo.add(new Result("Eels", "title", "Electric", 0.88));
		bing.add(new Result("Elk", "title", "Moose", 0.99));
		output_set = ml.push(yahoo, bing);
		assertEquals("Alligators", output_set.get(0).docid);
		assertEquals("Eels", output_set.get(1).docid);
		assertEquals("Elk", output_set.get(2).docid);
	}

}
