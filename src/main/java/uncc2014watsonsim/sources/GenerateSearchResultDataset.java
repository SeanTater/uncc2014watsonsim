package uncc2014watsonsim.sources;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import uncc2014watsonsim.Question;
import uncc2014watsonsim.QuestionDB;
import uncc2014watsonsim.ResultSet;
import uncc2014watsonsim.search.IndriSearch;
import uncc2014watsonsim.search.LuceneSearch;
import uncc2014watsonsim.search.WebSearchGoogle;

/**
 *
 * @author Phani Rahul
 */
public class GenerateSearchResultDataset {

    /**
     * @param args the command line arguments
     * @throws SQLException 
     */
    public static void main(String[] args) throws SQLException {
        ExecutorService pool = Executors.newFixedThreadPool(8);
    	for (Question q : QuestionDB.fetch_without_results(0,85)) {
    		pool.execute(new SingleTrainingResult(q));
    		//new SingleTrainingResult(q).run();
    	}
        pool.shutdown();
        try {
            pool.awaitTermination(2, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(GenerateSearchResultDataset.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Finished.");
    }
}

class SingleTrainingResult extends Thread {
	Question q;
	
	public SingleTrainingResult(Question q) {
		this.q = q;
	}
	
	public void run() {
		try {
			List<ResultSet> uncollated_results = new ArrayList<ResultSet>(); 
			uncollated_results.addAll(IndriSearch.runQuery(q.text));
			uncollated_results.addAll(LuceneSearch.runQuery(q.text));
			uncollated_results.addAll(WebSearchGoogle.runQuery(q.text));
			QuestionDB.replace_cache(q, uncollated_results);
			// Let the user know things are moving along.
			System.out.print(".");
			// Somewhere around once in 80 times..
			if (q.id % 80 == 0) System.out.println(); 
		} catch (Exception e) {
			// Eat exceptions
			e.printStackTrace();
		}
	}
}
