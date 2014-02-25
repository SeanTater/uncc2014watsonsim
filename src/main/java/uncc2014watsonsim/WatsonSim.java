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
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {

        //read from the command line
        System.out.println("Enter the jeopardy text: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Question question = new Question(br.readLine());
        
    	while (!question.raw_text.equalsIgnoreCase("")) {
	
	        HashSet<String> ignoreSet = new HashSet<String>();
	        ignoreSet.add("J! Archive");
	        ignoreSet.add("Jeopardy");
	        
	        //initialize indri and query
	        IndriSearch in = new IndriSearch();
	        in.setIndex(UserSpecificConstants.indriIndex);
	        in.runQuery(question.text);
	        for (int rank=0; rank < in.getResultCount(); rank++) {
	        	question.add(new ResultSet(
	    			in.getTitle(rank),
	    			"indri",
	    			rank,
	    			in.getScore(rank),
	    			false // correct? We don't know yet.
	    			));
	        }
	
	        //initialize and query lucene
	        LuceneSearch lu = new LuceneSearch(UserSpecificConstants.luceneSearchField);
	        lu.setIndex(UserSpecificConstants.luceneIndex);
	        lu.runQuery(question.text);
	        for (int rank=0; rank < in.getResultCount(); rank++) {
	        	question.add(new ResultSet(
	    			lu.getTitle(rank),
	    			"lucene",
	    			rank,
	    			lu.getScore(rank),
	    			false // correct? We don't know yet.
	    			));
	        }
	
	        //initialize google search engine and query.
	        WebSearchGoogle go = new WebSearchGoogle();
	        go.runQuery(question.text);
	        for (int rank=0; rank < in.getResultCount(); rank++) {
	        	question.add(new ResultSet(
	    			go.getTitle(rank),
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
	        System.out.println("Enter the jeopardy text or enter to quit: ");
	        br = new BufferedReader(new InputStreamReader(System.in));
	        question = new Question(br.readLine());
    	}
    }
}
