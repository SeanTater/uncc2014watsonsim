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
		
		// Make an exact copy when there is 1 result
		Resultset output_set;
		
		WatsonML ml = new WatsonML();
		output_set = ml.aggregate(yahoo);
		assertEquals(output_set.get(0), yahoo.get(0));
	}

}
