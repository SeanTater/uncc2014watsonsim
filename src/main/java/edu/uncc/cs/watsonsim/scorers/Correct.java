package edu.uncc.cs.watsonsim.scorers;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.StringUtils;
import edu.uncc.cs.watsonsim.nlp.Synonyms;

public class Correct extends AnswerScorer {
	private final Synonyms syn;
	public Correct(Environment env) {
		syn = new Synonyms(env);
	}
	@Override
	/**
	 * Generate the target attribute for Machine Learning.
	 * @returns correctness		0.0 -> incorrect, 1.0 -> correct
	 * */
	public double scoreAnswer(Question q, Answer a) {
		if (q.answer == null) {
			return 0;
		} else {
			return (syn.matchViaLevenshtein(q.answer.text, a.text)
					|| syn.matchViaSearch(q.answer.text, a.text)
					|| StringUtils.containsIgnoreCase(a.text, q.answer.text)) ? 1 : 0;
		}
        
	}
}
