package uncc2014watsonsim.sources;

import uncc2014watsonsim.DBQuestionResultsSource;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.researchers.PassageRetrieval;

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
