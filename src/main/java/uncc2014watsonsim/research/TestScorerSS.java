package uncc2014watsonsim.research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.QType;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;

public class TestScorerSS extends Scorer {
	Set<String> q_words = new HashSet<String>();
	
	public void question(Question q) {
		q_words.clear();
		q_words.addAll(Arrays.asList(q.text.split("\\W+")));
		super.question(q);
	}
	
	public double passage(Question q, Answer a, Passage p) {
		String rawText = q.getRaw_text();
		String[] passageWords = p.text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
		int passageWordCount = passageWords.length;
		String passageTitle = p.title;
		String searchEngine = p.engine_name;
		int wordMatch = 0;
		double score = 0;

		if(searchEngine != "bing")
		{
			for(String keyWord : q_words)
			{
				for(String word : passageWords)
				{
					if(keyWord.equals(word))
					{
						wordMatch++;
					}
				}
			}
			score = ((double)wordMatch / passageWordCount) * 1000;
		}
		
		
		System.out.println("Title: " + passageTitle);
		System.out.println("Search Engine: " + searchEngine);
		System.out.println("Score: %" + score);
		System.out.println();
		
		//Bonus 
		if(QType.FACTOID == q.getType())
		{
			if(p.engine_name == "bing")
			{
				score += score * .5;
			}
			//Make bing results higher scored
		}
		
		return 2.0;
	}
}
