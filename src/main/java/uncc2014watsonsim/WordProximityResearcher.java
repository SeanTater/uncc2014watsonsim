package uncc2014watsonsim;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import uncc2014watsonsim.search.LuceneSearcher;
import uncc2014watsonsim.search.Searcher;

public class WordProximityResearcher extends Researcher {

	@Override
	public void research_question(Question q) throws Exception {
		Set<String> q_words = new HashSet<String>();
		q_words.addAll(Arrays.asList(q.text.split("\\W+")));
		
		// Calculate the geometric mean of question word interval
		double distance = 1;
		double average_log_distance = 0;
		for (Answer a : q) {
			for (String w : a.getFullText().split("\\W+")) {
				if (q_words.contains(w)) {
					average_log_distance += Math.log(distance);
					distance = 1;
				} else {
					distance++;
				}
			}
		}
		
		// This result is given as log(interval). Does that matter?
	}
	
}
