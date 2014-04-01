package uncc2014watsonsim.search;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Document;
import uncc2014watsonsim.SQLiteDB;

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
public abstract class Searcher {
	
	static final SQLiteDB db = new SQLiteDB("sources");

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
    
	public abstract List<Answer> runQuery(String query) throws Exception;

    /**
     * How many results should Lucene and Indri return?
     */

    public final int MAX_RESULTS = 10;

    
    
    /** Fill in the missing titles and full texts from Answers using sources.db
     * This is a no-op if the sources database is missing.
     */
    List<Answer> fillFromSources(List<Answer> answers) {
    	if (!db.sanityCheck()) {
    		return answers;
    	} else {
	    	List<Answer> results = new ArrayList<Answer>();
	    	PreparedStatement fetcher = db.prep("select title, text from documents where docno=?;");
	
	    	for (Answer a: answers) {
	    		Document d = a.docs.get(0);
	    		ResultSet doc_row;
	    		try {
					fetcher.setString(1, d.reference);
					doc_row = fetcher.executeQuery();
		    		d.title = doc_row.getString("title");
		    		if (d.title == null) d.title = "";
		    		d.text = doc_row.getString("text");
		    		if (d.text == null) d.text = "";
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException("Failed to execute sources full text search. "
							+ "Missing document? docno:"+d.reference);
				}
	    		results.add(a);
	    	}
	    	return results;
	    }
    }
}
