package edu.uncc.cs.watsonsim;

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
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import edu.stanford.nlp.util.CacheMap;

/**
*@author Jagan Vujjini
*/
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	private static Analyzer analyzer = new StandardAnalyzer();
	private static Database db = new Database(); // Used for semantic distribution
	public static final int CONTEXT_LENGTH = 1000;
	
	//private static final int CONTEXT_HASH_COUNT = 20;
	private static final int CACHE_SIZE = 256;
	private static CacheMap<String, ArrayList<Double>> context_cache_map = new CacheMap<String, ArrayList<Double>>(CACHE_SIZE);
	
	/**
	 * Try to canonicalize a string somewhat conservatively.
	 * Basically, we:
	 *    ignore case
	 *    ignore punctuation such as (){}\\\\/\\[\\]—<>;:,.\"'“”‘’«»「」…-
	 *        The ' is probably debatable. The rest are generally
	 *        inaudible so they wouldn't have counted in a question
	 *        had they been spoken anyway.
	 *    ignore stopwords and some stems
	 *        This is the effect of Lucene's filters.
	 */
	public static String canonicalize(String dirty) {
		dirty = dirty
				.toLowerCase()
				.replaceAll("[(){}\\\\/\\[\\]—<>;:,.\"'“”‘’«»「」…-]", "")
				.trim();
		
		StringBuilder clean = new StringBuilder();
		for (String token : tokenize(dirty)) {
			clean.append(token);
			clean.append(' ');
		}
		return clean.toString().trim();
	}
	
	/**
	 * Remove all characters except alphanumerics and space.
	 * 
	 * This is a pretty wild thing to do since it clears out tons of usually
	 * useful stuff, like accent marks, punctuation, capitals..
	 */
	public static String sanitize(String input) {
		return input.replaceAll("[^A-Za-z0-9 ]", " ");
	}
	
	/** splits the given string into tokens */
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
	
	/** Conservatively normalize a string while tokenizing it */
	public static List<String> conservativeTokenize(String text) {
		String[] token_arr = text.toLowerCase().split("[ \t~`@#$%^&\\*\\(\\)_\\+-=\\{\\}\\[\\]:\";'<>\\?,./\\|\\\\]+");
		return Arrays.asList(token_arr);
	}
	
	
    /** Returns true if every non-stopword from candidate is found in reference */
    public static boolean matchSubset(String candidate, String reference){
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
	public static ArrayList<Double> getPhraseContextSafe(String phrase) {
		ArrayList<Double> merged_context = context_cache_map.get(	phrase);
		if (merged_context == null) {
			merged_context = new ArrayList<>();
			for (int i=0; i<CONTEXT_LENGTH; i++) merged_context.add(0.0);
			
			// Filter repeated words
			// word_set = S.toList $ S.fromList $ words phrase 
			PreparedStatement context_retriever = db.prep("SELECT context, count FROM rindex WHERE word == ?;");
			HashSet<String> word_set = new HashSet<String>();
			word_set.addAll(StringUtils.conservativeTokenize(phrase));
			
			// Sum the context vectors
			// foldl' (V.zipWith (+)) (V.replicate 1000) context_vectors
			try {
				for (String word : word_set) {
					context_retriever.setString(1, word);
					ResultSet sql_context = context_retriever.executeQuery();
					if (sql_context.next()) {
						java.nio.DoubleBuffer buffer = java.nio.ByteBuffer.wrap(sql_context.getBytes(1)).asDoubleBuffer();
						double total = 0;
						// Normalize each word so that they have the same weight when combined
						for (int i=0; i<CONTEXT_LENGTH; i++)
							total += buffer.get(i);
						for (int i=0; i<CONTEXT_LENGTH; i++)
							merged_context.set(i, merged_context.get(i) + (buffer.get(i) / total));
						
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
	public static double getCosineSimilarity(ArrayList<Double> vec1, ArrayList<Double> vec2) {
		double xy = 0;
		double xsquared = 0;
		double ysquared = 0;
		int length = Math.min(vec1.size(), vec2.size());
		for (int i=0; i<length; i++) {
			double x = vec1.get(i);
			double y = vec2.get(i);
			// Ignore uncertain dimensions
			// This little kludge makes a big difference
			if (Math.max(Math.abs(x), Math.abs(y)) > 0.1) {
				xy += x * y;
				xsquared += x * x;
				ysquared += y * y;	
			}
		}
		return xy / (Math.sqrt(xsquared) * Math.sqrt(ysquared) + Double.MIN_NORMAL);
	}
}
