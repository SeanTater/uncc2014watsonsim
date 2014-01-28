package uncc2014watsonsim;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


import org.junit.Test;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class ScoreQMapIntegrationTest {

	@Test
    public void integrate() throws FileNotFoundException, ParseException, IOException {
    	/* TODO: Find out what the usual way of handling large test data is
    	 *  - Download from internet on the fly (from Dropbox, maybe)?
    	 *  - Place on Github with everything else?
    	 *  - Pretend it's not a problem? */
    	QuestionMap questionmap = new QuestionMap("/home/sean/Dropbox/Projects/deepqa/watson/data/ct.json");
    	Question question = questionmap.get("This London borough is the G in GMT, squire");
    	Engine ranked_answers = new AverageScorer().test(question);
    	String top_answer = ranked_answers.get(0).getTitle();
    	assertNotNull(top_answer);
    	assertThat(top_answer.length(), not(0));
    	//Logger.getLogger(Test.class.getName()).log(Level.INFO, "The answer: "+ ranked_answers.get(0).getTitle());
    }
	
	@Test
    public void sample() throws FileNotFoundException, ParseException, IOException {
    	/* TODO: Find out what the usual way of handling large test data is
    	 *  - Download from internet on the fly (from Dropbox, maybe)?
    	 *  - Place on Github with everything else?
    	 *  - Pretend it's not a problem? */
    	QuestionMap questionmap = new QuestionMap("/home/sean/Dropbox/Projects/deepqa/watson/data/ct.json");
    	int correct = 0;
    	int available = 0;
    	int rank = 0;
    	int total = 500;
    	int runs = total;
    	for (Question question : questionmap.values()) {
    		Engine ranked_answers = new PrebuiltLRScorer().test(question);
    		ResultSet top_answer = ranked_answers.get(0);
    		assertNotNull(top_answer);
    		assertThat(top_answer.getTitle().length(), not(0));
    		
    		String correct_answer_score = "Not in results";
    		int irank =0;
    		for (ResultSet r : ranked_answers) {
    			if(r.isCorrect()) {
    				correct_answer_score = String.valueOf(r.getScore());
    				rank += irank;
    			}
    			irank++;
    		}
    		
    		System.out.println("Q: " + question.question + "\n" +
    				"A[Guessed: " + top_answer.getScore() + "]: " + top_answer.getTitle() + "\n" +
					"A[Actual:" + correct_answer_score + "]: "  + question.answer);
    		
    		if (top_answer.isCorrect())
    			correct++;
    		if (!correct_answer_score.equalsIgnoreCase("Not in results"))
    			available++;
    		runs--;
    		if(runs < 0) break;
    	}
    	System.out.println("" + correct + " of " + total + " correct");
    	System.out.println("" + available + " of " + total + " could have been");

    	System.out.println("Average correct rank " + ((double)rank) / available);
	}
}
