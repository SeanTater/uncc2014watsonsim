package uncc2014watsonsim;

import java.io.IOException;
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

import edu.stanford.nlp.util.CacheMap;

/**
*@author Jagan Vujjini
*/
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	private static Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_47);
	private static Database db = new Database(); // Used for semantic distribution
	public static final int CONTEXT_LENGTH = 1000;
	
	private static final int CONTEXT_HASH_COUNT = 20;
	private static final int CACHE_SIZE = 256;
	private static CacheMap<String, ArrayList<Integer>> context_cache_map = new CacheMap<String, ArrayList<Integer>>(CACHE_SIZE);
	
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
	 * The safe part about this is that it may give the wrong answer but not
	 * an exception.
	 * @param phrase
	 * @return the merged phrase vector, unless an error occurred.
	 */
	public static ArrayList<Integer> getPhraseContextSafe(String phrase) {
		ArrayList<Integer> merged_context = context_cache_map.get(phrase);
		if (merged_context == null) {
			// Filter repeated words
			// word_set = S.toList $ S.fromList $ words phrase 
			PreparedStatement context_retriever = db.prep("SELECT context FROM rindex WHERE word == ?;");
			HashSet<String> word_set = new HashSet<String>();
			word_set.addAll(StringUtils.tokenize(phrase));
			
			// Sum the context vectors
			// foldl' (V.zipWith (+)) (V.replicate 1000) context_vectors
			merged_context = new ArrayList<>();
			try {
				for (String word : word_set) {
					context_retriever.setString(1, word);
					ResultSet sql_context = context_retriever.executeQuery();
					if (sql_context.next()) {
						java.nio.IntBuffer buffer = java.nio.ByteBuffer.wrap(sql_context.getBytes(1)).asIntBuffer();
						for (int i=0; i<merged_context.size(); i++) {
							merged_context.set(i, merged_context.get(i) + buffer.get(i));
						}
					}
				}
			} catch (SQLException e) {} // At worst, return what we have so far. Maybe nothing.
		}
		context_cache_map.put(phrase, merged_context);
		return merged_context;
	}
    
	/**
	 * Find the cosine similarity between two vectors
	 * 1 is identical, 0 is orthogonal
	 * Synonyms are usually between 0.6 and 0.8.
	 * @param vec1
	 * @param vec2
	 * @return double between 0 and 1
	 */
	public static double getCosineSimilarity(ArrayList<Integer> vec1, ArrayList<Integer> vec2) {
		double xy = 0;
		double xsquared = 0;
		double ysquared = 0;
		int length = Math.min(vec1.size(), vec2.size());
		for (int i=0; i<length; i++) {
			int x = vec1.get(i);
			int y = vec2.get(i);
			xy += x * y;
			xsquared += x * x;
			ysquared += y * y;
		}
		return xy / Math.max(Math.sqrt(Math.abs(xsquared)) * Math.sqrt(Math.abs(ysquared)), Double.MIN_NORMAL);
	}
    
    
}