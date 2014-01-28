package watson;

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
    	int total = 50;
    	int runs = total;
    	for (Question question : questionmap.values()) {
    		Engine ranked_answers = new AverageScorer().test(question);
    		ResultSet top_answer = ranked_answers.get(0);
    		assertNotNull(top_answer);
    		assertThat(top_answer.getTitle().length(), not(0));
    		
    		String correct_answer_score = "Not in results";
    		for (ResultSet r : ranked_answers)
    			if(r.getTitle().equalsIgnoreCase(question.answer))
    				correct_answer_score = String.valueOf(r.getScore());
    		
    		System.out.println("Q: " + question.question + "\n" +
    				"A[Guessed: " + top_answer.getScore() + "]: " + top_answer.getTitle() + "\n" +
					"A[Actual:" + correct_answer_score + "]: "  + question.answer);
    		
    		if (question.answer.equalsIgnoreCase(top_answer.getTitle()))
    			correct++;
    		runs--;
    		if(runs < 0) break;
    	}
    	System.out.println("" + correct + " of " + total + " correct");
    			/*String.format(
    			"%f | %i of %i correct",
    			((double) correct )/ total, correct, total));*/
    	
	}
}
