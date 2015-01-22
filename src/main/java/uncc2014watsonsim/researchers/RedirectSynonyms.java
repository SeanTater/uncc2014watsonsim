package uncc2014watsonsim.researchers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Database;
import uncc2014watsonsim.Score;
import uncc2014watsonsim.scorers.Merge;

/**
 * Create a bunch of new answers with the same passages based on "synonyms"
 * made from Wikipedia redirects.
 * 
 * @author Sean
 */
public class RedirectSynonyms extends Researcher {
	Database db = new Database();

	PreparedStatement s = db.prep(
		"SELECT source from wiki_redirects where target = ?;");
	
	{
		Score.register("IS_WIKI_REDIRECT", 0.0, Merge.Min);
	}


	@Override
	public void question(Question q) {
		List<Answer> prev_answers = new ArrayList<>();
		prev_answers.addAll(q);
		
		for (Answer a : prev_answers) {
			try {
				s.setString(1, a.candidate_text);
				ResultSet results = s.executeQuery();
				while (results.next()) {
					Answer new_answer = new Answer(
							new ArrayList<>(a.passages),
							a.scores.clone(),
							results.getString("source"));
					Score.set(a.scores, "IS_WIKI_REDIRECT", 1.0);
					q.add(new_answer);
				}
			} catch (SQLException e) {
				// Just don't make any synonyms.
				return;
			}
		}
	}
}
