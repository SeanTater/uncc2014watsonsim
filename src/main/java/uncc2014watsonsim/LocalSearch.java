package uncc2014watsonsim;

import java.util.List;

/*
 * This interface might change; Please be ready to accomodate the changes.
 * This interface should be implemented by local search engines like 
 * Indri and Lucene, when querying them. Basically, it retreives the basic data 
 * from the queried result set.
 */

/**
 *
 * @author Phani Rahul
 */
public interface LocalSearch {

    /**
     * Runs the <i>query</i>, populating a list of ResultSets
     * 
     * For each ResultSet:
     * 1: Gets the score of the document from the search result. For different
     * search engines, the scoring methods are different. If the document is 
     * in TREC text format or TREC web format, every <DOC></DOC> should be
     * considered as a separate document.
     * 2: Gets the title of the document.
     * 3: Gets the full text of the document.
     *
     * @param query
     * @throws Exception 
     */
    public List<ResultSet> runQuery(String query) throws Exception;

    /**
     * How many results should Lucene and Indri return?
     */
    public final int MAX_RESULTS = 10;
}
