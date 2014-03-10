package uncc2014watsonsim;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WordProximityResearcher extends Researcher {

	@Override
	public void research_question(Question q) {
		// TODO Auto-generated method stub
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
