package uncc2014watsonsim;

abstract public class Researcher {
	// There will probably be something you want many researchers to do..
	public void research(Question q) {
		
	}
	
	/** Default implementation of research for a question.
	 * Simply calls research_answer for every Answer
	 * Override this if you need more power.
	 * @param question
	 * @throws Exception 
	 */
	public void research_question(Question q) throws Exception {
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
