package uncc2014watsonsim.research;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

public class WordProximity extends Scorer {
	Set<String> q_words = new HashSet<String>();
	
	@Override
	public void question(Question q) {
		q_words.clear();
		q_words.addAll(Arrays.asList(q.text.split("\\W+")));
		super.question(q);
	}
	
	@Override
	public double passage(Question q, Answer a, Passage p) {
		double distance = 1;
		double average_log_distance = 0;
		
		for (String w : p.text.split("\\W+")) {
			if (q_words.contains(w)) {
				average_log_distance += Math.log(distance);
				distance = 1;
			} else {
				distance++;
			}
		}

		// This result is given as log(interval). Does that matter?
		return average_log_distance;
	}
	
}
