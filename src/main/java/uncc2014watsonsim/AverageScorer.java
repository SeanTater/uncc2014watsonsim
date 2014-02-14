package uncc2014watsonsim;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import org.apache.mahout.*;
import org.json.simple.parser.ParseException;

public class AverageScorer {
    /** Correlates search results for improved accuracy */
	public AverageScorer() {}
	public AverageScorer(String filename) throws FileNotFoundException, ParseException, IOException {
		train(new QuestionMap(filename));
	}
	
	public void train(QuestionMap dataset) {
		// No-op until ML code is added
	}
	
    public Question test(Question question) {
    	for (ResultSet result : question) {
    		double score = 0;
    		int count = 0;
    		for (Engine engine : result.engines) {
    			score += engine.score;
    			count++;
    		}
    		score /= count;
    		result.engines.add(new Engine("combined", count, score));
    	}
    	
    	Collections.sort(question);
    	Collections.reverse(question);
    	return question;
    }
}
