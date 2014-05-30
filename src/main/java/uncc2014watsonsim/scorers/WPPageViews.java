package uncc2014watsonsim.scorers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Database;
import uncc2014watsonsim.Question;

public class WPPageViews extends AnswerScorer {
	Database db = new Database();
	PreparedStatement popularity_statement = db.prep(
			"SELECT pageviews FROM meta WHERE title = ? LIMIT 1;");

	@Override
	public double scoreAnswer(Question q, Answer a) {
		try {
			popularity_statement.setString(1, a.candidate_text);
			ResultSet rs = popularity_statement.executeQuery();
			
			if (rs.next()) {
				return rs.getDouble(1);
			} else {
				return Double.NaN;
			}
		} catch (SQLException se) {
			se.printStackTrace();
			return Double.NaN;
		}
	}
	
}
