package edu.uncc.cs.watsonsim.scorers;

import java.util.List;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.StringUtils;

/**
 * The Passage Term match scorer is designed, simply, to count the number of times
 * a term appears in the text.
 * 
 * "This assigns a score by
 *	matching question terms to passage terms, regardless
 *	of grammatical relationship or word order."
 *
 * It returns a number which is equal to the number of occurrences
 * @author Jonathan Shuman
 *
 */
public class PassageTermMatch extends PassageScorer { 
	public double scorePassage(Phrase q, Answer a, Passage p) {
		
		// Jane Austen
		String c_t = StringUtils.join(p.text, " ");
		
		// Romantic novelist Jane Austen once wrote -the- book Emma.
		String q_t = q.text;
		
		return generateNumberTerms(q_t, c_t);
		
	}
	
	/**
	 * @param queryText The text of the query to search passages
	 * @param passageText The text of the passage
	 * @return Number of occurrences of words in query in the passage
	 */
	private int generateNumberTerms(String queryText, String passageText) {
		/*
		 * We will first separate the text of the query and passage into terms.
		 * Note: The parameters are assumed to have stopwords removed.
		 */
		List<String> qTerms = StringUtils.tokenize(queryText);
		List<String> pTerms = StringUtils.tokenize(passageText);
		
		// Join the passage back together with stop words removed. 
		// We will use the StringUtils function to remove the words.
		String passageStopsRemoved = StringUtils.join(pTerms, " ");
		
		int matches = 0;
		//Scan through each of the terms to get its number of occurances in the passage text.
		for (String term : qTerms) {
			// First the bigram
			matches += StringUtils.countMatches(passageStopsRemoved, term);
		}
		return matches;
	}

}
