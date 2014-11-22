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
    	DBQuestionResultsSource dbquestions = new DBQuestionResultsSource("");
    	System.out.print("Processed question:");
    	int qcount=0;
    	for (Question q : dbquestions) {
    		new PassageCollector(q).collect();
    		System.out.println(++qcount);
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
			q.answer.direct_passages.remove(0);
			DBQuestionResultsSource.store_passages(q, q.answer.direct_passages);
		} catch (Exception e) {
			// Eat exceptions
			e.printStackTrace();
		}
	}
}
