package edu.uncc.cs.watsonsim.researchers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Question;

public class StrictFilters extends Researcher {
	private final Logger log = Logger.getLogger(this.getClass());
	/**
	 * Perform several strict filters relating mostly to game rules.
	 * 
	 * 1: Remove J! Archive since it has actual answers.
	 * 2: Remove "List of *" because that's not the format of an answer.
	 * 3: Remove any answer inside the question because they don't give the
	 *    answers away in the questions (at least not in a string-match way)
	 * 4: Remove ultra-long answers because J! never wants a 3-minute speech
	 * 5: Remove answers not in Latin text
	 */
	public List<Answer> question(Question q, List<Answer> answers) {
		List<Answer> new_answers = new ArrayList<>();
		for (Answer a : answers) {
			
			// J! Archive has answers
			if (a.text.contains("J! Archive")) {}
			
			// "List of" is a bad sign 
			else if (a.text.contains("List of")) {}
			
			// Is the answer in the question?
			else if (almostContains(q.text, a.text)) {}
			
			// Is it too long?
			// The longest real answer in our sample of about 40,000 is:
			// How much wood would a woodchuck chuck if a woodchuck could chuck wood?
			// and it's 70 characters long. So cut there.
			else if (a.text.length() > 70) {}
			
			// Is over half of it non-Latin text?
			else if (a.text.replaceAll("[^A-Za-z0-9 ]", "").length() * 2 < a.text.length()) {}
			
			// Does it look like a web address?
			else if (a.text.matches("^(http://)?([A-Za-z]+\\.)?[A-Za-z]+\\.(com|net|org|co\\.[A-Za-z]{2})$")) {}
			
			else {
				new_answers.add(a);
			}
		}
		
		log.info("Eliminated " + (answers.size() - new_answers.size()) + " invalid answers");
		return new_answers;
	}
	
	/**
	 * Check if the question text (left) almost contains the answer text
	 * (right).
	 */
	public boolean almostContains(String left, String right) {
		// TODO: more stopword removal, etc.
		return left.toLowerCase().contains(right.toLowerCase());
	}
}
