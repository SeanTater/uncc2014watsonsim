package watson;
import java.util.*;
import org.apache.mahout.*;

public class Scorer {
    /** Correlates search results for improved accuracy */
	
    public Resultset test(Resultset... resultsets) {
    	HashMap<Result, Double> new_score = new HashMap<Result, Double>();
    	HashMap<Result, Integer> new_entries = new HashMap<Result, Integer>();
    	for (Resultset resultset : resultsets) {
    		for (Result result : resultset) {
    			// This just adds the (normalized) scores.
    			double score = result.getScore();
    			int entries = 1;
    			if (new_score.containsKey(result)) {
    				score += new_score.get(result);
    				entries += new_entries.get(result);
    			}
    			new_score.put(result, score);
    			new_entries.put(result, entries);
    		}
    	}
    	Resultset output_results = new Resultset("Combined");
    	for (Result input_result : new_score.keySet()) {
    		output_results.add(
				new Result(input_result).setScore(
    				new_score.get(input_result) /
    				new_entries.get(input_result)
					)
				);
    	}
    	
    	Collections.sort(output_results);
    	return output_results;
    }
}
