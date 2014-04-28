/* Varsha Devadas */

package uncc2014watsonsim.research;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Pipeline;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;

public class NGram extends PassageScorer {
	public double scorePassage(Question q, Answer a, Passage p) {
		// Jane Austen
		List<String> a_set = generateNgrams(3, StringUtils.tokenize(a.candidate_text));
		
		// Romantic novelist Jane Austen once wrote -the- book Emma.
		List<String> p_set = generateNgrams(3, p.tokens());
		
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


	

