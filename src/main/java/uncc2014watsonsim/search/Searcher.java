package uncc2014watsonsim.search;

import java.util.List;

import uncc2014watsonsim.Database;
import uncc2014watsonsim.PassageRef;
import uncc2014watsonsim.scorers.Scored;

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
    
	public abstract List<Scored<PassageRef>> query(String query);

    /**
     * How many results should Lucene and Indri return?
     * This is also how many passages the scorers should expect.
     */

    public final static int MAX_RESULTS = 20;
}
