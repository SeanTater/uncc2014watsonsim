package uncc2014watsonsim;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
*
* @author walid shalaby (adapted from DBQuestionResultsSource)
*/
public class DBResultsPassagesSource extends QuestionSource {
	private static final long serialVersionUID = 1L;
	private static final SQLiteDB db = new SQLiteDB("questions");

	
	/** Get length questions, starting with question id > (not >=) start
	 * In hindsight >= would have been better but now it needs to be consistent.
	 */
	public DBResultsPassagesSource(int start, int length) throws Exception {
		// Get a list of questions, ordered so that it is consistent
		PreparedStatement bulk_select_questions = db.prep(
				"select q.rowid as id, q.question as raw_text " + 
				"r.rank as rrank, r.score as rscore, r.engine as rengine, r.correct as rcorrect, r.title as rtitle, r.fulltext as rfulltext, r.reference as rreference" + 
				"p.rank as prank, p.score as pscore, p.engine as pengine, p.title as ptitle, p.fulltext as pfulltext, p.query as pquery" + 
				"from questions q, results r, passages p " + 
				"where q.rowid=r.question and p.question=r.question and p.candidate_answer=r.title and rowid > ? order by rowid limit ?;");
		bulk_select_questions.setInt(1, start);
		bulk_select_questions.setInt(2, length);
		load_results_passages(bulk_select_questions.executeQuery());
	}
	
	/** Run an arbitrary query on the database to get questions.
	 */
	public DBResultsPassagesSource(String conditions) throws Exception {
		// Get a list of questions, ordered so that it is consistent
		PreparedStatement query = db.prep(
				"select q.rowid as id, q.question as raw_text, "+ 
				"r.rank as rrank, r.score as rscore, r.engine as rengine, r.correct as correct, r.title as rtitle, r.fulltext as rfulltext, r.reference as rreference, " + 
				"p.rank as prank, p.score as pscore, p.engine as pengine, p.title as ptitle, p.fulltext as pfulltext, p.reference as preference, p.query as pquery " + 
				" from questions q, results r, passages p " + 
				"where q.rowid=r.question and p.question=r.question and p.candidate_answer=r.title "
				+ conditions + " order by id;");
		load_results_passages(query.executeQuery());
	}
	
	public void load_results_passages(ResultSet sql) throws SQLException {
		Question q = null;
		while(sql.next()){
			if(q==null || q.id!=sql.getInt("id")) {	// new or first question
				if(q!=null) {	// new question
					add(q);
				}
			
				q = new Question(sql.getString("raw_text"));
				q.id = sql.getInt("id");				
				q.add(fillNewAnswer(sql));
				q.get(q.size()-1).passages.add(fillNewPassage(sql));
			}
			else { // same question
				// check is it a new answer
				if(q.get(q.size()-1).candidate_text.equals(sql.getString("rtitle"))==false)	// new answer
					q.add(fillNewAnswer(sql));
				
				q.get(q.size()-1).passages.add(fillNewPassage(sql));
			}
		}
		add(q);
	}
	private Passage fillNewPassage(ResultSet sql) {
		// TODO Auto-generated method stub
		Passage p = null;
		try {
			p = new Passage(sql.getString("pengine"), sql.getString("ptitle"), sql.getString("pfulltext"), sql.getString("preference"));
			p.score(sql.getString("pengine").toUpperCase()+"_RANK", (double)sql.getInt("prank"));
			p.score(sql.getString("pengine").toUpperCase()+"_SCORE", sql.getDouble("pscore"));
			p.score("CORRECT", sql.getBoolean("correct")==false? 0.0:1.0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}

	private Answer fillNewAnswer(ResultSet sql) {
		// TODO Auto-generated method stub
		Answer p = null;
		try {
			p = new Answer(sql.getString("rengine"), sql.getString("rtitle"), sql.getString("rfulltext"), sql.getString("rreference"));
			p.score(sql.getString("rengine").toUpperCase()+"_RANK", (double)sql.getInt("rrank"));
			p.score(sql.getString("rengine").toUpperCase()+"_SCORE", sql.getDouble("rscore"));			
			p.score("CORRECT", sql.getBoolean("correct")==false? 0.0:1.0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
}
