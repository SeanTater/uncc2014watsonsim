package uncc2014watsonsim.scorers;

import java.util.List;
import java.util.Map;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

public abstract class Scorer {
	public abstract Scored<Answer> scoreAnswer(Question q, Answer a, List<Passage> passages);
	
	/**
	 * This bit of subterfuge allows you to wrap something with a single
	 * score whose name is based on the name of the subclass.
	 * 
	 * @param target  Whatever you want to score
	 * @param score   The score you want to give it
	 * @return        The wrapped Scored<T>
	 */
	public <T> Scored<T> wrap(T target, double score) {
		return Scored.singleton(target, this, score);
	}
}
