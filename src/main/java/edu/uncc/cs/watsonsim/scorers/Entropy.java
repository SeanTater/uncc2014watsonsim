package edu.uncc.cs.watsonsim.scorers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.nlp.ApproxStringIntMap;
import edu.uncc.cs.watsonsim.nlp.StringStack;

public class Entropy extends AnswerScorer {
	// This is a custom approach for about a 10-fold reduction in memory
	private static final double mult = 2<<20;
	private static ApproxStringIntMap dict = new ApproxStringIntMap(new StringStack());
	
	public Entropy(Environment env) {
		load(env);
	}
	
	private static synchronized void load(Environment env) {
		if (dict.isEmpty()) {
			int collisions = 0;
			try {
				ResultSet rs = env.db.prep("SELECT word, p FROM entropy;").executeQuery();
				while (rs.next()) {
					collisions += dict.containsKey(rs.getString(1)) ? 1 : 0;
					// This mult is to put enough of the double's precision in
					// the int. p is logarithmic so overflow is not a problem.
					dict.put(rs.getString(1), (int)(rs.getDouble(2)*mult));
				}
			} catch (SQLException e) {
				// Leave the table blank and give 0's
				e.printStackTrace();
			}
			System.out.println("Loaded " + dict.size() + " words' entropy "
					+ "(" + collisions + " collisions)");
		}
	}
	
	protected double entropy(Iterable<String> targets) {
		double ent = 0;
		for (String target: targets) {
			ent += dict.get(target) / mult;
		}
		return ent;
	}

	@Override
	public double scoreAnswer(Question q, Answer a) {
		return entropy(a.memo(Phrase.tokens));
	}

}
