package uncc2014watsonsim;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
/**
 *
 * @author Phani Rahul
 */
public class WatsonSim {

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
	        
	        //initialize indri and query
        	question.addAll(IndriSearch.runQuery(question.text));
	
	        //initialize and query lucene
	        question.addAll(LuceneSearch.runQuery(question.text));
	
	        //initialize google search engine and query.
	        question.addAll(WebSearchGoogle.runQuery(question.text));
	        
	        new AverageScorer().test(question);
	        // Not a range-based for because we want the rank
	        for (int i=0; i<question.size(); i++) {
	        	ResultSet r = question.get(i);
	        	System.out.println(String.format("%d: [%01f] %s", i, r.first("combined").score, r.getTitle()));
	        }
	        
	
	        //read from the command line
	        System.out.println("Enter [0-9]+ to inspect full text, a question to search again, or enter to quit\n>>> ");
	        command = br.readLine();
	        while (command.matches("[0-9]+")) {
	        	ResultSet rs = question.get(Integer.parseInt(command));
	        	System.out.println("Full text for [" + rs.getTitle() + "]: \n" + rs.getFullText() + "\n");
	        	command = br.readLine();
	        }
    	}
    }
}
