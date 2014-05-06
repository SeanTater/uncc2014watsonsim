package uncc2014watsonsim.anagramPipeline;

import java.util.List;
import java.sql.*;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import uncc2014watsonsim.anagram.AnagramQuestion;
import uncc2014watsonsim.anagram.CandidateAnswer;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;

public class AnagramWriteDBEngine extends JCasAnnotator_ImplBase {

	private Statement makeSqlStatement() {
		String jdbc_driver = "com.mysql.jdbc.Driver";
		String db_uri ="jdbc:mysql:data";
		String username = "admin";
		String pass = "root";
		Connection conn = null;
		Statement state = null;
		
		try {
			Class.forName(jdbc_driver);
			conn = DriverManager.getConnection(db_uri, username, pass);
			state = conn.createStatement();
			return state;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (state != null)
					state.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
		
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		Statement stmt = null;
		String dbName = "anagramdb";
		/**
		 * Get Question, Candidates, and Candidate's supporting passages
		 */
		try {
			AnagramQuestion question = UimaTools.getSingleton(aJCas, AnagramQuestion.type);
			List<CandidateAnswer> CanAns = UimaTools.getFSList(question.getCandidateAnswers());
		}
		catch (UimaToolsException e) {
			e.printStackTrace();
		}
		
		/**
		 * Make database info
		 */
		try {
			stmt = makeSqlStatement();
			String deleteDBQuery = "Delete database if exists " + dbName;
			String createDBQuery = "create database if not exists " + dbName;
			stmt.execute(deleteDBQuery);
			stmt.execute(createDBQuery);
			
			/**
			 * Create Table for info
			 */
			String createQuestionsTable = "Create table if not exists " + dbName + ".Questions(" +
								 "_id int not null auto increment, " +
								 "anagram_text VarChar(255) not null, " + 
								 "question_text VarChar(255) not null, " +
								 "PRIMARY KEY (_ID));";
			String createCandidateAnswers = "Create table if not exists " + dbName + ".CandidateAnswers(" +
								 			"_id int NOT NULL auto increment, " +
								 			"question_id int NOT NULL, " +
								 			"answer VarChar(255) NOT NULL, ";
								 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (stmt != null)
					stmt.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
