/**
 * @author Varsha Devadas
 */

package edu.uncc.cs.watsonsim.scorers;

import java.util.ArrayList;
import java.util.List;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.StringUtils;

public class NGram extends PassageScorer {
	public double scorePassage(Phrase q, Answer a, Passage p) {
		// Jane Austen
		List<String> a_set = generateNgrams(3, StringUtils.tokenize(a.text));
		
		// Romantic novelist Jane Austen once wrote -the- book Emma.
		List<String> p_set = generateNgrams(3, p.getTokens());
		
		a_set.retainAll(p_set);
		return a_set.size();
			
	}
	public static List<String> generateNgrams(int n, List<String> words) {
        List<String> ngrams = new ArrayList<String>();
        for (int i = 0; i < words.size() - n + 1; i++)
            ngrams.add(concat(words, i, i+n));
        return ngrams;
	}
	
	public static String concat(List<String> words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words.get(i));
        return sb.toString();
    }
	
	/*public static void main(String[] args) {
		
    		Question question = Pipeline.ask("Who wrote Emma?");
    		Answer r = question.get(0);
    		NGram ngram = new NGram();
    		
	        double result = ngram.scorePassage(question, r, r.passages.get(0));
	        
	        System.out.println(result);
    	}*/
		
	
	
}


	

