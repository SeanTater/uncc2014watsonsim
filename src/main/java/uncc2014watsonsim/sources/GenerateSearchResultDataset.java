package uncc2014watsonsim.sources;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.DBQuestionSource;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.search.IndriSearcher;
import uncc2014watsonsim.search.LuceneSearcher;
import uncc2014watsonsim.search.GoogleSearcher;
import uncc2014watsonsim.search.Searcher;

/**
 *
 * @author Phani Rahul
 */
public class GenerateSearchResultDataset {

    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	// 7 lines to read an integer from stdin
    	int start = 0;
    	try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
    		System.out.print("Start on question number: ");
    		start = Integer.parseInt(br.readLine());
    	}
    	if (start <= 0) {
    		System.out.println("Starting number must be greater than 0.");
    		return;
    	}
        ExecutorService pool = Executors.newFixedThreadPool(8);
        DBQuestionSource dbquestions = new DBQuestionSource("where rowid > "+start+" limit 100");
    	for (Question q : dbquestions) {
    		//pool.execute(new SingleTrainingResult(q));
    		new SingleTrainingResult(q).run();
    	}
        pool.shutdown();
        try {
            pool.awaitTermination(2, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(GenerateSearchResultDataset.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Uploading results...");
        
        Sardine conn = SardineFactory.begin();
        try (FileInputStream fis = new FileInputStream(new File("data/questions.db"))) {
        	conn.put("http://seantater.is-a-linux-user.org/watsonsim/" + (new Date()).toString().replace(" ", "-") + ".db", fis);
    	}
        System.out.println("Done.");
    }
}

class SingleTrainingResult extends Thread {
	Question q;
	static Searcher[] searchers = {
		new LuceneSearcher(),
		new IndriSearcher(),
		new GoogleSearcher()
	};
	
	public SingleTrainingResult(Question q) {
		this.q = q;
	}
	
	public void run() {
		try {
			List<Answer> uncollated_results = new ArrayList<Answer>(); 
			for (Searcher searcher : searchers)
				//TODO: fix the following line which is failing to compile
				//uncollated_results.addAll(searcher.runQuery(q.text));
			//Thread.sleep(1000);
			DBQuestionSource.replace_cache(q, uncollated_results);
			// Let the user know things are moving along.
			System.out.print(""+ q.id + " ");
			// Somewhere around once in 80 times..
			if (q.id % 80 == 0) System.out.println(); 
		} catch (Exception e) {
			// Eat exceptions
			e.printStackTrace();
		}
	}
}
