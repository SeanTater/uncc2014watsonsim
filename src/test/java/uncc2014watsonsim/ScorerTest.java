package uncc2014watsonsim;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uncc2014watsonsim.scorers.PercentWordsInCommon;
import uncc2014watsonsim.scorers.Scorer;

public class ScorerTest {

	Question question;
	Answer a_is_question;
	Answer a_like_question;
	Answer a_unlike_question;
	Answer a_unlike_all;
	Passage p_is_question;
	Passage p_like_question;
	Passage p_unlike_question;
	
	@Before
	public void setUp() throws Exception {
		question = new Question("This is the question.");
		a_is_question = new Answer(p_is_question);
		a_like_question = new Answer(p_like_question);
		a_unlike_question = new Answer(p_unlike_question);
		a_unlike_all = new Answer(new Passage("engine", "Mars", "39th planet from the sun, mod 7.", "http://nana.gov"));
		p_is_question = new Passage("engine", "Question", "This is the question.", "http://question");
		p_like_question = new Passage("engine", "Questionishness", "This is kinda like the question.", "http://questionishness");
		p_unlike_question = new Passage("engine", "Cows", "Cows grow wings and fly on April 11.", "http://example.com");
	}

	@Test
	public void testScorePassage() {
		PassageScorer s = new PercentWordsInCommon();
		
		fail("Not yet implemented"); // TODO
	}

}
