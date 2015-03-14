package edu.uncc.cs.watsonsim.scorers;

import java.util.List;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;

public interface Scorer {
	public void scoreQuestion(Question q, List<Answer> answers);
}
