package edu.uncc.cs.watsonsim.index;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import edu.stanford.nlp.util.Triple;
import edu.uncc.cs.watsonsim.Configuration;
import edu.uncc.cs.watsonsim.Database;
import edu.uncc.cs.watsonsim.Passage;

/**
 *
 * @author Phani Rahul
 * @author Modifications: Jonathan Shuman
 * @author Later rewrite by Sean Gallagher
 * @purpose Index a database of plain-text sources using pluggable modules
 */
public class Reindex {
    /**
     * the input file which has to be indexed. This is a database made from TRECtext's
     */
    private final Database db;
	final List<Segment> indexers;
	private final Configuration conf = new Configuration();
	
	public Reindex() throws IOException {
		db = new Database(conf);
		indexers = Arrays.asList(
				//new Lucene(Paths.get(conf.getConfOrDie("lucene_index")))
				//new Indri(conf.getConfOrDie("indri_index")),
				new Bigrams()
				//new Edges(db)
				);
		
	}

    /**
     * Index collected datasources using Lucene and Indri 
     */
    public static void main(String[] args) throws SQLException, IOException {
    	new Reindex().run();
    }
    
    public void run() {
    	try {
	        //indexAll("SELECT title, text, reference FROM sources;");
	        if (db.backend().startsWith("SQLite")) {
	        	// I highly recommend SQLite. The indexing will run much faster
	        	// because it has a more efficient query plan for this
		        indexAll("SELECT "
		        			+ "title, "
		        			+ "group_concat(text, ' ') as text,"
		        			+ "min(reference) as reference "
		    			+ "FROM sources "
						+ "GROUP BY title;");
	        } else {
	        	indexAll("SELECT "
			    			+ "title, "
			    			+ "string_agg(text, ' ') as text,"
			    			+ "min(reference) as reference "
						+ "FROM sources "
						+ "GROUP BY title;");
	        	
	        }
		} catch (IllegalArgumentException | SQLException e) {
			e.printStackTrace();
		} finally {
			// Even if the process is interrupted, save the indices!
			indexers.forEach(i -> { 
				try {
					i.close();
				} catch (Exception e) {
					e.printStackTrace();
				}	
			});
			db.commit();
		}

        
        // SemanticVectors Post-processing
    	/*try {
			BuildIndex.main(new String[]{
					"-luceneindexpath", conf.getConfOrDie("lucene_index"),
					"-docidfield", "docno",
					"-docindexing", "incremental",
					"-contentsfields", "text"});
		} catch (IllegalArgumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	
        System.out.println("Done indexing.");
    }
    
    private void indexAll(String query) throws SQLException {
    	PreparedStatement statements = db.prep(query);
    	statements.setFetchSize(10000);
    	ResultSet rs = statements.executeQuery();
    	AtomicInteger c = new AtomicInteger();
    	Stream.generate(() -> {
    		List<Triple<String,String,String>> block = new ArrayList<>(300);
    		try {
    			synchronized(rs) {
    				while (block.size() < 300 && !rs.isAfterLast() && rs.next()) {
    					// The usual case, another result
    					block.add(Triple.makeTriple(
    							rs.getString(1), rs.getString(2), rs.getString(3)));
    				}
    			}
			} catch (SQLException e) {
				// Sometimes the resultset closes while we use it.
				// What can we do about it?
				e.printStackTrace();
			}
    		return block;
    	}).parallel().flatMap((block) -> {
    			if (!block.isEmpty()) {
    				for (Triple<String,String,String> row : block) {
    					Passage pass = new Passage(
    							"none", row.first, row.second, row.third);
    					
			    		for (Segment i : indexers) {
			    			i.accept(pass);
			    		}
    				}
		    		int count = c.addAndGet(block.size());
	    			System.out.println("Indexed " + count);
    			}
    			// It's looking for the first non-empty stream
    			if (block.isEmpty()) return Stream.of("done");
    			else return Stream.empty();
	    	}
    	).findFirst();
    }
}
