package edu.uncc.cs.watsonsim;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uncc.cs.watsonsim.StringUtils;

public class StringUtilsTest {

	@Test
	public void test_match_subset() {
		assertTrue(StringUtils.matchSubset("cat toy", "cat toy"));
		
		assertTrue(StringUtils.matchSubset("thundering applause", "resounding, thundering applause"));
		
		assertTrue(StringUtils.matchSubset("What is for dinner, mother?", "What, is mother for dinner?"));
	}
	
	@Test
	public void test_filter_relevant() {
		assertEquals(StringUtils.canonicalize("cat toy"), "cat toy");
		assertEquals(StringUtils.canonicalize("resounding, thundering applause"), "resounding thundering applause");
		assertEquals(StringUtils.canonicalize("What is for dinner, mother?"), "what dinner mother");
		assertEquals(StringUtils.canonicalize("I am a walaby"), "i am walaby"); // This is more documentation than test
		assertEquals(StringUtils.canonicalize("I\n\t am   a walaby~!@#$%^&*()_+`-={}[]:\";\'<>?,./"), "i am walaby");
	}

}
