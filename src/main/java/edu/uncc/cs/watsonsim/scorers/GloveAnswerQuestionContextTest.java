package edu.uncc.cs.watsonsim.scorers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;

public class GloveAnswerQuestionContextTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testScoreAnswer() {
		GloveAnswerQuestionContext scorer = new GloveAnswerQuestionContext();
		assertEquals(scorer.scoreAnswer(new Question("frog"), new Answer("toad")), 0.73, 0.01);
		assertEquals(scorer.scoreAnswer(new Question("frog"), new Answer("maple")), 0.23, 0.01);
		assertEquals(scorer.scoreAnswer(
				new Question("Who was Marilyn Monroe's second husband?"),
				new Answer("Joe Dimaggio")), 0.26, 0.01);
		assertEquals(scorer.scoreAnswer(
				new Question("Who was Marilyn Monroe's second husband?"),
				new Answer("husband")), 0.26, 0.01);
	}

}
