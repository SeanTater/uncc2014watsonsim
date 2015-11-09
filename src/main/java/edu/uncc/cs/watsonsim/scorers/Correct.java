package edu.uncc.cs.watsonsim.scorers;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.nlp.Relatedness;

public class Correct extends AnswerScorer {
	private final Relatedness syn;
	public Correct(Environment env) {
		syn = new Relatedness(env);
	}
	@Override
	/**
	 * Generate the target attribute for Machine Learning.
	 * @returns correctness		0.0 -> incorrect, 1.0 -> correct
	 * */
	public double scoreAnswer(Question q, Answer a) {
		if (q.correct_answer == null) {
			return 0;
		} else {
			return syn.implies(q.correct_answer, a) ? 1 : 0;
		}
        
	}
}
