package edu.uncc.cs.watsonsim.researchers;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Question;

/** Researchers can modify questions and have the guarantee of running
 * sequentially. They also do not return double's because they are not expected
 * to do scoring. If they do, they can use score() themselves. Consider using
 * Scorer instead for that, which is parallelizable.
 */
abstract public class Researcher {
	/**
	 * The empty researcher does nothing.
	 */
	public static final Researcher NIL = new Researcher() {
		public Question pull(Question q){return q;}
	};
	
	/**
	 * The previous item in the research chain
	 */
	protected Researcher chain = NIL;
	
	public static Researcher pipe(Researcher... segments) {
		Researcher prev = NIL;
		for (Researcher link : segments) {
			link.chain = prev;
			prev = link;
		}
		return prev;
	}
	
	/**
	 * Wrapper method to pull questions through the research chain
	 */
	public Question pull(Question q) {
		question(chain.pull(q));
		return q;
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
		for (Passage p: a.passages)
			passage(q, a, p);
	}
	
	/** Default implementation for researching an answer.
	 * Does nothing by default. You don't need to override this if you don't
	 * use it.
	 * 
	 * @param answer
	 */
	public void passage(Question q, Answer a, Passage p) {}
}