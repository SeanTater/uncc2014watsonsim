package edu.uncc.cs.watsonsim;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.QueryBuilder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * The NLP toolkit needs several shared resources, like text search indices
 * and database connections. Some can be shared between threads to save
 * memory, others should be independent. Also, configuration parameters
 * should all be entered in one place to keep it consistent between threads.
 * 
 * So start an global environment by constructing it, and start a new thread
 * by using the newThread() method of the environment.
 * 
 * The public fields of the Environment are intended for internal use by all
 * the NLP packages. Exercise great care before mutating anything. 
 * 
 * @author Sean Gallagher
 */
public class Environment extends Configuration {
	public final Database db;
	public final Dataset rdf;
	public final IndexSearcher lucene;
	private final QueryBuilder lucene_query_builder = new QueryBuilder(new StandardAnalyzer());
	private static final Cache<String, ScoreDoc[]> recent_lucene_searches =
            CacheBuilder.newBuilder()
		    	.concurrencyLevel(50)
		    	.softValues()
		    	.maximumSize(1000)
		    	.build();
	public final Log log = new Log(getClass(), System.out::println);
	
	/**
	 * Create a (possibly) shared NLP environment. The given data directory
	 * must be created (usually from a downloaded zipfile, check the README).
	 * Expect many open files and many reads. Network filesystems are known to
	 * perform poorly as data directories. Strive to use a local directory if
	 * possible, or at least the Lucene indices otherwise.
	 * 
	 * config.properties can be either in the data directory or the working
	 * directory. This is to allow sharing (read-only) indices while still
	 * allowing separate development configurations.  
	 */
	public Environment() {
		
		// Now do some per-thread setup
		db = new Database(this);
		rdf = TDBFactory.assembleDataset(
				pathMustExist("rdf/jena-lucene.ttl"));
		
		// Lucene indexes have huge overhead so avoid re-instantiating by putting them in the Environment
		IndexReader reader;
		try {
			reader = DirectoryReader.open(new MMapDirectory(Paths.get(getConfOrDie("lucene_index"))));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("The candidate-answer Lucene index failed to open.");
		}
		lucene = new IndexSearcher(reader);
		//lucene.setSimilarity(new BM25Similarity());
	}
	
	/**
	 * Run a vanilla boolean Lucene query
	 * @param query
	 * @param count
	 * @return
	 */
	public ScoreDoc[] simpleLuceneQuery(String query, int count) {
		if (query.length() < 3) return new ScoreDoc[0];
		try {
			return recent_lucene_searches.get(query, () -> forcedSimpleLuceneQuery(query, count));
		} catch (ExecutionException e) {
			e.printStackTrace();
			return new ScoreDoc[0];
		}
	}
	
	/**
	 * Run a vanilla boolean Lucene query
	 * @param query		Terms to query lucene with, using SHOULD (a kind of OR)
	 * @param count		The number of results to return
	 * @return			An array of ScoreDocs
	 * @throws IOException
	 *  We  
	 */
	private ScoreDoc[] forcedSimpleLuceneQuery(String query, int count) throws IOException {
		Query bquery = lucene_query_builder.createBooleanQuery("text", query, Occur.SHOULD);
		if (bquery != null) {
			return lucene.search(bquery, count).scoreDocs;
		} else {
			return new ScoreDoc[0];
		}
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
	public synchronized <X> X computeIfAbsent(String key,
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
				result.close();
				general_cache_check.close();
				// Fill cache
				PreparedStatement set_cache = db.prep(
						"INSERT INTO kv_cache (key, value) VALUES (?,?);");
				X value = func.apply(key);
				set_cache.setString(1, key);
				set_cache.setString(2, new Gson().toJson(value));
				set_cache.executeUpdate();
				set_cache.close();
				return value;
			}
		} catch (SQLException e) {
			// Oh no! Back to just evaluating.
			e.printStackTrace();
			return func.apply(key);
		}
	}
}
