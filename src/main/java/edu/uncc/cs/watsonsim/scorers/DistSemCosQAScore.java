package edu.uncc.cs.watsonsim.scorers;

import java.util.ArrayList;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.StringUtils;

public class DistSemCosQAScore extends AnswerScorer {
	
	@Override
	public double scoreAnswer(Question q, Answer a) {
		ArrayList<Double> q_context = StringUtils.getPhraseContextSafe(q.text);
		ArrayList<Double> a_context = StringUtils.getPhraseContextSafe(a.candidate_text);
		
		return StringUtils.getCosineSimilarity(q_context, a_context);
	}

}
