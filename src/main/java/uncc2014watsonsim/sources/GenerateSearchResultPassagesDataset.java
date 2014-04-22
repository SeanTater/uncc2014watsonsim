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
import uncc2014watsonsim.DBQuestionResultsSource;
import uncc2014watsonsim.DBQuestionSource;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.research.PassageRetrieval;
import uncc2014watsonsim.search.IndriSearcher;
import uncc2014watsonsim.search.LuceneSearcher;
import uncc2014watsonsim.search.GoogleSearcher;
import uncc2014watsonsim.search.Searcher;

/**
 *
 * @author walid shalaby (adapted from GenerateSearchResultDataset)
 */
public class GenerateSearchResultPassagesDataset {

    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	int curq = -1;
    	DBQuestionResultsSource dbquestions = new DBQuestionResultsSource("");
    	for (Question q : dbquestions) {
    		new PassageCollector(q).collect();
    		if(curq!=q.id) {
    			curq = q.id;
    			System.out.print(""+ q.id + " ");
    			// Somewhere around once in 80 times..
    			if (q.id % 80 == 0) System.out.println();
    		}    		
    	}
        
        System.out.println("Done.");
    }
}

class PassageCollector extends Thread {
	Question q;
	PassageRetrieval p;
	
	public PassageCollector(Question q) {
		this.q = q;
		p = new PassageRetrieval();
	}
	
	public void collect() {
		try {
			p.answer(q, q.answer);
			// set correct flag same as original result
			
			// remove first passage as it is already the search result
			q.answer.passages.remove(0);
			DBQuestionResultsSource.store_passages(q, q.answer.passages);
		} catch (Exception e) {
			// Eat exceptions
			e.printStackTrace();
		}
	}
}
