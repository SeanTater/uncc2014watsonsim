package uncc2014watsonsim;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBQuestionSource extends ArrayList<Question> {
	private static final long serialVersionUID = 1L;
	private static final Database db = new Database();
	
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
			bulk_insert.setString(1, q.getRaw_text());
			bulk_insert.setString(2, r.candidate_text);
			bulk_insert.setString(3, r.direct_passages.get(0).getText());
			//TODO: we need to generalize this
			String engine = r.direct_passages.get(0).engine_name;
			bulk_insert.setString(4, engine);
			bulk_insert.setDouble(5, r.scores.get(engine.toUpperCase()+"_RANK"));
			try {
				bulk_insert.setDouble(6, r.scores.get(engine.toUpperCase()+"_SCORE"));
			} catch(NullPointerException | IllegalArgumentException e) {
				// ignore as google/bing don't have scores!
				bulk_insert.setDouble(6, 0.0);
			}
			bulk_insert.setString(7, r.direct_passages.get(0).reference);
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
			add(q);
		}
	}
}
