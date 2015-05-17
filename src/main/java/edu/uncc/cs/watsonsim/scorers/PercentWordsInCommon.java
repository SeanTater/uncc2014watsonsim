package edu.uncc.cs.watsonsim.scorers;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;

//Score is Percent of words in common / the average distance between the words
public class PercentWordsInCommon extends PassageScorer {
	
public double scorePassage(Phrase q, Answer a, Passage p) {
		String[] questionTextArray = q.text.split(" ");
		int[] distanceBetweenWords = new int[q.text.length()];
		int distanceIndex = 0;
		int lastMatch = -1;
		String passageText = p.text;
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
			return (count/((double)q.text.length()))/((double)distanceSum/count);
		else
			return 0;
	}

}
