package uncc2014watsonsim.search;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Database;
import uncc2014watsonsim.PassageRef;

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
	
	static final Database db = new Database();

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
    
	public abstract List<Passage> query(String query);

    /**
     * How many results should Lucene and Indri return?
     * This is also how many passages the scorers should expect.
     */

    public final static int MAX_RESULTS = 20;
    
    
    /**
     * Dereference a PassageRef.
     * 
     * TODO: It only dereferences DOCNO's not but it could easily also do web
     * based dereferencing too.
     */
    List<Passage> deref(List<PassageRef> refs) {
    	List<Passage> results = new ArrayList<Passage>();
    	PreparedStatement fetcher = db.parPrep("SELECT title, text FROM meta INNER JOIN content ON meta.id=content.id WHERE reference=?;");

    	for (PassageRef ref: refs) {
    		ResultSet doc_row;
    		try {
				fetcher.setString(1, ref.reference);
				doc_row = fetcher.executeQuery();
				
				String title = Optional.ofNullable(doc_row.getString("title")).orElse("");
				String text = Optional.ofNullable(doc_row.getString("text")).orElse("");
				if (doc_row.next()) {
					results.add(new Passage(
							ref.engine_name,
							title,
							text,
							ref.reference
							));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to execute sources search. "
						+ "Missing document? docno:"+ref.reference);
			}
    	}
    	return results;
    }
}
