package unfinished;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uncc2014watsonsim.Database;

public class PhraseTokens {
	static Map<String, Integer> dictionary = new HashMap<>();
	
	public static void main(String[] args) throws SQLException {
		Database db = new Database();
		ResultSet sql = db.prep("SELECT text FROM content LIMIT 5000;").executeQuery();
		
		while (sql.next()) {
			index(sql.getString("text"));
			if (sql.getRow() % 100 == 0) System.err.print(".");
			if (sql.getRow() % 5000 == 0) System.err.println();
		}
		
		Database dict_db = new Database();
		PreparedStatement outstream = dict_db.prep("INSERT OR REPLACE INTO dictionary(word, count) VALUES (?, ?);");
		for (Entry<String,Integer> entry: dictionary.entrySet()) {
			outstream.setString(1, entry.getKey());
			outstream.setInt(2, entry.getValue());
			outstream.addBatch();
		}
		outstream.executeBatch();
		//System.out.println(dictionary);
	}
	
	public static void index(String text) {
		String[] tokens = text.split("\\W+");
		String phrase = "";
		for (String token : tokens) {
			phrase += " " + token;
			Integer count = dictionary.get(phrase);
			if (count == null) {
				dictionary.put(phrase, 1);
				phrase = "";
			} else {
				dictionary.put(phrase, count+1);
			}
		}
	}

}
