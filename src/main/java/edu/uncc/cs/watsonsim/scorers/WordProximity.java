package edu.uncc.cs.watsonsim.scorers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Question;

public class WordProximity extends PassageScorer {
	Set<String> q_words = new HashSet<String>();
	
	@Override
	public void scoreQuestion(Question q) {
		q_words.clear();
		q_words.addAll(Arrays.asList(q.text.split("\\W+")));
		super.scoreQuestion(q);
	}
	
	@Override
	public double scorePassage(Question q, Answer a, Passage p) {
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
