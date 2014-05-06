package uncc2014watsonsim.anagramPipeline.wordtree;

import java.util.ArrayList;
import java.util.HashSet;

public class AnagramSolver {
	private WordTree wordTree;
	
	public AnagramSolver() { };
	
	public WordTree getWordTree() {
		return wordTree;
	}
	
	public void setWordTree(WordTree wordTree) {
		this.wordTree = wordTree;
	}
	
	public String[] generateCandidateSolutions(String phrase) {
		return generateCandidateHelper(wordTree.getRoot(), phrase, "");
	}
	
	private String[] generateCandidateHelper(WordNode currentNode, String partialPhrase, String partialSolution) {
		ArrayList<String> solutions = new ArrayList<String>();
		if (partialPhrase.equals("")) {
			if (partialSolution.length() > 1) {
				if (currentNode.getChildWithValue("=") != null) {
					solutions.add(partialSolution);
				}
			}
		}
		else if (partialSolution.isEmpty() && partialPhrase.length() <= wordTree.getSmallWordLimit()) {
			String[] possiblities = generatePermutations(partialPhrase);
			for (String str : possiblities) {
				if (wordTree.containsWord(str)) {
					solutions.add(str);
				}
			}
		}
		else {
			/**
			 * If the current node has a child node with the value =, that means the current partial solution
			 * might just be a word in a phrase which is a final solution. To check for this possibility,
			 * run generateCandidateHelper with the rest of partialPhrase starting at the top to find the entire phrase
			 */
			if (currentNode.getChildWithValue("=") != null) {
				String[] subSolutions = generateCandidateHelper(wordTree.getRoot(), partialPhrase, "");
				if (subSolutions != null) {
					for (String s : subSolutions) {
						solutions.add(partialSolution + " " + s);
					}
				}
			}
			/**
			 * steps:
			 * 1.) find the unique characters in the partialPhrase
			 * 2.) for each unique character, check to see if the current node has a child whose value is one of the unique characters
			 * 3.) if it does, then call generateCandidateHelper(child node with value, partial phrase with the character removed,
			 * 			partialSolution with the character added)
			 * 4.) if generateCandidateHelper did not return null, add returned values to the list of solutions
			 */
			String[] uniqueCharSet = getUniqueCharacters(partialPhrase);
			for (String c : uniqueCharSet) {
				WordNode childNode = currentNode.getChildWithValue(c);
				if (childNode != null) {
					int charIndex = partialPhrase.indexOf(c);
					StringBuilder strBuilder = new StringBuilder(partialPhrase);
					String[] returnedValues = generateCandidateHelper(childNode, strBuilder.deleteCharAt(charIndex).toString(), partialSolution + c);
					
					if (returnedValues != null) {
						for (String s : returnedValues) {
							solutions.add(s);
						}
					}
				}
			}
		}
		
		if (solutions.isEmpty())
			return null;
		else
			return solutions.toArray(new String[0]);
	}
	
	/**
	 * Find the list of unique characters for the phrase (returned as strings for convenience)
	 * @param phrase 
	 * @return an array of unique one character strings
	 */
	private String[] getUniqueCharacters(String phrase) {
		HashSet<String> charSet = new HashSet<String>(phrase.length());
		
		for (int i = 0; i < phrase.length(); i++) {
			charSet.add(phrase.substring(i, i+1));
		}
		
		return charSet.toArray(new String[0]);
	}
	
	private String[] generatePermutations(String chars) {
		ArrayList<String> returnedValues = new ArrayList<String>();
		if (chars.length() == 1) {
			return new String[] {chars};
		}
		else {
			String currentChar = chars.substring(0,1);
			String[] previousResults = generatePermutations(chars.substring(1));
			for (String str : previousResults) {
				for (int i = 0; i < str.length()+1; i++) {
					returnedValues.add(str.substring(0,i) + currentChar + str.substring(i, str.length()));
				}
			}
			return returnedValues.toArray(new String[0]);
		}
	}
}
