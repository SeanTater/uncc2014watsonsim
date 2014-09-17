package uncc2014watsonsim.scorers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

import org.apache.mahout.math.Arrays;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Database;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;
import uncc2014watsonsim.StringUtils;

public class RandomIndexingCosineSimilarity extends AnswerScorer {
	private static Database db = new Database();
	private static final int CONTEXT_LENGTH = 1000;
	
	static {
		// Register the score names _once_
		Score.registerAnswerScore("QUESTION_CONTEXT_MAGNITUDE");
		Score.registerAnswerScore("ANSWER_CONTEXT_MAGNITUDE");
		for (int i=0; i<CONTEXT_LENGTH; i++) {
			Score.registerAnswerScore("QUESTION_CONTEXT_"+i);
			Score.registerAnswerScore("ANSWER_CONTEXT_"+i);
		}
	}
	
	@Override
	public double scoreAnswer(Question q, Answer a) {
		int[] q_context;
		int[] a_context;
		try {
			q_context = phraseToContext(q.text);
			a_context = phraseToContext(a.candidate_text);
		} catch (SQLException e) {
			// Fail silently
			return Double.NaN;
		}
		
		vectorScore(a, "QUESTION_CONTEXT_", q_context);
		vectorScore(a, "ANSWER_CONTEXT_", a_context);
		
		double xy = 0;
		double xsquared = 0;
		double ysquared = 0;
		for (int i=0; i<CONTEXT_LENGTH; i++) {
			int x = q_context[i];
			int y = a_context[i];
			xy += x * y;
			xsquared += x * x;
			ysquared += y * y;
		}
		return xy / Math.max(Math.sqrt(Math.abs(xsquared)) * Math.sqrt(Math.abs(ysquared)), Double.MIN_NORMAL);
	}
	
	/**
	 * Concatenate the context vectors into the score list
	 * @param a  The relevant answer
	 * @param name  The name of the score, with trailing underscore
	 * @param context  The list of scores to add.
	 */
	private void vectorScore(Answer a, String name, int[] context) {
		// sum()
		double sum = 0.0;
		for (int i=0; i < context.length; i++) sum += context[i];
		a.score(name + "MAGNITUDE", sum);
		
		// x[i] = y[i] / sum
		double[] normalized_context = new double[context.length];
		for (int i=0; i < context.length; i++) normalized_context[i] = context[i] / sum;
		
		// score each
		for (int i=0; i < context.length; i++) {
			a.score(name + i, normalized_context[i]);
		}
	}
	
	/**
	 * Fetch and merge the phrase contexts from a database.
	 * @param phrase
	 * @return
	 * @throws SQLException
	 */
	private int[] phraseToContext(String phrase) throws SQLException {
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
