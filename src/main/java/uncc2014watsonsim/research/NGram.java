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

public class NGram extends Scorer {
	public double scorePassage(Question q, Answer a, Passage p) {
		// Jane Austen
				ArrayList<String> a_set = generateNgrams(3,StringUtils.filterRelevant(a.candidate_text));
				
				// Romantic novelist Jane Austen once wrote -the- book Emma.
				ArrayList<String> p_set = generateNgrams(3,StringUtils.filterRelevant(p.text));
				
				
				
				a_set.retainAll(p_set);
				
				return a_set.size();
			
	}
	public static ArrayList<String> generateNgrams(int n, String str) {
        ArrayList<String> ngrams = new ArrayList<String>();
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
	
	/*public static void main(String[] args) {
		
    		Question question = Pipeline.ask("Who wrote Emma?");
    		Answer r = question.get(0);
    		NGram ngram = new NGram();
    		
	        double result = ngram.scorePassage(question, r, r.passages.get(0));
	        
	        System.out.println(result);
    	}*/
		
	
	
}


	

