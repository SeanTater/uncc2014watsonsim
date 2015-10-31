package edu.uncc.cs.watsonsim.scorers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.Function;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Database;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.Question;

public class Entropy extends AnswerScorer {
	// This should be about 5 million words, which is reasonably small to store
	private static final Map<String, Double> dict = new HashMap<>(5000000);

	public Entropy(Environment env) {
		load(env);
	}
	
	private static synchronized void load(Environment env) {
		if (dict.isEmpty()) {
			try {
				ResultSet rs = env.db.prep("SELECT word, p FROM entropy;").executeQuery();
				while (rs.next()) {
					dict.put(rs.getString(1), rs.getDouble(2));
				}
			} catch (SQLException e) {
				// Leave the table blank and give 0's
				e.printStackTrace();
			}
			System.out.println("Loaded " + dict.size() + " words' entropy");
		}
	}
	
	protected double entropy(Iterable<String> targets) {
		double ent = 0;
		for (String target: targets) {
			ent += dict.getOrDefault(target, 0.0);
		}
		return ent;
	}

	@Override
	public double scoreAnswer(Question q, Answer a) {
		return entropy(a.memo(Phrase.tokens));
	}

}
