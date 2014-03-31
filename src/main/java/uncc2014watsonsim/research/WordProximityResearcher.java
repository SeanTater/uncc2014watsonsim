package uncc2014watsonsim.research;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;

public class WordProximityResearcher extends Researcher {

	@Override
	public void research(Question q) throws Exception {
		Set<String> q_words = new HashSet<String>();
		q_words.addAll(Arrays.asList(q.text.split("\\W+")));
		
		// Calculate the geometric mean of question word interval
		for (Answer a : q) {
			double distance = 1;
			double average_log_distance = 0;
			
			for (String w : a.getFullText().split("\\W+")) {
				if (q_words.contains(w)) {
					average_log_distance += Math.log(distance);
					distance = 1;
				} else {
					distance++;
				}
			}

			// This result is given as log(interval). Does that matter?
			a.scores.put(Score.WORD_PROXIMITY, average_log_distance);
		}
		
	}
	
}
