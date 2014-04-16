/* Varsha Devadas */

package uncc2014watsonsim.research;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;

public class NGram extends PassageScorer {
	public double scorePassage(Question q, Answer a, Passage p) {
		// Jane Austen
		Set<String> a_set = (Set<String>) generateNgrams(3,a.candidate_text);
		
		// Romantic novelist Jane Austen once wrote -the- book Emma.
		Set<String> p_set = (Set<String>) generateNgrams(3,p.text);
		
		a_set.retainAll(p_set);
		
		return a_set.size();
			
	}
	public static List<String> generateNgrams(int n, String str) {
        List<String> ngrams = new ArrayList<String>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++)
            ngrams.add(concat(words, i, i+n));
        return ngrams;
	}
	
	public static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words[i]);
        return sb.toString();
    }
	
}


	

