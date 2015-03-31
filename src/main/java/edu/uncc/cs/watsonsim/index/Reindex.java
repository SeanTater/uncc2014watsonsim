/*
 * Lucene library can do much more that what is being done in this program. 
 * Look at the lucene documentation to get more juice out of the library
 */
package edu.uncc.cs.watsonsim.index;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.uncc.cs.watsonsim.Database;
import edu.uncc.cs.watsonsim.Passage;

/**
 *
 * @author Phani Rahul
 * @author Modifications: Jonathan Shuman
 * @purpose To index a directory of files to Lucene. 
 * 		The modification is to add support for indexing a directory of files and an exception
 * 				catch for one of the files in the short wikipedia which seems to have a formatting issue 
 * 				(and any others in the future which might)
 */
public class Reindex {
    /**
     * the input file which has to be indexed. This is a database made from TRECtext's
     */
    final Database db = new Database();
	final List<Segment> indexers;
	
	public Reindex() throws IOException {
    	// Read the configuration
		Properties props = null;
		for (String prefix : new String[]{"data/", ""}) {
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
					+ "data/ or "
					+ System.getProperty("user.dir") // CWD
					+ " You can create one by making a copy of"
					+ " config.properties.sample. Check the README as well.");
		}
		
		// Now make properties immutable, and call it a Map<String, String>
		Map<Object, Object> m = new HashMap<>();
		m.putAll(props);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, String> config = Collections.unmodifiableMap((Map) m);
		
		indexers = Arrays.asList(
				new Lucene(Paths.get(config.get("lucene_index"))),
				new Indri(config.get("indri_index"))
				//new Bigrams(),
				//new Edges()
				);
		
	}

    /**
     * Index collected datasources using Lucene and Indri 
     */
    public static void main(String[] args) throws SQLException, IOException {
    	new Reindex().run();
    }
    
    public void run() throws SQLException {
    	try {
	        indexAll("SELECT title, text, reference FROM sources;");
	        
	        indexAll("SELECT "
	        			+ "title, "
	        			+ "string_agg(text, ' ') as text,"
	        			+ "min(reference) as reference "
	    			+ "FROM sources "
					+ "GROUP BY title;");
		} finally {
			// Even if the process is interrupted, save the indices!
			indexers.forEach(i -> { 
				try {
					i.close();
				} catch (Exception e) {
					e.printStackTrace();
				}	
			});
		}
        System.out.println("Done indexing.");
    }
    
    private void indexAll(String query) throws SQLException {
    	ResultSet rs = db.prep(query).executeQuery();
    	
    	while (rs.next()) {
    		Passage row = new Passage("none", rs.getString(1), rs.getString(2), rs.getString(3));
    		for (Segment i : indexers) {
    			i.accept(row);
    			System.out.println("Indexed: " + row.title);
    		}
    	}
    }
}
