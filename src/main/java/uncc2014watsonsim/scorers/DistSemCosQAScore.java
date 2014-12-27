package uncc2014watsonsim.scorers;

import java.util.ArrayList;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;

public class DistSemCosQAScore extends AnswerScorer {
	
	@Override
	public double scoreAnswer(Question q, Answer a) {
		ArrayList<Double> q_context = StringUtils.getPhraseContextSafe(q.getRaw_text());
		ArrayList<Double> a_context = StringUtils.getPhraseContextSafe(a.candidate_text);
		
		return StringUtils.getCosineSimilarity(q_context, a_context);
	}

}
