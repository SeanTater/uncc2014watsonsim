package uncc2014watsonsim.anagramPipeline.wordtree;

import java.util.ArrayList;
import java.util.HashMap;

public class WordTree implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7589483329747333951L;
	private WordNode root;
	private ArrayList<WordNode> nodes = new ArrayList<WordNode>();
	private HashMap<String, String> smallWordMap = new HashMap<String, String>();
	private int smallWordLimit = 3;
	private boolean useSmallWordHash = true;
	
	public WordTree() {
		root = new WordNode("~");
	}
	
	public WordTree(boolean useSmallWordHash) {
		root = new WordNode("~");
		this.useSmallWordHash = useSmallWordHash;
	}
	
	public WordTree(WordNode root, boolean useSmallWordHash) {
		this.root = root;
		this.useSmallWordHash = useSmallWordHash;
	}
	
	/**
	 * adds a word to the tree if it doesn't already contain it
	 * @author Jacob Medd
	 * @param word
	 */
	public void addWord(String passedWord) {
		String word = passedWord.toLowerCase();
		if (containsWord(word)) { return; }
		if (useSmallWordHash && word.length() <= smallWordLimit) {
			smallWordMap.put(word, word);
		}
		else
			addWordHelper(word+"=", root);
	}
	
	/**
	 * adds an array of words to the tree
	 * @param words
	 */
	public void addWords(String[] words) {
		for (String s : words) {
			addWord(s);
		}
	}
	
	private void addWordHelper(String word, WordNode currentNode) {
		if (word.equals("")) { return; }
		
		String currentChar = Character.toString(word.charAt(0));
		WordNode nextNode = currentNode.getChildWithValue(currentChar);
		if (nextNode != null) {
			addWordHelper(word.substring(1), nextNode);
		}
		else
		{
			WordNode newChild = new WordNode(currentChar);
			currentNode.addChild(newChild);
			nodes.add(newChild);
			addWordHelper(word.substring(1), newChild);
		}
	}
	
	/**
	 * @author Jacob Medd
	 * @param word the word to be tested
	 * @return true if the word tree contains the word
	 */
	public boolean containsWord(String word) {
		String tempWord = word.toLowerCase();
		if (useSmallWordHash && tempWord.length() <= smallWordLimit) {
			return smallWordMap.get(tempWord) != null;
		}
		else
			containsWordHelper(word+"=", root);
		return false;
	}
	
	private boolean containsWordHelper(String word, WordNode currentNode) {
		String currentChar = Character.toString(word.charAt(0));
		WordNode nextNode = null;
		if (currentChar.equals("=")) {
			if (currentNode.getChildWithValue("=") != null) {
				return true;
			}
			else
				return false;
		}
		
		nextNode = currentNode.getChildWithValue(currentChar);
		if (nextNode == null)
			return false;
		else
			return containsWordHelper(word.substring(1), nextNode);
	}
	
	public WordNode getRoot() {
		return root;
	}
	
	/**
	 * Sets the root node. Warning: the root node's value should not be a part of the word being 
	 * searched for. By default, the root node's value is ~
	 * @author Jacob Medd
	 * @param node
	 */
	public void setRoot(WordNode node) {
		root = node;
	}
	
	/**
	 * Returns the maximum number of characters a word has to be to be considered small
	 * @return
	 */
	public int getSmallWordLimit() {
		return smallWordLimit;
	}
	
	/**
	 * @author Jacob Medd
	 * @param value - the value that the child node should have
	 * @return a subtree of the current tree whose root is the immediate child with the value
	 * matching the passed value. Returns null if there is no child with the value 
	 */
	/**
	public WordTree getChildWordTree(String value) {
		WordTree result = null;
		WordNode nextNode = root.getChildWithValue(value);
		if (nextNode != null) {
			result = new WordTree(nextNode);
		}
		
		return result;
	}*/
}
