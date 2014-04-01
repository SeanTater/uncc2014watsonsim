package uncc2014watsonsim.search;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
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
    
	public abstract List<Passage> runQuery(String query) throws Exception;

    /**
     * How many results should Lucene and Indri return?
     */
    public final int MAX_RESULTS = 50;
    
    public List<Answer> runFitbQuery(Question question) throws Exception {
    	return new ArrayList<Answer>(0);
    }
    
    
    /** Fill in the missing titles and full texts from Answers using sources.db
     * This is a no-op if the sources database is missing.
     */
    List<Passage> fillFromSources(List<Passage> passages) {
    	if (!db.sanityCheck()) {
    		return passages;
    	} else {
	    	List<Passage> results = new ArrayList<Passage>();
	    	PreparedStatement fetcher = db.prep("select title, text from documents where docno=?;");
	
	    	for (Passage p: passages) {
	    		ResultSet doc_row;
	    		try {
					fetcher.setString(1, p.reference);
					doc_row = fetcher.executeQuery();
		    		p.title = doc_row.getString("title");
		    		if (p.title == null) p.title = "";
		    		p.text = doc_row.getString("text");
		    		if (p.text == null) p.text = "";
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException("Failed to execute sources full text search. "
							+ "Missing document? docno:"+p.reference);
				}
	    		results.add(p);
	    	}
	    	return results;
	    }
    }
}
