package watson;
import java.util.*;
import org.apache.mahout.*;

public class Scorer {
    /** Correlates search results for improved accuracy */
	
	public void train(Engine... resultsets) {
		train(Arrays.asList(resultsets));
	}
	
	public void train(List<Engine> resultsets) {
		// No-op until ML code is added
	}
	
    public Engine test(Question question) {
    	Map<ResultSet, Double> new_score = new HashMap<ResultSet, Double>();
    	Map<ResultSet, Integer> new_entries = new HashMap<ResultSet, Integer>();
    	for (Engine resultlist : question) {
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
    	return output_results;
    }
}
