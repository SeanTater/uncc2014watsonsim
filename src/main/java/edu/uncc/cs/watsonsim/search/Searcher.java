package edu.uncc.cs.watsonsim.search;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.uncc.cs.watsonsim.Database;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Question;

/*
 * This interface might change; Please be ready to accommodate the changes.
 * This interface should be implemented by local search engines like 
 * Indri and Lucene, when querying them. Basically, it retrieves the basic data
 * from the queried result set.
 */

/**
 *
 * @author Phani Rahul
 */
public abstract class Searcher {
	protected final Database db;
	protected final Environment env;
	public Searcher(Environment env) {
		this.env = env;
		db = env.db;
	}

    /**
     * Runs the <i>query</i>, populating a list of ResultSets
     * 
     * For each ResultSet:
     * <p>1: Gets the score of the document from the search result. For different
     * search engines, the scoring methods are different. If the document is 
     * in TREC text format or TREC web format, every {@literal<DOC></DOC>} should be
     * considered as a separate document.
     * <p>2: Gets the title of the document.
     * <p>3: Gets the full text of the document.
     *
     * @param query
     * @throws Exception 
     */
    
	public List<Passage> query(String query) {
		return new ArrayList<>();
	};
	public List<Passage> query(Question q) {
		return query(q.text);
	};

    /**
     * How many results should Lucene and Indri return?
     * This is also how many passages the scorers should expect.
     */

    public final static int MAX_RESULTS = 10;
    
    
    /** Fill in the missing titles and full texts from Answers using the
     * sources from the relational database.
     *  
     * This is a no-op if the sources database is missing.
     */
    List<Passage> fillFromSources(List<Passage> passages) {
    	List<Passage> results = new ArrayList<>();
    	PreparedStatement fetcher = db.prep("SELECT title, text FROM sources WHERE reference=? or id=?;");

    	for (Passage p: passages) {
    		ResultSet doc_row;
    		try {
				fetcher.setString(1, p.reference);
				fetcher.setString(2, p.reference);
				doc_row = fetcher.executeQuery();
				if (doc_row.next()
						&& doc_row.getString("title") != null
						&& doc_row.getString("text") != null) {
					Passage np = new Passage(
							p.engine_name,
							doc_row.getString("title"),
							doc_row.getString("text"),
							p.reference
							);
					np.scores = p.scores.clone();
                    results.add(np);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to execute sources search. "
						+ "Missing document? docno:"+p.reference);
			}
    	}
    	return results;
    }
}
