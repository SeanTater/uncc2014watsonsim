package uncc2014watsonsim;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	private static Database db = new Database(); // Used for semantic distribution
	public static final int CONTEXT_LENGTH = 1000;
	private static final int CONTEXT_HASH_COUNT = 20;
	
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
    
	/**
	 * Fetch and merge the phrase contexts from a database.
	 * @param phrase
	 * @return
	 * @throws SQLException
	 */
	public static int[] getPhraseContext(String phrase) throws SQLException {
		// Filter repeated words
		// word_set = S.toList $ S.fromList $ words phrase 
		PreparedStatement context_retriever = db.prep("SELECT context FROM rindex WHERE word == ?;");
		HashSet<String> word_set = new HashSet<String>();
		word_set.addAll(StringUtils.tokenize(phrase));
		
		// Sum the context vectors
		// foldl' (V.zipWith (+)) (V.replicate 1000) context_vectors
		int[] merged_context = new int[CONTEXT_LENGTH];
		for (String word : word_set) {
			context_retriever.setString(1, word);
			ResultSet sql_context = context_retriever.executeQuery();
			if (sql_context.next()) {
				java.nio.IntBuffer buffer = java.nio.ByteBuffer.wrap(sql_context.getBytes(1)).asIntBuffer();
				for (int i=0; i<merged_context.length; i++) {
					merged_context[i] += buffer.get(i);
				}
			}
		}
		return merged_context;
	}
    
    
}