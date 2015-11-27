package edu.uncc.cs.watsonsim.nlp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import edu.uncc.cs.watsonsim.Environment;

public class Redirects {
	
	// Here's a cute trick: we don't know the redirect sources or targets, but
	// we can refer the target to itself and then match on whether the sources
	// and targets refer (as targets) to the same hash
	private static ApproxStringIntMap redirects = new ApproxStringIntMap(null);

	public Redirects(Environment env) {
		load(env);
	}
	
	private static synchronized void load(Environment env) {
		if (redirects.isEmpty()) {
			int collisions = 0;
			try {
				ResultSet rs = env.db.prep(
						"SELECT source, target FROM wiki_redirects;"
						).executeQuery();
				/* There is a trick here. Ordering by target means we don't
				 * need to check for the ID of the target each time, if it is
				 * different, then it will be the next in order.
				 */
				while (rs.next()) {
					collisions += redirects.containsKey(rs.getString(1)) ? 1 : 0;
					redirects.put(rs.getString(1), rs.getString(2).hashCode());
					redirects.put(rs.getString(2), rs.getString(2).hashCode());
				}
			} catch (SQLException e) {
				// Leave the table blank and give 0's
				e.printStackTrace();
			}
			redirects.put("mammalia", "mammal".hashCode());
			System.out.println("Loaded " + redirects.size() + " redirects "
					+ "(" + collisions + " collisions)");
		}
	}

	public boolean matches(String a, String b) {
		int a_redir = redirects.get(a);
		int b_redir = redirects.get(b);
		return a_redir != 0 && a_redir == b_redir;
	}
}
