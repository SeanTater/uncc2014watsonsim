package uncc2014watsonsim;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		} catch(SQLException | ClassNotFoundException e) {
	       // if the error message is "out of memory", 
	       // it probably means no database file is found
	       System.err.println(e.getMessage());
		}
		
		// JDBC's SQLite uses autocommit (So commit() is redundant)
		// Furthermore, close() is a no-op as long as the results are commit()'d
		// So don't bother adding code to do all that.
	}
	
	public static List<Question> fetch(int start, int length) throws SQLException {
		String q_select = String.format("select * from questions order by rowid limit %d offset %d;", length, start);
		// Get a list of questions, ordered so that it is consistent
		ResultSet sql = conn.createStatement().executeQuery(q_select);
		Map<Integer, Question> questions = new HashMap<Integer, Question>();
		while(sql.next()){
			Question q = Question.known(
				sql.getString("question"),
				sql.getString("answer"),
				sql.getString("category")
			);
			questions.put(sql.getInt("rowid"), q);
		}
		
		// Get a list of results and populate the questions with them
		sql = conn.createStatement().executeQuery(
				"select * from results where question in ("+ q_select +");");
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
		return (List<Question>) questions.values();
	}
}
