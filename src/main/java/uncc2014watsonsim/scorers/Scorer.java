package uncc2014watsonsim.scorers;

import java.util.Map;

import uncc2014watsonsim.Question;

public abstract class Scorer {
	public abstract QScore scoreQuestion(Question q);
}
