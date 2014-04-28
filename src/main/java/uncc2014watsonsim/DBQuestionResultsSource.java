package uncc2014watsonsim;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uncc2014watsonsim.research.PassageRetrieval;

/**
*
* @author walid shalaby (adapted from DBQuestionResultsSource)
*/
public class DBQuestionResultsSource extends QuestionSource {
	private static final long serialVersionUID = 1L;
	private static final SQLiteDB db = new SQLiteDB("questions");

	
	/** Get length questions, starting with question id > (not >=) start
	 * In hindsight >= would have been better but now it needs to be consistent.
	 */
	public DBQuestionResultsSource(int start, int length) throws Exception {
		// Get a list of questions, ordered so that it is consistent
		PreparedStatement bulk_select_questions = db.prep(
				"select q.question as raw_text, r.title as candidate_answer from questions q, results r where q.rowid=r.question and rowid > ? order by rowid limit ?;");
		bulk_select_questions.setInt(1, start);
		bulk_select_questions.setInt(2, length);
		load_results(bulk_select_questions.executeQuery());
	}
	
	/** Run an arbitrary query on the database to get questions.
	 */
	public DBQuestionResultsSource(String conditions) throws Exception {
		// Get a list of questions, ordered so that it is consistent
		PreparedStatement query = db.prep("select q.rowid as id, q.question as raw_text, r.title as candidate_answer from questions q, results r where q.rowid=r.question  "
				+ conditions + " order by q.rowid;");
		load_results(query.executeQuery());
	}
	
	/** Replace the cached results for a single question.
	 * Every answer must have _one_ document. The database doesn't support more.
	 * @param candidate_text 
	 * @throws SQLException */
	public static void store_passages(Question q, List<Passage> results) throws SQLException {
		// insert supporting passages into DB
		PreparedStatement bulk_insert = db.prep(
				"insert or replace into passages(question, title, fulltext, engine, rank, score, reference, candidate_answer, query) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?);");
	    
		for (Passage r : results) {
			bulk_insert.setLong(1, q.id);
			bulk_insert.setString(2, r.title);
			bulk_insert.setString(3, r.getText());
			//TODO: we need to generalize this
			String engine = r.engine_name;
			bulk_insert.setString(4, engine);
			bulk_insert.setDouble(5, r.scores.get(engine.toUpperCase()+"_RANK"));
			try {
				bulk_insert.setDouble(6, r.scores.get(engine.toUpperCase()+"_SCORE"));
			} catch(NullPointerException | IllegalArgumentException e) {
				// ignore as google/bing don't have scores!
				bulk_insert.setDouble(6, 0.0);
			}
			bulk_insert.setString(8, q.answer.candidate_text);
			bulk_insert.setString(9, PassageRetrieval.getPassageQuery(q, q.answer));
			bulk_insert.addBatch();
		}
		bulk_insert.executeBatch();
	}
	
	public void load_results(ResultSet sql) throws SQLException {
		while(sql.next()){
			Question q = new Question(sql.getString("raw_text"));
			q.id = sql.getInt("id");
			q.answer = new Answer(new Passage("", sql.getString("candidate_answer"), "", ""));
			add(q);
		}
	}
}
