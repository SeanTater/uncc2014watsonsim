package edu.uncc.cs.watsonsim.search;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Function;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.uncc.cs.watsonsim.Database;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.Score;

public class CachingSearcher extends Searcher {
	private final Random gen = new Random();
	private final Searcher searcher;
	private final String engine_name;
	private final Gson gson = new GsonBuilder()
		.registerTypeHierarchyAdapter(
				Phrase.class, new Phrase.Deserializer()).create();

	public CachingSearcher(Environment env, Searcher searcher, String engine_name) {
		super(env);
		this.searcher = searcher;
		this.engine_name = engine_name;
	}
	
	/**
	 * Evaluate a function with a long term persistent cache. It's slower and
	 * more espensive than memcached but it is meant for very expensive
	 * functions like searching Bing.
	 * 
	 * @param key    The unique key used to find the cache entry
	 * @param func   The function we are memoizing
	 * @param dump   Deserialize func's output
	 * @param load   Serialize func's output
	 * @return       Output of func(key)
	 */
	public <X> X computeIfAbsent(String key,
			Function<String, X> func,
			Type clazz) {
		try {
			// Check cache
			PreparedStatement general_cache_check = db.prep(
					"SELECT value, created_on FROM kv_cache "
					+ "WHERE (key=?);");
			general_cache_check.setString(1, key);
			ResultSet result = general_cache_check.executeQuery();
			if (result.next()) {
				// Load cache
				return new Gson().fromJson(result.getString(1), clazz);
			} else {
				// Fill cache
				PreparedStatement set_cache = db.prep(
						"INSERT INTO kv_cache (key, value) VALUES (?,?);");
				X value = func.apply(key);
				set_cache.setString(1, key);
				set_cache.setString(2, new Gson().toJson(value));
				set_cache.executeUpdate();
				return value;
			}
		} catch (SQLException e) {
			// Oh no! Back to just evaluating.
			e.printStackTrace();
			return func.apply(key);
		}
	}
	
	public List<Passage> query(String query) {
		return computeIfAbsent(
				"search:" + engine_name +":"+ query,
				k -> searcher.query(query),
				new TypeToken<List<Passage>>(){}.getType()
				);
	}
}
