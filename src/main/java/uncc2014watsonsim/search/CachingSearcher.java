package uncc2014watsonsim.search;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.SQLiteDB;
import uncc2014watsonsim.Score;

//TODO: Enum scoring breaks this class.
public class CachingSearcher extends Searcher {
	Searcher[] searchers;
	SQLiteDB db = new SQLiteDB("questions");

	public CachingSearcher(Searcher[] searchers) {
		this.searchers = searchers;
	}

	@Override
	public List<Passage> runQuery(String query) throws Exception {
		List<Passage> results = new ArrayList<Passage>();
		
		// Part 1: Find any existing cache, use it if possible
		PreparedStatement cache = db.prep(
				"select engine, title, fulltext, reference, rank, score from cache where (query = ?);");
		cache.setString(1, query);
		ResultSet sql = cache.executeQuery();

		while (sql.next()) {
			// Load cache into Answers
			Passage p = new Passage(
					sql.getString("engine"),
					sql.getString("title"),
					sql.getString("fulltext"),
					sql.getString("reference"));
			try {
				p.score(Score.valueOf(sql.getString("engine").toUpperCase() + "_RANK"), sql.getDouble("rank"));
			} catch (NullPointerException e) {}
			try {
				p.score(Score.valueOf(sql.getString("engine").toUpperCase() + "_SCORE"), sql.getDouble("score"));
			} catch (NullPointerException e) {}
		}
		if (results.isEmpty()) {
			// If the SQL search didn't return anything, then run the Searcher.
			PreparedStatement set_cache = db.prep(
					"insert into cache (query, engine, title, fulltext, reference, rank, score) values (?,?,?,?,?,?,?);");
			for (Searcher s: searchers) {
				for (Passage p: s.runQuery(query)) {
					// Add every Answer to the cache
					results.add(p);

					String engine = p.engine_name;
					set_cache.setString(1, query);
					set_cache.setString(2, engine);
					set_cache.setString(3, p.title);
					set_cache.setString(4, p.text);
					set_cache.setString(5, p.reference);
					Double rank = p.scores.get(engine+"_rank");
					if (rank == null) rank = 0.0;
					set_cache.setDouble(6, rank);
					Double score = p.scores.get(engine+"_score");
					if (score == null) score = 0.0;
					set_cache.setDouble(7, score);
					set_cache.addBatch();
				}
			}
			set_cache.executeBatch();
		}
		return results;
	}
}
