package edu.uncc.cs.watsonsim.scorers;

import java.util.HashSet;

import edu.stanford.nlp.trees.Tree;
import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;

/* @author Wlodek
 * @author Sean Gallagher
 * 
 * Create a score based on how many parse trees the question, candidate answer
 * and passage have in common.
 * 
 * This scorer can be very slow.
 */

public class CommonConstituents extends PassageScorer {
	/**
	 * Score the similarity of two sentences according to
	 * sum([ len(x) | x of X, y of Y, if x == y ])
	 * where X and Y are the sets of subtrees of the parses of s1 and s2.  
	 * @param x
	 * @param y
	 * @return
	 */
	public static double getCommonSubtreeCount(Phrase t1, Phrase t2) {
		
		HashSet<String> t1_subtrees = new HashSet<>();
		HashSet<String> t2_subtrees = new HashSet<>();
		for (Tree x : t1.getTrees()) t1_subtrees.add(x.toString());
		for (Tree y : t2.getTrees()) t2_subtrees.add(y.toString());
		t1_subtrees.retainAll(t2_subtrees);
		
		// x.getLeaves().size() may also be a good idea.
		// I don't have any intuition for which may be better.
		return t1_subtrees.size();
	}
		

	/** Generate a simple score based on scorePhrases.
	 * 
	 */
	public double scorePassage(Phrase q, Answer a, Passage p) {
		return getCommonSubtreeCount(p, new Phrase(a.text));
	}
}

