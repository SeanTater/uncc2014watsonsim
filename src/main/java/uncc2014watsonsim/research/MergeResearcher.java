package uncc2014watsonsim.research;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;

public class MergeResearcher extends Researcher {
	
	@Override
	/** Call merge on any two answers with the same title */
	public void research(Question q) throws Exception {
		// The left cursor moves right
		for (int first_ai=0; first_ai<q.size(); first_ai++) {
			// The right cursor moves left (so that we can delete safely)
			for (int second_ai=q.size()-1; second_ai>first_ai; second_ai--) {
				Answer first_a = q.get(first_ai);
				Answer second_a = q.get(second_ai);
				// Merge if necessary
				//TODO: This uses more or less exact matching. We should do better.
				if (second_a.matches(first_a)) {
					first_a.merge(second_a);
					q.remove(second_ai);
				}
			}
		}
	}

}