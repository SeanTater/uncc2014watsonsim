package uncc2014watsonsim;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBQuestionSource extends QuestionSource {
	private static final long serialVersionUID = 1L;
	private static final SQLiteDB db = new SQLiteDB("questions");

	
	/** Get length questions, starting with question id > (not >=) start
	 * In hindsight >= would have been better but now it needs to be consistent.
	 */
	public DBQuestionSource(int start, int length) throws Exception {
		// Get a list of questions, ordered so that it is consistent
		PreparedStatement bulk_select_questions = db.prep(
				"select * from questions where rowid > ? order by rowid limit ?;");
		bulk_select_questions.setInt(1, start);
		bulk_select_questions.setInt(2, length);
		read_results(bulk_select_questions.executeQuery());
	}
	
	/** Run an arbitrary query on the database to get questions.
	 */
	public DBQuestionSource(String conditions) throws Exception {
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
		PreparedStatement bulk_delete = db.prep("delete from results where question = ?;");
		PreparedStatement bulk_insert = db.prep(
				"insert into results(question, title, fulltext, engine, rank, score, reference) "
				+ "values (?, ?, ?, ?, ?, ?, ?);");
	    bulk_delete.setLong(1, q.id);
	    bulk_delete.execute();
	    
		for (Answer r : results) {
			bulk_insert.setLong(1, q.id);
			bulk_insert.setString(2, r.getTitle());
			bulk_insert.setString(3, r.getFullText());
			// In this case, we know there is exactly one document so it is safe to access it.
			bulk_insert.setString(4, r.docs.get(0).engine_name);
			bulk_insert.setLong(5, r.docs.get(0).rank);
			bulk_insert.setDouble(6, r.docs.get(0).score);
			bulk_insert.setString(7, r.docs.get(0).reference);
			bulk_insert.addBatch();
		}
		bulk_insert.executeBatch();
	}
	
	public void read_results(ResultSet sql) throws SQLException {
		while(sql.next()){
			add(Question.known(
				sql.getString("question"),
				sql.getString("answer"),
				sql.getString("category")
			));
		}
	}
}
