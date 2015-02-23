package uncc2014watsonsim.scorers;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;

public class Correct extends AnswerScorer {
	@Override
	/**
	 * Generate the target attribute for Machine Learning.
	 * @returns correctness		0.0 -> incorrect, 1.0 -> correct
	 * */
	public double scoreAnswer(Question q, Answer a) {
        int dist = StringUtils.getLevenshteinDistance(
        		StringUtils.filterRelevant(a.candidate_text),
        		StringUtils.filterRelevant(q.answer.candidate_text),
        		3);
        // -1 means "uncertain, but greater than the threshold"
		return (0 <= dist && dist < 2) ? 1 : 0;
	}
}
