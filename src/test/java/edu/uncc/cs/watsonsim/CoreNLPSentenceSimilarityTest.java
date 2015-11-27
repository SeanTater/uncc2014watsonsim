package edu.uncc.cs.watsonsim;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import edu.stanford.nlp.trees.Tree;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.nlp.Trees;
import edu.uncc.cs.watsonsim.scorers.CommonConstituents;

public class CoreNLPSentenceSimilarityTest {

	@Test
	public void testParseToTree() {
		
		// Empty case
		assertEquals(new ArrayList<>(), Trees.parse(""));
		// Simple case
		assertEquals(Tree.valueOf("(ROOT (NP (NN Example)))"), Trees.parse("Example").get(0));
		// Challenging case
		// fails: "Buffalo buffalo Buffalo buffalo buffalo buffalo Buffalo buffalo."
		// succeeds, or at least it looks generally right to me:
		assertEquals(Tree.valueOf("(ROOT (S (NP (NNP Niel) (NNP Armstrong)) "
				+ "(VP (VBD was) (NP (DT the) (JJ first) (NN man)"
				+ "(S (VP (TO to) (VP (VB walk) "
				+ "(PP (IN on) (NP (DT the) (NN moon)))))))) (. .)))"),
				Trees.parse("Niel Armstrong was the first man to walk on the moon.").get(0));
		
		assertEquals(
				Tree.valueOf("(ROOT (S (NP (PRP I)) (VP (VBP am) (ADJP (JJ tall))) (. .)))"),
				Trees.parse("I am tall. You are short.").get(0));
		assertEquals(
				Tree.valueOf("(ROOT (S (NP (PRP You)) (VP (VBP are) (ADJP (JJ short))) (. .)))"),
				Trees.parse("I am tall. You are short.").get(1));
		
	}

	@Test
	public void testScorePhrases() {
		CommonConstituents scorer = new CommonConstituents();
		
		
		// These are in large part to make sure that it does not accidentally change.
		/*assertEquals(
				1.0,
				scorer.getCommonSubtreeCount(
					new Phrase("this"),
					new Phrase("this")),
				0.01
		);*/
		assertEquals(
				6.0,
				scorer.getCommonSubtreeCount(
					new Phrase("My goat knows the bowling score."),
					new Phrase("Michael rowed the boat ashore.")),
				0.01
		);
		assertEquals(
				12.0,
				scorer.getCommonSubtreeCount(
					new Phrase("A tisket. A tasket. A green and yellow basket."),
					new Phrase("A tisket, a tasket, what color is my basket?")),
				0.01
		);
	}
}
