package uncc2014watsonsim.research;

import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;

/*Author : Jagan Vujjini
 * 
 * Just Modified Jacob Medd's Scorer to ignore Stop Words.
 * Will be adding the Stemmed Words Functionality.
 * 
 */

//Score is Percent of words in common / the average distance between the words
public class PercentFilteredWordsInCommon extends Scorer {
	
	public double scorePassage(Question q, Passage p) {
			String filteredQ = StringUtils.filterRelevant(q.text);
			String[] questionTextArray = filteredQ.split(" ");
			int[] distanceBetweenWords = new int[filteredQ.length()];
			int distanceIndex = 0;
			int lastMatch = -1;
			String passageText = StringUtils.filterRelevant(p.text);
			int distanceSum = 0;
			int count = 0;
			for (int i = 0; i < questionTextArray.length; i++)
			{
				if (passageText.contains(questionTextArray[i]))
				{
					if (lastMatch == -1)
						lastMatch = i;
					else
					{
						distanceBetweenWords[distanceIndex] = (i - lastMatch);
						distanceIndex += 1;
						lastMatch = i;
					}
					count += 1;
				}
			}
			for (int i = 0; i < count; i++)
			{
				distanceSum += distanceBetweenWords[i];
			}
			if (count > 0)
				return (count/((double)filteredQ.length()))/((double)distanceSum/count);
			else
				return 0;
	}
	
	public void stemFirst(String text) {
		
	}

}
