package uncc2014watsonsim.researchers;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.nlp.LAT;


public class TagLAT extends Researcher {
	private final LAT backend = new LAT();
	/**
	 * Find the possible lexical types of a candidate, and label the answer.
	 */
	@Override
	public void answer(Question q, Answer a) {
		a.lexical_types = backend.types(a.candidate_text);
	}

}

