package edu.uncc.cs.watsonsim.researchers;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;


/** Trim any text from before the hyphen in the candidate text of an answer */
public class HyphenTrimmer extends Researcher {
	
	public Answer answer(Question q, Answer a) {
		String[] improved_answer_parts = a.text.split("[-:(|]");
		
		if (improved_answer_parts.length>0) {
			a.log(this, "Trimming superfluous info");
			return a.withText(improved_answer_parts[0].trim());
		}
		return a;
	}

}
