package uncc2014watsonsim;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import edu.stanford.nlp.trees.Tree;
import uncc2014watsonsim.scorers.CoreNLPSentenceSimilarity;

public class CoreNLPSentenceSimilarityTest {

	@Test
	public void testParseToTree() {
		CoreNLPSentenceSimilarity scorer = new CoreNLPSentenceSimilarity();
		
		// Empty case
		assertEquals(new ArrayList<>(), scorer.parseToTrees(""));
		// Simple case
		assertEquals(Tree.valueOf("(ROOT (NP (NN Example)))"), scorer.parseToTrees("Example").get(0));
		// Challenging case
		// fails: "Buffalo buffalo Buffalo buffalo buffalo buffalo Buffalo buffalo."
		// succeeds, or at least it looks generally right to me:
		assertEquals(Tree.valueOf("(ROOT (S (NP (NNP Niel) (NNP Armstrong)) "
				+ "(VP (VBD was) (NP (DT the) (JJ first) (NN man)"
				+ "(S (VP (TO to) (VP (VB walk) "
				+ "(PP (IN on) (NP (DT the) (NN moon)))))))) (. .)))"),
				scorer.parseToTrees("Niel Armstrong was the first man to walk on the moon.").get(0));
		
		assertEquals(
				Tree.valueOf("(ROOT (S (NP (PRP I)) (VP (VBP am) (ADJP (JJ tall))) (. .)))"),
				scorer.parseToTrees("I am tall. You are short.").get(0));
		assertEquals(
				Tree.valueOf("(ROOT (S (NP (PRP You)) (VP (VBP are) (ADJP (JJ short))) (. .)))"),
				scorer.parseToTrees("I am tall. You are short.").get(1));
		
	}

	@Test
	public void testScorePhrases() {
		CoreNLPSentenceSimilarity scorer = new CoreNLPSentenceSimilarity();
		assertEquals(
				6.0,
				scorer.scorePhrases("My goat knows the bowling score.", "Michael rowed the boat ashore."),
				0.01
		);
		assertEquals(
				28.0,
				scorer.scorePhrases("A tisket, a tasket, a green and yellow basket.", "A tisket, a tasket, what color is my basket?"),
				0.01
		);
	}
}
