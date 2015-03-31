package edu.uncc.cs.watsonsim.scorers;

import java.util.HashSet;

import edu.stanford.nlp.trees.Tree;
import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.Question;

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
	public double scorePhrases(Phrase t1, Phrase t2) {
		
		HashSet<Tree> t1_subtrees = new HashSet<>();
		HashSet<Tree> t2_subtrees = new HashSet<>();
		for (Tree x : t1.trees) t1_subtrees.addAll(x);
		for (Tree y : t2.trees) t2_subtrees.addAll(y);
		t1_subtrees.retainAll(t2_subtrees);
		
		double score = 0.0;
		// x.getLeaves().size() may also be a good idea.
		// I don't have any intuition for which may be better.
		for (Tree x : t1_subtrees) score += x.size();
		return score;
	}
		

	/** Generate a simple score based on scorePhrases.
	 * 
	 */
	public double scorePassage(Question q, Answer a, Passage p) {
		return scorePhrases(p, new Phrase(a.text));
	}
}

