package uncc2014watsonsim.scorers;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;

public class DistSemCosineSimilarity extends AnswerScorer {
	
	@Override
	public double scoreAnswer(Question q, Answer a) {
		int[] q_context = StringUtils.getPhraseContextSafe(q.text);
		int[] a_context = StringUtils.getPhraseContextSafe(a.candidate_text);
		
		return StringUtils.getCosineSimilarity(q_context, a_context);
	}

}
