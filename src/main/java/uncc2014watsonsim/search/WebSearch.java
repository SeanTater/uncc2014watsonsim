package uncc2014watsonsim.search;

/*
 * This interface might change; Please be ready to accomodate the changes.
 * This interface should be implemented by web search engines like 
 * Google, Yahoo and Bing when querying them. Basically, it retreives the basic data 
 * from the queried result set.
 */

/**
 *
 * @author Phani Rahul
 */
public interface WebSearch {  
    
    /**
     * simply runs the <i>query</i>
     *
     * @param query
     */
    public void runQuery(String query);

    /**
     * Given a document index(or rank), this methods returns the title of the
     * document. 
     * @param index
     */
    public String getTitle(int index);

    /**
     * Given a document index(or rank), this methods returns the link to the 
     * entire document. 
     * @param index
     */
    public String getDocumentPointer(int index);
    
    /**
     * 
     */
    public final int MAX_RESULTS = 30;
    
    /**
     * This method should return the maximum number of indices available in the 
     * result set. If this value is more than <i>maxResults</i>, then just return 
     * <i>maxResults</i>.
     */
    public int getResultCount();
    
}
