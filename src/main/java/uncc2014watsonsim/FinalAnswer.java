package uncc2014watsonsim;

import java.util.List;
import java.util.Optional;

import uncc2014watsonsim.scorers.AScore;
import uncc2014watsonsim.scorers.QScore;

/**
 * This immutable type wraps the results of asking a question.
 * @author Sean
 */
public class FinalAnswer {
	private final Question question;
	private final List<Answer> candidates;
	private final QScore scores;
	public FinalAnswer(Question question, List<Answer> candidates, QScore scores) {
		this.question = question;
		this.candidates = candidates;
		this.scores = scores;
	}
	
	/**
	 * Get the question this FinalAnswer corresponds to.
	 * @return The Question wrapper object.
	 */
	public Question getQuestion() {
		return question;
	}
	
	/**
	 * Get the ranked list of candidate answers in order of decreasing confidence.
	 * @return A ranked list of Answer wrapper objects.
	 */
	public List<Answer> getCandidates() {
		return candidates;
	}
	
	
	/**
	 * Get the top answer corresponding to this question.
	 * 
	 * @return An optional answer with its scores. Never gives null, only
	 * empty(), and never throws an exception.
	 */
	public Optional<Answer> getTopAnswer() {
		if (candidates.size() == 0)
			return Optional.empty();
		else
			return Optional.of(candidates.get(0));
	}
	
	/**
	 * Get the top answer scores corresponding to this question.
	 * If getTopAnswer() is empty, then this will be too and vice versa.
	 * @return An optional answer with its scores. Never gives null, only
	 * empty(), and never throws an exception.
	 */
	public Optional<AScore> getTopScores() {
		return getTopAnswer().map(scores::get);
	}
	
	
	/**
	 * Get a map of scores for each candidate answer.
	 * This is a mapping of Answer -> AScore.
	 * @return A map of answers to answer scores, for use along with the candidates field.
	 */
	public QScore getScores() {
		return scores;
	}

}
