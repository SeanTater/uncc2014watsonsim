package uncc2014watsonsim;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
*@author Jagan Vujjini
*/
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
	/** Filter out stop words from a string */
	public static String filterRelevant(String text) {
		String mQuestion="";
		
		text = text.replaceAll("[^0-9a-zA-Z ]+", "").toLowerCase().trim();
		TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_46, new StringReader(text));
		tokenStream = new org.apache.lucene.analysis.core.StopFilter(Version.LUCENE_46, tokenStream, EnglishAnalyzer.getDefaultStopSet());
		CharTermAttribute token = tokenStream.addAttribute(CharTermAttribute.class);
		
		try {
			// On the fence whether it is better to error here or not. Suggestions?
			tokenStream.reset();
		
			while (tokenStream.incrementToken()) {
				mQuestion += token.toString() + " ";
			}
			
			tokenStream.close();

		} catch (IOException e) {
			// If we can't trim it, so what?
			e.printStackTrace();
			mQuestion = text;
		}
		
		return mQuestion.trim();
	}
	
    /** Returns true if every non-stopword from candidate is found in reference */
    public static boolean match_subset(String candidate, String reference){
        
            // Removing stop words and non-alphanumeric characters from the strings
            candidate = StringUtils.filterRelevant(candidate);
            reference = StringUtils.filterRelevant(reference);
            
            // Match these two sets in linear (or linearithmic) time
            HashSet<String> reference_terms = new HashSet<String>();
            reference_terms.addAll(Arrays.asList(reference.toLowerCase().split("\\W+")));
            return reference_terms.containsAll(Arrays.asList(candidate.toLowerCase().split("\\W+")));
    }
    
    /** Guess if one answer matches the other based on levenshtein distance */
    public boolean match_levenshtein(String candidate, String reference) {
        int threshold = Math.min(candidate.length(), reference.length()) / 2;
        
        return StringUtils.getLevenshteinDistance(candidate.toLowerCase(), reference.toLowerCase()) < threshold;
    }

}