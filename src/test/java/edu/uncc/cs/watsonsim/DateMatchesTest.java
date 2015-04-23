package edu.uncc.cs.watsonsim;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uncc.cs.watsonsim.scorers.DateMatches;

public class DateMatchesTest {

	@Test
	public void test() {
		assertTrue(DateMatches.maybeYear("2005"));
		assertTrue(DateMatches.maybeYear("05"));
		assertFalse(DateMatches.maybeYear("-12"));
		assertFalse(DateMatches.maybeYear("Fall"));
		
		assertTrue(DateMatches.maybeMonth("March"));
		assertTrue(DateMatches.maybeMonth("Mar"));
		assertTrue(DateMatches.maybeMonth("03"));
		assertTrue(DateMatches.maybeMonth("3"));
		
		assertTrue(DateMatches.maybeDay("2"));
		assertTrue(DateMatches.maybeDay("12"));
		assertFalse(DateMatches.maybeDay("123"));
		
		assertTrue(DateMatches.maybeDate("04/05/1992"));
		assertTrue(DateMatches.maybeDate("04-05-1992"));
		assertTrue(DateMatches.maybeDate("04 05 1992"));
		assertTrue(DateMatches.maybeDate("05 1992"));
		assertTrue(DateMatches.maybeDate("05-1992"));
		assertTrue(DateMatches.maybeDate("05/1992"));
		assertTrue(DateMatches.maybeDate("May 1992"));
		assertTrue(DateMatches.maybeDate("04 May"));
		assertTrue(DateMatches.maybeDate("May 04"));
		assertTrue(DateMatches.maybeDate("May 4, 1992"));
		assertTrue(DateMatches.maybeDate("1992, 04 May"));
		assertFalse(DateMatches.maybeDate("99181919728"));
		assertFalse(DateMatches.maybeDate("1010 1010 0101 0001"));
		assertFalse(DateMatches.maybeDate("Mayday Mayday"));
		assertTrue(DateMatches.maybeDate("12 June 19283")); // still 12 June
	}

}
