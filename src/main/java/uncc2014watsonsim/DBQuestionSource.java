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
	static Connection conn;
	static PreparedStatement bulk_delete;
	static PreparedStatement bulk_insert;
	static PreparedStatement bulk_select_questions;
	static PreparedStatement bulk_select_results;
	static {
		try {
		    // load the sqlite-JDBC driver using the current class loader
		    Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:data" + File.separator + "questions.db");
			conn.createStatement().execute("PRAGMA journal_mode = TRUNCATE;");
			conn.createStatement().execute("PRAGMA journal_size_limit = 1048576;"); // 1MB
			conn.createStatement().execute("PRAGMA synchronous = OFF;");
			
			bulk_delete = conn.prepareStatement("delete from results where question = ?");
			bulk_insert = conn.prepareStatement("insert into results(question, title, fulltext, engine, rank, score)"
					+ " values (?, ?, ?, ?, ?, ?);");

			bulk_select_questions = conn.prepareStatement("select * from questions where rowid > ? order by rowid limit ?;");
			bulk_select_results = conn.prepareStatement("select results.question as question_id, title, fulltext, category, engine, rank, score from results inner join questions on results.question = questions.rowid" +
					" where (results.question >= ?) and (results.question <= ?);");
		} catch(SQLException | ClassNotFoundException e) {
	       // if the error message is "out of memory", 
	       // it probably means no database file is found
	       System.err.println(e.getMessage());
		}
		
		// JDBC's SQLite uses autocommit (So commit() is redundant)
		// Furthermore, close() is a no-op as long as the results are commit()'d
		// So don't bother adding code to do all that.
	}	

	public DBQuestionSource() throws Exception {
		fetch_without_results(0, 100);
	}
	
	/** Replace the results for a single question 
	 * @throws SQLException */
	public static void replace_cache(Question q, List<ResultSet> results) throws SQLException {
		// Get a list of results and populate the questions with them
	    bulk_delete.setLong(1, q.id);
	    bulk_delete.execute();
	    
		for (ResultSet r : results) {
			bulk_insert.setLong(1, q.id);
			bulk_insert.setString(2, r.getTitle());
			bulk_insert.setString(3, r.getFullText());
			// In this case, we know there is exactly one engine so it is safe to access it.
			bulk_insert.setString(4, r.engines.get(0).name);
			bulk_insert.setLong(5, r.engines.get(0).rank);
			bulk_insert.setDouble(6, r.engines.get(0).score);
			bulk_insert.addBatch();
		}
		bulk_insert.executeBatch();
	}
	
	public static Map<Integer, Question> fetch_map_without_results(int start, int length) throws SQLException {
		// Get a list of questions, ordered so that it is consistent
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
	
	public static QuestionSource fetch_with_results(int start, int length) throws SQLException {
	    Map<Integer, Question> questions = fetch_map_without_results(start, length);
		// Get a list of results and populate the questions with them
	    System.out.println("look for q from " + Collections.min(questions.keySet()) + " to " + Collections.max(questions.keySet()));
		bulk_select_results.setInt(1, Collections.min(questions.keySet()));
		bulk_select_results.setInt(2, Collections.max(questions.keySet()));
		java.sql.ResultSet sql = bulk_select_results.executeQuery();
		while(sql.next()){
			questions.get(sql.getInt("question_id")).add(new uncc2014watsonsim.ResultSet(
				sql.getString("title"),
				sql.getString("fulltext"),
				sql.getString("engine"),
				sql.getInt("rank"),
				sql.getDouble("score")
			));
		}
		return new QuestionSource(questions.values());
	}
}
