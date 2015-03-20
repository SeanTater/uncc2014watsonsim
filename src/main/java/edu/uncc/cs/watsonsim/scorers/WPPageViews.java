package edu.uncc.cs.watsonsim.scorers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Database;
import edu.uncc.cs.watsonsim.Question;

public class WPPageViews extends AnswerScorer {
	Database db = new Database();
	PreparedStatement popularity_statement = db.prep(
			"SELECT pageviews FROM meta WHERE title = ? LIMIT 1;");

	@Override
	public synchronized double scoreAnswer(Question q, Answer a) {
		try {
			popularity_statement.setString(1, a.text);
			ResultSet rs = popularity_statement.executeQuery();
			
			if (rs.next()) {
				return rs.getDouble(1);
			} else {
				return -1;
			}
		} catch (SQLException se) {
			se.printStackTrace();
			return -1;
		}
	}
	
}
