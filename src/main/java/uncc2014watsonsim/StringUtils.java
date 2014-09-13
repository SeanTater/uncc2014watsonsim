package uncc2014watsonsim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
*@author Jagan Vujjini
*/
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	private static Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_47);
	
	/** Filter out stop words from a string */
	public static String filterRelevant(String text) {
		String mQuestion="";
		for (String token : tokenize(text))
			mQuestion += token + " ";
		return mQuestion.trim();
	}
	
	/** Filter out stop words from a string */
	public static List<String> tokenize(String text) {
		List<String> tokens = new ArrayList<>();
		
		try (TokenStream tokenStream = analyzer.tokenStream("text", text)) {
			//TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_46, new StringReader(text));
			//tokenStream = new org.apache.lucene.analysis.core.StopFilter(Version.LUCENE_46, tokenStream, EnglishAnalyzer.getDefaultStopSet());
			CharTermAttribute token = tokenStream.addAttribute(CharTermAttribute.class);
			
			// On the fence whether it is better to error here or not. Suggestions?
			tokenStream.reset();
		
			while (tokenStream.incrementToken()) {
				tokens.add(token.toString());
			}
		} catch (IOException e) {
			// If we can't trim it, so what?
			e.printStackTrace();
		}
		return tokens;
	}
	
    /** Returns true if every non-stopword from candidate is found in reference */
    public static boolean match_subset(String candidate, String reference){
            // Match these two sets in linear (or linearithmic) time
            HashSet<String> reference_terms = new HashSet<String>();
            reference_terms.addAll(StringUtils.tokenize(candidate));
            return reference_terms.containsAll(StringUtils.tokenize(reference));
    }
    
    
}