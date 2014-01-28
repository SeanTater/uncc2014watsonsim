package watson;
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
	
    public Engine test(Question question) {
    	// Look for identical results from multiple engines
    	Map<ResultSet, Double> new_score = new HashMap<ResultSet, Double>();
    	Map<ResultSet, Integer> new_entries = new HashMap<ResultSet, Integer>();
    	for (Engine engine : question) {
    		for (ResultSet resultset : engine) {
    			// This just adds the (normalized) scores.
    			double score = resultset.getScore();
    			int entries = 1;
    			if (new_score.containsKey(resultset)) {
    				score += new_score.get(resultset);
    				entries += new_entries.get(resultset);
    			}
    			new_score.put(resultset, score);
    			new_entries.put(resultset, entries);
    		}
    	}
    	
    	Engine output_results = new Engine("Combined");
    	for (ResultSet input_result : new_score.keySet()) {
    		ResultSet rs = new ResultSet(input_result);
    		rs.setScore(
    				new_score.get(input_result) /
    				new_entries.get(input_result)
					);
    		output_results.add(rs);
    	}
    	
    	Collections.sort(output_results);
    	Collections.reverse(output_results);
    	return output_results;
    }
}
