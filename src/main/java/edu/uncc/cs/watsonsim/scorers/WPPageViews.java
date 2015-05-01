package edu.uncc.cs.watsonsim.scorers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Database;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Question;

public class WPPageViews extends AnswerScorer {
	private final Database db;
	private final PreparedStatement popularity_statement;
	public WPPageViews(Environment env) {
		db = env.db;
		popularity_statement = db.prep(
				"SELECT pageviews FROM sources WHERE title = ? LIMIT 1;");
	}

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
