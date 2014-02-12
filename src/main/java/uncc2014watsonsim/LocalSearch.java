package watsondemo;

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
     * Sets the path to the directory of the index repository
     * @param indexPath 
     */
    public void setIndex(String indexPath);

    /**
     * simply runs the <i>query</i>
     *
     * @param query
     */
    public void runQuery(String query);

    /**
     * this method gets the score of the document from the search result, given
     * the index(or rank) of that document. For different search engines, the
     * scoring methods are different. If the document is in TREC text format or
     * TREC web format, every <DOC></DOC> should be considered as a separate
     * document.
     *
     * @param index
     */
    public double getScore(int index);

    /**
     * Given a document index(or rank), this methods returns the title of the
     * document. If the document is in TREC text format or TREC web format,
     * every <DOC></DOC> should be considered as a separate document.
     *
     * @param index
     */
    public String getTitle(int index);

    /**
     * Given a document index(or rank), this methods returns the entire
     * document. If the document is in TREC text format or TREC web format,
     * every <DOC></DOC>
     * should be considered as a separate document.
     *
     * @param index
     */
    public String getDocument(int index);
    /**
     *
     */
    public final int MAX_RESULTS = 10;

    /**
     * This method should return the maximum number of indices available in the
     * result set. If this value is more than <i>maxResults</i>, then just
     * return
     * <i>maxResults</i>.
     */
    public int getResultCount();
}
