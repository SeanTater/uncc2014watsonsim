package uncc2014watsonsim.researchers;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

/** Researchers can modify questions and have the guarantee of running
 * sequentially. They also do not return double's because they are not expected
 * to do scoring. If they do, they can use score() themselves. Consider using
 * Scorer instead for that, which is parallelizable.
 */
abstract public class Researcher {
	
	/** Default implementation of research for a question.
	 * Simply calls research_answer for every Answer
	 * Override this if you need more power.
	 * @param question
	 * @throws Exception 
	 */
	public void research(Question q) {
		question(q);
	}

	/** Default implementation of research for a question.
	 * Simply calls research_answer for every Answer
	 * Override this if you need more power.
	 * @param question
	 * @throws Exception 
	 */
	public void question(Question q) {
		// Use counting instead of iteration to allow concurrent modification
		for (int i=0; i < q.size(); i++)
			answer(q, q.get(i));
	}
	
	/** Default implementation for researching an answer.
	 * Does nothing by default. You don't need to override this if you don't
	 * use it.
	 * @param q TODO
	 * @param answer
	 * 
	 * @return TODO
	 */
	public void answer(Question q, Answer a) {
		for (Passage p: a.direct_passages)
			passage(q, a, p);
	}
	
	/** Default implementation for researching an answer.
	 * Does nothing by default. You don't need to override this if you don't
	 * use it.
	 * 
	 * @param answer
	 */
	public void passage(Question q, Answer a, Passage p) {}
	
	/** Default implementation for ending question research.
	 * This might trigger some database inserts or like writing, for example.
	 */
	public void complete() {};
}
