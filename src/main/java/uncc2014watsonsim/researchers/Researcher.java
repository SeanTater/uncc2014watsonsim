package uncc2014watsonsim.researchers;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import java.util.List;

/** Researchers can modify questions and have the guarantee of running
 * sequentially. They also do not return double's because they are not expected
 * to do scoring. If they do, they can use score() themselves. Consider using
 * Scorer instead for that, which is parallelizable.
 */
abstract public class Researcher {

	/** Default implementation of research for a question.
	 * Simply calls research_answer for every Answer
	 * Override this if you need more power.
	 * @param answers TODO
	 * @param question
	 * @throws Exception 
	 */
	public abstract void question(Question q, List<Answer> answers);
	
	/** Default implementation for ending question research.
	 * This might trigger some database inserts or like writing, for example.
	 */
	public void complete() {};
}
