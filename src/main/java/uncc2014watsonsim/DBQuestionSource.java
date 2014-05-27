package uncc2014watsonsim;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DBQuestionSource extends QuestionSource {
	private static final long serialVersionUID = 1L;
	private static final Database db = new Database();

	
	/** Get length questions, starting with question id > (not >=) start
	 * In hindsight >= would have been better but now it needs to be consistent.
	 */
	public DBQuestionSource(int start, int length) throws SQLException {
		// Get a list of questions, ordered so that it is consistent
		PreparedStatement bulk_select_questions = db.prep(
				"select * from questions where rowid > ? order by rowid limit ?;");
		bulk_select_questions.setInt(1, start);
		bulk_select_questions.setInt(2, length);
		read_results(bulk_select_questions.executeQuery());
	}
	
	/** Run an arbitrary query on the database to get questions.
	 */
	public DBQuestionSource(String conditions) throws SQLException {
		// Get a list of questions, ordered so that it is consistent
		PreparedStatement query = db.prep("select * from questions "
				+ conditions + ";");
		read_results(query.executeQuery());
	}
	
	/** Replace the cached results for a single question.
	 * Every answer must have _one_ document. The database doesn't support more.
	 * @throws SQLException */
	public static void replace_cache(Question q, List<Answer> results) throws SQLException {
		// Get a list of results and populate the questions with them
		PreparedStatement bulk_insert = db.prep(
				"insert or replace into results(question, title, fulltext, engine, rank, score, reference) "
				+ "values (?, ?, ?, ?, ?, ?, ?);");
	    
		for (Answer r : results) {
			bulk_insert.setLong(1, q.id);
			bulk_insert.setString(2, r.candidate_text);
			bulk_insert.setString(3, r.passages.get(0).getText());
			//TODO: we need to generalize this
			String engine = r.passages.get(0).engine_name;
			bulk_insert.setString(4, engine);
			bulk_insert.setDouble(5, r.scores.get(engine.toUpperCase()+"_RANK"));
			try {
				bulk_insert.setDouble(6, r.scores.get(engine.toUpperCase()+"_SCORE"));
			} catch(NullPointerException | IllegalArgumentException e) {
				// ignore as google/bing don't have scores!
				bulk_insert.setDouble(6, 0.0);
			}
			bulk_insert.setString(7, r.passages.get(0).reference);
			bulk_insert.addBatch();
		}
		bulk_insert.executeBatch();
	}
	
	public void read_results(ResultSet sql) throws SQLException {
		while(sql.next()){
			Question q = Question.known(
					sql.getString("question"),
					sql.getString("answer"),
					sql.getString("category")
				);
			q.id = sql.getInt("rowid");
			add(q);
		}
	}
}
