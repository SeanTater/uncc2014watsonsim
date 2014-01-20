package watson;
import java.util.*;
import org.apache.mahout.*;

public class WatsonML {
    /** Correlates search results for improved accuracy
     * TODO: handle inexact duplicates */
    public Resultset aggregate(List<Resultset> resultsets) {
    	HashMap<String, List<Result>> related_results;// = new HashMap<String, List<Result>>;
    	for (Resultset resultset : resultsets) {
    		for (Result result : resultset) {
    			if (!related_results.containsKey(result.title)) {
    				related_results.put(result.title, new ArrayList<Result>); 
    			}
				related_results.get(result.title).add(result);
    		}
    	}
    }
}
