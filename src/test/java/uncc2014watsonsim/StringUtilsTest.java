package uncc2014watsonsim;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void test_match_subset() {
		assertTrue(StringUtils.match_subset("cat toy", "cat toy"));
		
		assertTrue(StringUtils.match_subset("thundering applause", "resounding, thundering applause"));
		
		assertTrue(StringUtils.match_subset("What is for dinner, mother?", "What, is mother for dinner?"));
	}
	
	@Test
	public void test_filter_relevant() {
		assertEquals(StringUtils.filterRelevant("cat toy"), "cat toy");
		assertEquals(StringUtils.filterRelevant("resounding, thundering applause"), "resounding thundering applause");
		assertEquals(StringUtils.filterRelevant("What is for dinner, mother?"), "what dinner mother");
		assertEquals(StringUtils.filterRelevant("I am a walaby"), "i am walaby"); // This is more documentation than test
		assertEquals(StringUtils.filterRelevant("I\n\t am   a walaby~!@#$%^&*()_+`-={}[]:\";\'<>?,./"), "i am walaby");
	}

}
