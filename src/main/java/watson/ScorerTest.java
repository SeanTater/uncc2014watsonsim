package watson;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class ScorerTest {

	@Test
	public void testPush() {
		// Setup possible inputs
		AnswerList yahoo = new AnswerList("Yahoo");
		yahoo.add(new ResultSet("Alligators", 0.5, true, 1));
		AnswerList bing = new AnswerList("Bing");
		bing.add(new ResultSet("Alligators", 0.5, true, 1));
		
		AnswerList output_set;
		Scorer ml = new Scorer();
		
		// Make an exact copy when there is 1 result
		output_set = ml.test(yahoo);
		assertEquals(output_set.get(0), yahoo.get(0));
		assertEquals(output_set.get(0).getScore(), yahoo.get(0).getScore(), 0.001);
		
		// Average two results
		output_set = ml.test(yahoo, bing);
		assertEquals(output_set.get(0), yahoo.get(0));
		assertEquals(0.5, output_set.get(0).getScore(), 0.001);
		
		// Sort unique results
		yahoo.add(new ResultSet("Eels", 0.88, false, 2));
		bing.add(new ResultSet("Elk", 0.99, false, 2));
		output_set = ml.test(yahoo, bing);
		assertEquals("Alligators", output_set.get(0).getTitle());
		assertEquals("Eels", output_set.get(1).getTitle());
		assertEquals("Elk", output_set.get(2).getTitle());
	}

}
