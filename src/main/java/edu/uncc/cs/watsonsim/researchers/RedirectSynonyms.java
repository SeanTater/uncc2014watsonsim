package edu.uncc.cs.watsonsim.researchers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Database;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.Score;
import edu.uncc.cs.watsonsim.scorers.Merge;

/**
 * Create a bunch of new answers with the same passages based on "synonyms"
 * made from Wikipedia redirects.
 * 
 * @author Sean
 */
public class RedirectSynonyms extends Researcher {
	private final Database db;
	private final PreparedStatement s;
	
	public RedirectSynonyms(Environment env) {
		db = env.db;
		s = db.prep("SELECT source from wiki_redirects where target = ?;");
		Score.register("IS_WIKI_REDIRECT", 0.0, Merge.Min);
	}

	@Override
	public List<Answer> question(Question q, List<Answer> answers) {
		// For logging 
		int synonym_count = 0;
		List<Answer> new_answers = new ArrayList<Answer>();
		for (Answer a : answers) {
			try {
				s.setString(1, a.text);
				ResultSet results = s.executeQuery();
				while (results.next()) {
					synonym_count++;
					Answer new_answer = new Answer(
							new ArrayList<>(a.passages),
							a.scores.clone(),
							StringEscapeUtils.unescapeXml(results.getString("source")));
					Score.set(a.scores, "IS_WIKI_REDIRECT", 1.0);
					new_answers.add(new_answer);
				}
			} catch (SQLException e) {
				// Just don't make any synonyms.
				return answers;
			}
		}
		
		log.info("Found " + synonym_count + " synonyms for " + answers.size() +
				" candidate answers using Wikipedia redirects.");
		return new_answers;
	}
}
