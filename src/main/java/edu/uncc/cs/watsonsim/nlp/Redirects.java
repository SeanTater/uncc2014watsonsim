package edu.uncc.cs.watsonsim.nlp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import edu.uncc.cs.watsonsim.Environment;

public class Redirects {
	// This should be about 5 million words, which is reasonably small to store
	private static final Map<String, String> redirs = new HashMap<>(5000000);

	public Redirects(Environment env) {
		load(env);
	}
	
	private static synchronized void load(Environment env) {
		if (redirs.isEmpty()) {
			try {
				ResultSet rs = env.db.prep("SELECT source, target FROM wiki_redirects;").executeQuery();
				while (rs.next()) {
					redirs.put(rs.getString(1), rs.getString(2));
					redirs.put(rs.getString(2), rs.getString(1));
				}
			} catch (SQLException e) {
				// Leave the table blank and give 0's
				e.printStackTrace();
			}
			redirs.put("mammalia", "mammal");
			System.out.println("Loaded " + redirs.size() + " redirects");
		}
	}

	public boolean matches(String a, String b) {
		String maybe_b = redirs.get(a);
		return maybe_b == null ? false : maybe_b.equals(b);
	}
}
