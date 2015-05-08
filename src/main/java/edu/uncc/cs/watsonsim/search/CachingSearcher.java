package edu.uncc.cs.watsonsim.search;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import edu.uncc.cs.watsonsim.Database;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Score;

public class CachingSearcher extends Searcher {
	private Random gen = new Random();
	private Searcher searcher;
	String engine_name;

	public CachingSearcher(Environment env, Searcher searcher, String engine_name) {
		super(env);
		this.searcher = searcher;
		this.engine_name = engine_name;
	}
	
	private void getScores(Passage p, long passage_id) throws SQLException {
		PreparedStatement cache = db.prep(
				"SELECT name, value FROM cache_scores WHERE (passage_id = ?);");
		cache.setLong(1, passage_id);
		ResultSet sql = cache.executeQuery();
		while (sql.next()) {
			// Only the ENGINE_NAME* scores were saved, so no filtering needs to happen here
			p.score(sql.getString("name"), sql.getDouble("value"));
		}
	}
	
	private void setScores(Passage p, long passage_id) throws SQLException {
		PreparedStatement cache = db.prep(
				"INSERT INTO cache_scores(passage_id, name, value) VALUES (?, ?, ?);");
		cache.setLong(1, passage_id);
		for (Entry<String, Double> score: Score.asMap(p.scores).entrySet()) {
			// Only save scores that prefix this engine, to avoid breaking other scores
			if (score.getKey().toLowerCase().startsWith(this.engine_name)) {
				cache.setString(2, score.getKey());		
				cache.setDouble(3, score.getValue());
				cache.addBatch();
			}
		}
		cache.executeBatch();
	}
	
	public List<Passage> query(String query) {
		List<Passage> results = new ArrayList<>();
		try {
			// Part 1: Find any existing cache, use it if possible
			PreparedStatement cache = db.prep(
					"SELECT id, title, fulltext, reference FROM cache WHERE (query = ? AND engine = ?);");
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
				getScores(p, sql.getLong("id"));
			}
		} catch (SQLException e) {
			// If retrieving fails, that is OK. We will simply revert to the searcher.
			System.out.println("Failed to retrieve cache. (DB missing?) Reverting to searcher.");
			e.printStackTrace();
		}
		
		if (results.isEmpty()) {
			// If the SQL search didn't return anything, then run the Searcher.
			List<Passage> query_results = searcher.query(query);
			try {
				db.prep("BEGIN;").execute();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			PreparedStatement set_cache = db.prep(
					"INSERT INTO cache (id, query, engine, title, fulltext, reference) VALUES (?,?,?,?,?,?);");
			for (Passage p : query_results) {
				// Add every Answer to the cache
				results.add(p);
				long id = gen.nextLong();
				try {
					set_cache.setLong(1, id);
					set_cache.setString(2, query);
					set_cache.setString(3, p.engine_name);
					set_cache.setString(4, p.title);
					set_cache.setString(5, p.text);
					set_cache.setString(6, p.reference);
					set_cache.addBatch();
					setScores(p, id);
				} catch (SQLException e) {
					System.out.println("Failed to add cache entry. (DB missing?) Ignoring.");
					e.printStackTrace();
				}
			}
			try {
				set_cache.executeBatch();
				db.prep("COMMIT;").execute();
			} catch (SQLException e) {
				System.out.println("Failed to add cache entry. (DB missing?) Ignoring.");
				e.printStackTrace();
			} finally {
				try {
					db.prep("ROLLBACK;").execute();
				} catch (SQLException e) {}
			}
		}
		return results;
	}
}
