package watson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.parser.ParseException;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

/**
 * @author Phani Rahul
 * @author Sean Gallagher
 */
public class ScoreQMapIntegrationTest {

	@Test
    public void integrate() {
        try {
        	/* TODO: Find out what the usual way of handling large test data is
        	 *  - Download from internet on the fly (from Dropbox, maybe)?
        	 *  - Place on Github with everything else?
        	 *  - Pretend it's not a problem? */
        	QuestionMap questionmap = new QuestionMap("/home/sean/Dropbox/Projects/deepqa/watson/data/ct.json");
        	Question question = questionmap.get("This London borough is the G in GMT, squire");
        	Engine ranked_answers = new Scorer().test(question);
            System.out.println("The answer: "+ ranked_answers.get(0).getTitle());
        } catch (ParseException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
