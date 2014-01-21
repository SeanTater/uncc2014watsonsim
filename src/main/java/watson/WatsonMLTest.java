package watson;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class WatsonMLTest {

	@Test
	public void testAggregate() {
		// Setup possible inputs
		Resultset yahoo = new Resultset("Yahoo");
		yahoo.add(new Result("doc1", "Alligators", "They're Aunt Annie's", 0.25));
		Resultset bing = new Resultset("Bing");
		bing.add(new Result("doc1", "Alligators", "They're Aunt Annie's", 0.75));
		
		Resultset output_set;
		WatsonML ml = new WatsonML();
		
		// Make an exact copy when there is 1 result
		output_set = ml.aggregate(yahoo);
		assertEquals(output_set.get(0), yahoo.get(0));
		assertEquals(output_set.get(0).getScore(), yahoo.get(0).getScore(), 0.001);
		
		// Average two results
		output_set = ml.aggregate(yahoo, bing);
		assertEquals(output_set.get(0), yahoo.get(0));
		assertEquals(0.5, output_set.get(0).getScore(), 0.001);
		
		// Sort unique results
		yahoo.add(new Result("doc1", "Eels", "Electric", 0.88));
		bing.add(new Result("doc1", "Elk", "Moose", 0.99));
		output_set = ml.aggregate(yahoo, bing);
		assertEquals("Alligators", output_set.get(0).title);
		assertEquals("Eels", output_set.get(1).title);
		assertEquals("Elk", output_set.get(2).title);
	}

}
