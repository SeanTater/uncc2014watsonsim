package uncc2014watsonsim;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBQuestionSource extends QuestionSource {
	private static final long serialVersionUID = 1L;
	static final SQLiteDB db = new SQLiteDB("questions");

	public DBQuestionSource() throws Exception {
		fetch_without_results(0, 100);
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
	
	public static Map<Integer, Question> fetch_map_without_results(int start, int length) throws SQLException {
		// Get a list of questions, ordered so that it is consistent
		PreparedStatement bulk_select_questions = db.conn.prepareStatement(
				"select * from questions where rowid > ? order by rowid limit ?;");
		bulk_select_questions.setInt(1, start);
		bulk_select_questions.setInt(2, length);
		java.sql.ResultSet sql = bulk_select_questions.executeQuery();
		
		Map<Integer, Question> questions = new HashMap<Integer, Question>();
		while(sql.next()){
			Question q = Question.known(
				sql.getString("question"),
				sql.getString("answer"),
				sql.getString("category")
			);
			q.id = sql.getInt("rowid");
			questions.put(q.id, q);
		}
		return questions;
	}
	
	public void fetch_without_results(int start, int length) throws SQLException {
		this.addAll(fetch_map_without_results(start, length).values());
	}
	
	public QuestionSource fetch_with_results(int start, int length) throws SQLException {
	    Map<Integer, Question> questions = fetch_map_without_results(start, length);
		// Get a list of results and populate the questions with them
	    System.out.println("look for q from " + Collections.min(questions.keySet()) + " to " + Collections.max(questions.keySet()));

		PreparedStatement bulk_select_results = db.prep(
				"select results.question "
				+ "as question_id, title, fulltext, category, engine, rank, score, reference "
				+ "from results inner join questions on results.question = questions.rowid "
				+ "where (results.question >= ?) and (results.question <= ?);");
		bulk_select_results.setInt(1, Collections.min(questions.keySet()));
		bulk_select_results.setInt(2, Collections.max(questions.keySet()));
		java.sql.ResultSet sql = bulk_select_results.executeQuery();
		while(sql.next()){
			questions.get(sql.getInt("question_id")).add(new uncc2014watsonsim.Answer(
				sql.getString("engine"),
				sql.getString("title"),
				sql.getString("fulltext"),
				sql.getString("reference"),
				sql.getInt("rank"),
				sql.getDouble("score")
			));
		}
		return new QuestionSource(questions.values());
	}
}
