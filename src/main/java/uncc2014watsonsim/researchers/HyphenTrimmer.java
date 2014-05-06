package uncc2014watsonsim.researchers;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;


/** Trim any text from before the hyphen in the candidate text of an answer */
public class HyphenTrimmer extends Researcher {
	
	public void answer(Question q, Answer a) {
		String improved_answer = a.candidate_text.split("[-:]")[0].trim();
		if (!improved_answer.isEmpty()) {
			a.candidate_text = improved_answer;
		}
	}

}
