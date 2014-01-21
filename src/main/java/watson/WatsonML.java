package watson;
import java.util.*;
import org.apache.mahout.*;

public class WatsonML {
    /** Correlates search results for improved accuracy
     * TODO: handle inexact duplicates */
	
    public Resultset aggregate(Resultset... resultsets) {
    	HashMap<Result, Double> new_score = new HashMap<Result, Double>();
    	for (Resultset resultset : resultsets) {
    		for (Result result : resultset) {
    			// This just adds the (normalized) scores.
    			double score = result.getScore();
    			if (new_score.containsKey(result)) {
    				score = new_score.get(result);
    			}
    			new_score.put(result, score);
    		}
    	}
    	Resultset output_results = new Resultset("Combined");
    	for (Result input_result : new_score.keySet()) {
    		output_results.add(new Result(input_result)
    			.setScore(new_score.get(input_result)));
    	}
    	return output_results;
    }
}
