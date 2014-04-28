package uncc2014watsonsim.research;

import java.util.List;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;

/*Author : Jacob Medd, Jagan Vujjini
 * 
 * Just Modified Jacob Medd's Scorer to ignore Stop Words.
 * Will be adding the Stemmed Words Functionality.
 *
 * 
 * Later modified. It seems that:
 *   (% word in common) / (mean distance between common words)
 * is a constant.
 * 
 * So just use one of them, and the % in common is easiest.
 */

public class PercentFilteredWordsInCommon extends PassageScorer {
	
	public double scorePassage(Question q, Answer a, Passage p) {
			List<String> questionTextArray = StringUtils.tokenize(q.text);
			List<String> passageText = p.tokens();
			int count = 0;
			for (String word : questionTextArray)
				if (passageText.contains(word))
					count += 1;
			return (count / (double)questionTextArray.size());
	}

}
