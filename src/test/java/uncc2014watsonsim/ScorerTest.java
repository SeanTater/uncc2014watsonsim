package uncc2014watsonsim;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

public class ScorerTest {

	// Setup possible inputs
	ResultSet yahoo1;
	ResultSet yahoo2;
	ResultSet bing1;
	ResultSet bing2;
	AverageScorer ml;
	
	@Before
	public void setUp() {
		// Setup possible inputs
		yahoo1 = new ResultSet("Alligators", "text", "yahoo", 1, 0.75, true);
		yahoo2 = new ResultSet("Eels", "text", "yahoo", 2, 0.38, false);
		bing1 = new ResultSet("Alligators", "text", "bing", 1, 0.25, true);
		bing2 = new ResultSet("Elk", "text", "bing", 2, 0.19, false);
		ml = new AverageScorer();
	}

	@Test
	public void testOne() {
		// Make an exact copy when there is 1 result
		Question q = new Question("Fake Question?");
		q.add(yahoo1);
		ml.test(q);
		assertEquals(q.get(0), yahoo1);
		assertEquals(q.get(0).first("combined").score, AverageScorer.logistic(0.75), 0.001);
	}
	
	@Test
	public void testTwo() {
		// Average two results
		Question q = new Question("Fake Question?");
		q.add(yahoo1);
		q.add(bing1);
		ml.test(q);
		assertEquals(q.get(0), yahoo1);
		assertEquals(q.get(0).first("combined").score, AverageScorer.logistic(0.5), 0.001);
	}
		
	@Test
	public void testSort() {
		// Sort unique results
		Question q = new Question("Fake Question?");
		q.add(yahoo1);
		q.add(yahoo2);
		q.add(bing1);
		q.add(bing2);
		ml.test(q);
		assertEquals("Alligators", q.get(0).getTitle());
		assertEquals("Eels", q.get(1).getTitle());
		assertEquals("Elk", q.get(2).getTitle());
	}

}
