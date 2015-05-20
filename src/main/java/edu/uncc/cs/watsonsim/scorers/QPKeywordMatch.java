package edu.uncc.cs.watsonsim.scorers;

import java.util.List;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.StringUtils;

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

public class QPKeywordMatch extends PassageScorer {
	
	public double scorePassage(Phrase q, Answer a, Passage p) {
			List<String> questionTextArray = StringUtils.tokenize(q.text);
			int count = 0;
			for (String word : questionTextArray)
				if (p.getTokens().contains(word))
					count += 1;
			return (count / (double)questionTextArray.size());
	}
}
