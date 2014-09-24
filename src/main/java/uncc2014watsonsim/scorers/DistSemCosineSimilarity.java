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

public class DistSemCosineSimilarity extends AnswerScorer {
	static {
		// Register the score names _once_
		Score.registerAnswerScore("QUESTION_CONTEXT_MAGNITUDE");
		Score.registerAnswerScore("ANSWER_CONTEXT_MAGNITUDE");
		for (int i=0; i<StringUtils.CONTEXT_LENGTH; i++) {
			Score.registerAnswerScore("QUESTION_CONTEXT_"+i);
			Score.registerAnswerScore("ANSWER_CONTEXT_"+i);
		}
	}
	
	@Override
	public double scoreAnswer(Question q, Answer a) {
		int[] q_context;
		int[] a_context;
		try {
			q_context = StringUtils.getPhraseContext(q.text);
			a_context = StringUtils.getPhraseContext(a.candidate_text);
		} catch (SQLException e) {
			// Fail silently
			return Double.NaN;
		}
		
		double xy = 0;
		double xsquared = 0;
		double ysquared = 0;
		for (int i=0; i<StringUtils.CONTEXT_LENGTH; i++) {
			int x = q_context[i];
			int y = a_context[i];
			xy += x * y;
			xsquared += x * x;
			ysquared += y * y;
		}
		return xy / Math.max(Math.sqrt(Math.abs(xsquared)) * Math.sqrt(Math.abs(ysquared)), Double.MIN_NORMAL);
	}

}
