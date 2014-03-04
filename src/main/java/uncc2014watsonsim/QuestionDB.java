package uncc2014watsonsim;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionDB {
	static Connection conn;
	static {
		try {
		    // load the sqlite-JDBC driver using the current class loader
		    Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:data" + File.separator + "questions.db");
			conn.createStatement().execute("PRAGMA journal_mode = WAL;");
			conn.createStatement().execute("PRAGMA journal_size_limit = 1048576;"); // 1MB
			conn.createStatement().execute("PRAGMA synchronous = OFF;");
		} catch(SQLException | ClassNotFoundException e) {
	       // if the error message is "out of memory", 
	       // it probably means no database file is found
	       System.err.println(e.getMessage());
		}
		
		// JDBC's SQLite uses autocommit (So commit() is redundant)
		// Furthermore, close() is a no-op as long as the results are commit()'d
		// So don't bother adding code to do all that.
	}
	
	/** Replace the results for a single question 
	 * @throws SQLException */
	public static void replace_cache(Question q, List<ResultSet> results) throws SQLException {
		// Get a list of results and populate the questions with them
	    PreparedStatement s = conn.prepareStatement("delete from results where question = ?");
	    s.setInt(1, q.id);
	    s.execute();
	    
		s = conn.prepareStatement("insert into results(question, title, fulltext, engine, rank, score, correct)"
				+ "values (?, ?, ?, ?, ?, ?, ?);");
		for (ResultSet r : results) {
			s.setLong(1, q.id);
			s.setString(2, r.getTitle());
			s.setString(3, r.getFullText());
			// In this case, we know there is exactly one engine so it is safe to access it.
			s.setString(4, r.engines.get(0).name);
			s.setLong(5, r.engines.get(0).rank);
			s.setDouble(6, r.engines.get(0).score);
			s.setBoolean(7, r.isCorrect());
			s.addBatch();
		}
		s.executeBatch();
	}
	
	/** Fetch Questions with existing Indri/Lucene/Google search results */
	public static List<Question> fetch_cached(int start, int length) throws SQLException {
		return fetch(String.format( // no ending ;!
			"where cached order by rowid limit %d offset %d", length, start));
	}
	
	/** Fetch Questions without existing results */
	public static List<Question> fetch_uncached(int start, int length) throws SQLException {
		return fetch(String.format( // no ending ;!
			"where not cached order by rowid limit %d offset %d", length, start));
	}
	
	public static List<Question> fetch(String q_select) throws SQLException {
		// Get a list of questions, ordered so that it is consistent
		java.sql.ResultSet sql = conn.createStatement().executeQuery(
				"select * from questions " + q_select);
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
		
		// Get a list of results and populate the questions with them
		sql = conn.createStatement().executeQuery(
				"select * from results where question in ("
				+ "select rowid from questions "+ q_select +");");
		while(sql.next()){
			uncc2014watsonsim.ResultSet r = new uncc2014watsonsim.ResultSet(
				sql.getString("title"),
				sql.getString("text"),
				sql.getString("engine"),
				sql.getInt("rank"),
				sql.getDouble("score"),
				sql.getBoolean("correct")
			);
			questions.get(sql.getInt("question")).add(r);
		}
		return new ArrayList<Question>(questions.values());
	}
}
