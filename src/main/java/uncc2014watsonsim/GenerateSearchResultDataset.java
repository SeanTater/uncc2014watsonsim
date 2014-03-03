package uncc2014watsonsim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        ExecutorService pool = Executors.newFixedThreadPool(15);
    	for (Question q : QuestionDB.fetch_uncached(0, 10)) {
    		pool.execute(new SingleTrainingResult(q));
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
			System.out.print(".");
		} catch (Exception e) {
			// Eat exceptions
			e.printStackTrace();
		}
	}
}
