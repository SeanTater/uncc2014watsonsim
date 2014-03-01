package uncc2014watsonsim;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import privatedata.UserSpecificConstants;

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
	        WebSearchGoogle go = new WebSearchGoogle();
	        go.runQuery(question.text);
	        for (int rank=0; rank < go.getResultCount(); rank++) {
	        	question.add(new ResultSet(
	    			go.getTitle(rank),
	    			"", 
	    			"google",
	    			rank,
	    			rank,
	    			false // correct? We don't know yet.
	    			));
	        }
	        
	        new AverageScorer().test(question);
	        for (ResultSet r : question) {
	        	System.out.println(String.format("[%01f] %s", r.first("combined").score, r.getTitle()));
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
