package uncc2014watsonsim;

import static org.junit.Assert.*;

import org.junit.Test;

import uncc2014watsonsim.scorers.CoreNLPSentenceSimilarity;

public class CoreNLPSentenceSimilarityTest {

	@Test
	public void testParseToTree() {
		CoreNLPSentenceSimilarity scorer = new CoreNLPSentenceSimilarity();
		assertEquals(scorer.parseToTree(""), null);
		assertEquals(scorer.parseToTree("Example"), null);
	}

}
