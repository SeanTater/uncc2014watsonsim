package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Score {
	public static final int MAX_PASSAGE_COUNT = 50;
	public static final List<String> answer_score_names = new ArrayList<>();
	public static final List<String> passage_score_names = new ArrayList<>();
	
	/** Register the answer score for automatically generated model data
	 * @param name	The ANSWER_SCORE (uppercase, with underscores)
	 * 
	 * This function is idempotent.
	 */
	public static void registerAnswerScore(String name) {
		int index = Collections.binarySearch(answer_score_names, name);
		if (index < 0)
			answer_score_names.add(-index-1, name);
	}
	
	/** Register the passage score for automatically generated model data
	 * @param name	The PASSAGE_SCORE (uppercase, with underscores)
	 * 
	 * This function is idempotent.
	 */
	public static void registerPassageScore(String name) {
		int index = Collections.binarySearch(passage_score_names, name);
		if (index < 0)
			passage_score_names.add(-index-1, name);
	}
}
