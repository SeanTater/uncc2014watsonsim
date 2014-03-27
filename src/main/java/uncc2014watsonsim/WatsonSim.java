package uncc2014watsonsim;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;

import uncc2014watsonsim.search.BingSearcher;
import uncc2014watsonsim.search.GoogleSearcher;
import uncc2014watsonsim.search.IndriSearcher;
import uncc2014watsonsim.search.LuceneSearcher;
import uncc2014watsonsim.search.Searcher;

/**
 *
 * @author Phani Rahul
 */
public class WatsonSim {
	static final Searcher[] searchers = {
		new LuceneSearcher(),
		new IndriSearcher(),
		new BingSearcher(),
		//new GoogleSearcher()
	};
	
	static final Researcher[] researchers = {
		new MergeResearcher(),
		new PersonRecognitionResearcher()
	};
	
	static final Learner learner = new WekaLearner();

    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {

        //read from the command line
        System.out.println("Enter the jeopardy text: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String command = br.readLine();
        
    	while (!command.isEmpty()) {
            Question question = new Question(command);
	        HashSet<String> ignoreSet = new HashSet<String>();
	        ignoreSet.add("J! Archive");
	        ignoreSet.add("Jeopardy");
	        
	        System.out.println("This is a " + question.getType() + " Question");
	        
        	// Query every engine
	        for (Searcher s: searchers)	        	
	        		question.addAll(s.runQuery(question.text));
	        
	        /* This is Jagan's quotes FITB code. I do not have quotes indexed separately so I can't do this.
	        for (Searcher s : searchers){
	        	// Query every engine
	        	if(question.getType() == QType.FACTOID){
	        		question.addAll(s.runQuery(question.text, UserSpecificConstants.indriIndex, UserSpecificConstants.luceneIndex));
	        	} else if (question.getType() == QType.FITB) {
	        		question.addAll(s.runQuery(question.text, UserSpecificConstants.quotesIndriIndex, UserSpecificConstants.quotesLuceneIndex));
	        	} else {
	        		return;
	        	}
	        }*/
	        
        	for (Researcher r : researchers) {
        		r.research(question);
        	}
        	
	        learner.test(question);
	        
	        // Not a range-based for because we want the rank
	        for (int i=0; i<question.size(); i++) {
	        	Answer r = question.get(i);
	        	// The merge researcher does what was once here.
	        	System.out.println(String.format("%2d: %s", i, r));
	        }
	        
	
	        //read from the command line
	        System.out.println("Enter [0-9]+ to inspect full text, a question to search again, or enter to quit\n>>> ");
	        command = br.readLine();
	        while (command.matches("[0-9]+")) {
	        	Answer rs = question.get(Integer.parseInt(command));
	        	System.out.println("Full text for [" + rs.getTitle() + "]: \n" + rs.getFullText() + "\n");
	        	command = br.readLine();
	        }
    	}
    }
}
