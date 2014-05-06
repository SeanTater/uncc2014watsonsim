/**
 * @author Jacob Medd
 */
package uncc2014watsonsim.anagramPipeline;

import java.util.ArrayList;

public class WordNode implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3788718429575096246L;
	private String value;
	private ArrayList<WordNode> children;
	
	public WordNode() {
		children = new ArrayList<WordNode>();
	}
	
	public WordNode(String value) {
		this();
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void addChild(WordNode child) {
		children.add(child);
	}
	
	public WordNode[] getChildren() {
		return children.toArray(new WordNode[0]);
	}
	
	/**
	 * @author Jacob Medd
	 * @param value The value that the child should have
	 * Returns the first child with the given value. If there isn't a match, then the 
	 * method will return null
	 */
	public WordNode getChildWithValue(String value) {
		WordNode result = null;
		for (int i = 0; i < children.size(); i ++) {
			if (children.get(i).getValue().equals(value)) {
				result = children.get(i);
				break;
			}
		}
		
		return result;
	}
}
