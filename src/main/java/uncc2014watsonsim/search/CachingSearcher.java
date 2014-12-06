package uncc2014watsonsim.search;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import uncc2014watsonsim.Database;
import uncc2014watsonsim.PassageRef;
import uncc2014watsonsim.scorers.Scored;

/**
 * A transparent caching proxy for Searchers using the database.
 * 
 * @author Sean
 */
public class CachingSearcher extends Searcher {
	Searcher searcher;
	String engine_name;
	
	/**
	 * Create a new transparent cache for a searcher, using a tag to identify
	 * this engine from others.
	 * 
	 * @param searcher			The searcher whose results to cache
	 * @param engine_name		The (made up) tag for this searcher
	 */
	
	public CachingSearcher(Searcher searcher, String engine_name) {
		this.searcher = searcher;
		this.engine_name = engine_name;
	}
	
	/**
	 * Read a denormalized, JSON formatted scores object.
	 * 
	 * This includes some unchecked casts. Be sure the database is clean.
	 * @param ref		The target passage reference
	 * @param scores    The annotations
	 * @return  a Scored<PassageRef> with the scores from the JSON object
	 */
	private Scored<PassageRef> getScores(PassageRef ref, String scores) {
		Scored<PassageRef> sref = Scored.mzero(ref);
		JSONObject obj = (JSONObject) JSONValue.parse(scores);
		for (Object name : obj.keySet()) {
			sref.put((String) name, (Double) obj.get(name));
		}
		return sref;
	}
	
	/**
	 * Generate a JSON representation of scores
	 * @param sref	The scored PassageRef
	 * @return		Just the scores, as JSON
	 */
	private String makeScores(Scored<PassageRef> sref) {
		return JSONValue.toJSONString(sref);
	}
	
	public List<Scored<PassageRef>> query(String query) {
		List<Scored<PassageRef>> results = new ArrayList<>();
		Database db = new Database();
		try {
			// Part 1: Find any existing cache, use it if possible
			PreparedStatement cache = db.prep(
					"select id, title, fulltext, reference from cache where (query = ? and engine = ?);");
			cache.setString(1, query);
			cache.setString(2, engine_name);
			ResultSet sql = cache.executeQuery();

			while (sql.next()) {
				// Load cache into Answers
				PassageRef p = new PassageRef(
						engine_name,
						sql.getString("reference"),
						Optional.of(sql.getString("title")),
						Optional.of(sql.getString("fulltext")));
				results.add(getScores(p, sql.getString("scores")));
			}
		} catch (SQLException e) {
			// If retrieving fails, that is OK. We will simply revert to the searcher.
			System.out.println("Failed to retrieve cache. (DB missing?) Reverting to searcher.");
			e.printStackTrace();
		}
		
		if (results.isEmpty()) {
			// If the SQL search didn't return anything, then run the Searcher.
			List<Scored<PassageRef>> query_results = searcher.query(query);
			PreparedStatement set_cache = db.prep(
					"insert into cache (id, query, engine, title, fulltext, reference, scores) values (?,?,?,?,?,?,?);");
			for (Scored<PassageRef> sref : query_results) {
				// Add every Answer to the cache
				try {
					set_cache.setLong(1, 0);
					set_cache.setString(2, query);
					set_cache.setString(3, sref.getTarget().engine_name);
					set_cache.setString(4, sref.getTarget().title.orElse(""));
					set_cache.setString(5, sref.getTarget().text.orElse(""));
					set_cache.setString(6, sref.getTarget().reference);
					set_cache.setString(7, makeScores(sref));
					set_cache.addBatch();
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
