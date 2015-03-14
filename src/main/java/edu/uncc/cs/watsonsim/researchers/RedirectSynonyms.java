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
	private final Logger log = Logger.getLogger(this.getClass());
	
	public RedirectSynonyms(Environment env) {
		db = env.db;
		s = db.prep("SELECT source from wiki_redirects where target = ?;");
		Score.register("IS_WIKI_REDIRECT", 0.0, Merge.Min);
	}

	@Override
	public void question(Question q) {
		List<Answer> prev_answers = new ArrayList<>();
		prev_answers.addAll(q);
		
		// For logging 
		int synonym_count = 0;
		
		for (Answer a : prev_answers) {
			try {
				s.setString(1, a.candidate_text);
				ResultSet results = s.executeQuery();
				while (results.next()) {
					synonym_count++;
					Answer new_answer = new Answer(
							new ArrayList<>(a.passages),
							a.scores.clone(),
							StringEscapeUtils.unescapeXml(results.getString("source")));
					Score.set(a.scores, "IS_WIKI_REDIRECT", 1.0);
					q.add(new_answer);
				}
			} catch (SQLException e) {
				// Just don't make any synonyms.
				return;
			}
		}
		
		log.info("Found " + synonym_count + " synonyms for " + prev_answers.size() +
				" candidate answers using Wikipedia redirects.");
	}
}
