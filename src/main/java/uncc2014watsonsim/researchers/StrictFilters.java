package uncc2014watsonsim.researchers;

import org.apache.log4j.Logger;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;

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
	public void question(Question q) {
		int original_size = q.size(); // For logging
		for (int i=q.size()-1; i >= 0; i--) {
			Answer a = q.get(i);
			
			// J! Archive has answers
			if (a.candidate_text.contains("J! Archive"))
				q.remove(i);
			
			// "List of" is a bad sign 
			else if (a.candidate_text.contains("List of"))
				q.remove(i);
			
			// Is the answer in the question?
			else if (almostContains(q.getRaw_text(), a.candidate_text))
				q.remove(i);
			
			// Is it too long?
			// The longest real answer in our sample of about 40,000 is:
			// How much wood would a woodchuck chuck if a woodchuck could chuck wood?
			// and it's 70 characters long. So cut there.
			else if (a.candidate_text.length() > 70)
				q.remove(i);
			
			// Is over half of it non-Latin text?
			else if (a.candidate_text.replaceAll("[^A-Za-z0-9 ]", "").length() * 2 < a.candidate_text.length())
				q.remove(i);
			
			// Does it look like a web address?
			else if (a.candidate_text.matches("^(http://)?([A-Za-z]+\\.)?[A-Za-z]+\\.(com|net|org|co\\.[A-Za-z]{2})$"))
				q.remove(i);
		}
		
		log.info("Eliminated " + (original_size - q.size()) + " invalid answers");
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
