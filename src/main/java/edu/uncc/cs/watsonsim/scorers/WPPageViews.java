package edu.uncc.cs.watsonsim.scorers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.nlp.ApproxStringIntMap;

public class WPPageViews extends AnswerScorer {
	private static ApproxStringIntMap pageviews = new ApproxStringIntMap(null);
	
	public WPPageViews(Environment env) {
		load(env);
	}
	
	private static synchronized void load(Environment env) {
		if (pageviews.isEmpty()) {
			int collisions = 0;
			try {
				ResultSet res = env.db.prep(
						"SELECT title, page_views FROM page_views;")
						.executeQuery();
				while (res.next()) {
					collisions += pageviews.containsKey(res.getString(1).toLowerCase()) ? 1 : 0;
					pageviews.put(res.getString(1).toLowerCase(), res.getInt(2));
				}
			} catch (SQLException e) {
				// at worst give 0s
				e.printStackTrace();
			}
			System.out.println("Loaded view data about " + pageviews.size() + " pages "
					+ "(" + collisions + " collisions)");
		}
	}

	@Override
	public double scoreAnswer(Question q, Answer a) {
		return pageviews.get(a.toString().toLowerCase());
	}
	
}
