package uncc2014watsonsim.research;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;

abstract public class Researcher {
	// There will probably be something you want many researchers to do..
	
	/** Default implementation of research for a question.
	 * Simply calls research_answer for every Answer
	 * Override this if you need more power.
	 * @param question
	 * @throws Exception 
	 */
	public void research(Question q) throws Exception{
		for (Answer a : q)
			research_answer(a);
	}
	
	/** Default implementation for researching an answer.
	 * Does nothing by default. You don't need to override this if you don't
	 * use it.
	 * 
	 * @param answer
	 */
	public void research_answer(Answer a) {}
}
