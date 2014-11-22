package uncc2014watsonsim.researchers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

	@Override
	public void answer(Question q, Answer a) {
		PreparedStatement s = db.prep(
			"SELECT source_title FROM redirects INNER JOIN meta ON target_id=id where title = ?;");
		try {
			s.setString(1, a.candidate_text);
			ResultSet results = s.executeQuery();
			while (results.next()) {
				Answer new_a = new Answer(results.getString("source_title"));
				new_a.direct_passages.addAll(a.direct_passages);
				q.add(new_a);
			}
		} catch (SQLException e) {
			// Just don't make any synonyms.
			return;
		}
		
	}
}
