package edu.uncc.cs.watsonsim.scorers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.StringUtils;

/**
 * @author Sean Gallagher
 *
 */

public class SkipBigram extends PassageScorer {
	
	public double scorePassage(Phrase q, Answer a, Passage p) {
		
		// Jane Austen
		Set<String> a_set = generateBigrams(StringUtils.tokenize(a.text));
		
		// Romantic novelist Jane Austen once wrote -the- book Emma.
		Set<String> p_set = generateBigrams(p.getTokens());
		
		a_set.retainAll(p_set);
		
		return a_set.size();
	}
	
	private Set<String> generateBigrams(List<String> terms) {
		Set<String> bigrams = new HashSet<>();
		for (int ti=0; ti<terms.size()-1; ti++) {
			// First the bigram
			bigrams.add(terms.get(ti) + terms.get(ti+1));
			if (ti < terms.size()-2) {
				// Maybe the skip bigram, if we are more than one word from end
				bigrams.add(terms.get(ti) + terms.get(ti+1));
			}
		}
		return bigrams;
	}

}
