package uncc2014watsonsim.scorers;

import java.util.List;
import java.util.Map;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

public abstract class Scorer {
	public abstract Scored<Answer> scoreAnswer(Question q, Answer a, List<Passage> passages);
}
