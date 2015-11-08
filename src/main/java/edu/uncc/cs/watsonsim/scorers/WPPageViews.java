package edu.uncc.cs.watsonsim.scorers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Question;

public class WPPageViews extends AnswerScorer {
	private static final HashMap<String, Integer> pageviews
		= new HashMap<>(5000000);
	
	public WPPageViews(Environment env) {
		load(env);
	}
	
	private static synchronized void load(Environment env) {
		if (pageviews.isEmpty()) {
			try {
				ResultSet res = env.db.prep(
						"SELECT title, page_views FROM page_views;")
						.executeQuery();
				while (res.next()) {
					pageviews.merge(res.getString(1).toLowerCase(), res.getInt(1), Integer::sum);
				}
			} catch (SQLException e) {
				// at worst give 0s
				e.printStackTrace();
			}
			System.out.println("Loaded " + pageviews.size() + " pageview stats");
		}
	}

	@Override
	public double scoreAnswer(Question q, Answer a) {
		return pageviews.getOrDefault(a.text.toLowerCase(), 0);
	}
	
}
