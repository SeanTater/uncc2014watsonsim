package uncc2014watsonsim.researchers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Database;

/**
 * Create a bunch of new answers with the same passages based on "synonyms"
 * made from Wikipedia redirects.
 * 
 * @author Sean
 */
public class RedirectSynonyms extends Researcher {
	Database db = new Database();

	PreparedStatement s = db.prep(
		"SELECT source from wiki_redirects where target = ? collate nocase;");

	@Override
	public void question(Question q) {
		List<Answer> prev_answers = new ArrayList<>();
		prev_answers.addAll(q);
		
		for (Answer a : prev_answers) {
			try {
				s.setString(1, a.candidate_text);
				ResultSet results = s.executeQuery();
				while (results.next()) {
					q.add(new Answer(a.passages, a.scores, results.getString("source")));
				}
			} catch (SQLException e) {
				// Just don't make any synonyms.
				return;
			}
		}
	}
}
