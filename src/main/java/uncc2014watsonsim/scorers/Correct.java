package uncc2014watsonsim.scorers;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;
import uncc2014watsonsim.nlp.Environment;
import uncc2014watsonsim.nlp.Synonyms;

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
			return (syn.matchViaLevenshtein(q.answer.candidate_text, a.candidate_text)) ? 1 : 0;
		}
        
	}
}
