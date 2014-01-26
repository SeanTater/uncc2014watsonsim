package watson;
import java.util.*;
import org.apache.mahout.*;

public class Scorer {
    /** Correlates search results for improved accuracy */
	
	public void train(AnswerList... resultsets) {
		train(Arrays.asList(resultsets));
	}
	
	public void train(List<AnswerList> resultsets) {
		// No-op until ML code is added
	}
	
	public AnswerList test(AnswerList... resultsets) {
		return test(Arrays.asList(resultsets));
	}
	
    public AnswerList test(List<AnswerList> engines) {
    	Map<ResultSet, Double> new_score = new HashMap<ResultSet, Double>();
    	Map<ResultSet, Integer> new_entries = new HashMap<ResultSet, Integer>();
    	for (AnswerList resultlist : engines) {
    		for (ResultSet resultset : resultlist) {
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
    	AnswerList output_results = new AnswerList("Combined");
    	for (ResultSet input_result : new_score.keySet()) {
    		ResultSet rs = new ResultSet(input_result);
    		rs.setScore(
    				new_score.get(input_result) /
    				new_entries.get(input_result)
					);
    		output_results.add(rs);
    	}
    	
    	Collections.sort(output_results);
    	return output_results;
    }
}
