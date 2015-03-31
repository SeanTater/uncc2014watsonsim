package edu.uncc.cs.watsonsim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.QueryBuilder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
public class Environment {
	private final String data_path;
	public final Map<String, String> config;
	public final Database db = new Database();
	public final Dataset rdf;
	public final IndexSearcher lucene;
	private final QueryBuilder lucene_query_builder = new QueryBuilder(new StandardAnalyzer());
	private static final Cache<String, ScoreDoc[]> recent_lucene_searches =
            CacheBuilder.newBuilder()
		    	.concurrencyLevel(50)
		    	.softValues()
		    	.maximumSize(1000)
		    	.build();
	
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
	 * 
	 * @param data_path The path to the data directory as a String.
	 * @throws IOException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" }) // From Properties -> Map
	public Environment(String data_path) throws IOException {
		/*
		 * The excess verbosity here is because it is probably the entry point
		 * for first-time users of the program.
		 */
		
		// Check the data path
		if (data_path == null
				|| data_path.isEmpty()) {
			throw new IOException("Cannot use an empty or null directory. "
					+ "Pick \"data/\" if you are unsure.");
		}
		File f = new File(data_path);
		if (!(f.exists() && f.isDirectory())) {
			throw new IOException(data_path + " should be a directory.");
		}
		
		this.data_path = data_path.endsWith(File.separator) ?
				data_path : (data_path + File.separator);
		
		// Read the configuration
		Properties props = null;
		for (String prefix : new String[]{this.data_path, ""}) {
			try (Reader s = new InputStreamReader(
					new FileInputStream(prefix + "config.properties"), "UTF-8")){
				// Make it, then link it if it works.
				Properties _local_props = new Properties();
				_local_props.load(s);
				props = _local_props;
			} catch (FileNotFoundException e) {
				// This is only an error if none are found.
			}
		}
		// If it didn't link, all the reads failed.
		if (props == null) {
			throw new IOException("Failed to read config.properties in either "
					+ this.data_path
					+ " or "
					+ System.getProperty("user.dir") // CWD
					+ " You can create one by making a copy of"
					+ " config.properties.sample. Check the README as well.");
		}
		// Now make properties immutable.
		Map<Object, Object> m = new HashMap<>();
		m.putAll(props);
		this.config = Collections.unmodifiableMap((Map) m);
		
		// Now do some per-thread setup
		rdf = TDBFactory.assembleDataset(
				pathMustExist("rdf/jena-lucene.ttl"));
		
		// Lucene indexes have huge overhead so avoid re-instantiating by putting them in the Environment
		IndexReader reader;
		try {
			reader = DirectoryReader.open(new MMapDirectory(Paths.get(getOrDie("lucene_index"))));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("The candidate-answer Lucene index failed to open.");
		}
		lucene = new IndexSearcher(reader);
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
		Query bquery = lucene_query_builder.createBooleanQuery("text", query);
		if (bquery != null) {
			return lucene.search(bquery, count).scoreDocs;
		} else {
			return new ScoreDoc[0];
		}
	}
	

	/**
	 * Convenience method for getting a setting.
	 * @param config Map from the configuration file (config.properties) 
	 * @param key The key that must exist in the properties
	 * @return The non-null String value, or else throw a RuntimeException.
	 */
	public String getOrDie(String key) {
		String value = config.get(key);
		if (value == null) throw new RuntimeException("Required key (" + key + ") missing from configuration file.");
		return value;
	}
	
	/**
	 * Get the path to a resource, ensuring it exists.
	 * This is mostly to give helpful errors and fail fast if you missed a
	 * step setting up.
	 * @param resource The relative path of the resource without leading /
	 */
	public String pathMustExist(String resource) {
		String path = data_path + File.separator + resource;
		if (!new File(path).exists()) {
			throw new RuntimeException("The data directory is missing the"
					+ " expected resource: " + path);
		}
		return path;
	}
}
