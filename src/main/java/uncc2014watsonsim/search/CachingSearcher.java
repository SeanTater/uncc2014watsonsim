package uncc2014watsonsim.search;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import uncc2014watsonsim.Passage;
import uncc2014watsonsim.SQLiteDB;

public class CachingSearcher extends Searcher {
	Searcher searcher;
	SQLiteDB db = new SQLiteDB("questions");
	String engine_name;
	

	public CachingSearcher(Searcher searcher, String engine_name) {
		this.searcher = searcher;
		this.engine_name = engine_name;
	}
	
	private void getScores(Passage p, long passage_id) throws SQLException {
		PreparedStatement cache = db.prep(
				"select name, value from cache_scores where (passage_id = ?);");
		cache.setLong(1, passage_id);
		ResultSet sql = cache.executeQuery();
		while (sql.next()) {
			p.score(sql.getString("name"), sql.getDouble("value"));
		}
	}
	
	private void setScores(Passage p, long passage_id) throws SQLException {
		PreparedStatement cache = db.prep(
				"insert into cache_scores(passage_id, name, value) values (?, ?, ?);");
		cache.setLong(1, passage_id);
		for (Entry<String, Double> score: p.scores.entrySet()) {
			cache.setString(2, score.getKey());		
			cache.setDouble(3, score.getValue());
			cache.addBatch();
		}
		cache.executeBatch();
	}
	
	public List<Passage> query(String query) {
		List<Passage> results = new ArrayList<Passage>();
		
		try {
			// Part 1: Find any existing cache, use it if possible
			PreparedStatement cache = db.prep(
					"select id, title, fulltext, reference from cache where (query = ? and engine = ?);");
			cache.setString(1, query);
			cache.setString(2, engine_name);
			ResultSet sql = cache.executeQuery();

			while (sql.next()) {
				// Load cache into Answers
				Passage p = new Passage(
						engine_name,
						sql.getString("title"),
						sql.getString("fulltext"),
						sql.getString("reference"));
				results.add(p);
				getScores(p, sql.getInt("id"));
			}
		} catch (SQLException e) {
			// If retrieving fails, that is OK. We will simply revert to the searcher.
			System.out.println("Failed to retrieve cache from SQLite cache. (DB missing?) Reverting to searcher.");
			e.printStackTrace();
		}
		
		if (results.isEmpty()) {
			// If the SQL search didn't return anything, then run the Searcher.
			PreparedStatement set_cache = db.prep(
					"insert into cache (id, query, engine, title, fulltext, reference, rank, score) values (?,?,?,?,?,?,?,?);");
			for (Passage p : searcher.query(query)) {
				// Add every Answer to the cache
				results.add(p);
				long id = query.hashCode() ^ ((p.engine_name.hashCode() ^ p.reference.hashCode()) << 32);
				try {
					set_cache.setLong(1, id);
					set_cache.setString(2, query);
					set_cache.setString(3, p.engine_name);
					set_cache.setString(4, p.title);
					set_cache.setString(5, p.getText());
					set_cache.setString(6, p.reference);
					set_cache.addBatch();
					setScores(p, id);
				} catch (SQLException e) {
					System.out.println("Failed to set cache to SQLite. (DB missing?) Ignoring.");
					e.printStackTrace();
				}
			}
			try {
				set_cache.executeBatch();
			} catch (SQLException e) {
				System.out.println("Failed to set cache to SQLite. (DB missing?) Ignoring.");
				e.printStackTrace();
			}
		}
		return results;
	}
}
