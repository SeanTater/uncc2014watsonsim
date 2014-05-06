package uncc2014watsonsim.qAnalysis;

import java.util.Collection;

public interface AnnotatedString {

	/**
	 * Register a tag. Only do this once for each type of tag you want.
	 * It returns the tag index, which you can later use for add() and friends.
	 * <p>
	 * The whole reason for going this route is that:
	 * 1: It's incredibly cheap.
	 * 2: It allows you to use the open-closed principle.
	 * 
	 */
	public abstract int register(String name);

	/** Add a tag. This is the most popular method.
	 * 
	 * It's added to an unsorted array, and you can have multiple copies of the
	 * exact same tag.
	 * 
	 * If you get an ArrayIndexOutOfBoundsException, check your tag.
	 * 
	 * @param tag  The tag you registered with register().
	 * @param start  The start of the region, inclusive.
	 * @param end   The end of the region, exclusive.
	 */
	public abstract void add(int tag, int start, int end);

	/**
	 * Remove a tag.
	 * Removal is slow and involves a linear search. Avoid it if you can.
	 * Parameters are the same as add().
	 * 
	 * @param tag  The tag you registered with register().
	 * @param start  The start of the region, inclusive.
	 * @param end   The end of the region, exclusive.
	 */
	public abstract void remove(int tag, int start, int end);

	/**
	 * Add all the results of a Regex using add().
	 * 
	 * This is just a short convenience method.
	 * 
	 * @param tag  The tag you registered with register().
	 * @param regex  The string Regex to match on.
	 */
	public abstract void addRegex(int tag, String regex);

	/**
	 * Remove all the results of a Regex using remove().
	 * 
	 * @param tag  The tag you registered with register().
	 * @param regex  The string Regex to match on.
	 */
	public abstract void removeRegex(int tag, String regex);

	/**
	 * Get all annotations of one tag type as Spans
	 */
	public abstract Collection<Span> getSpans(int tag);

	/**
	 * Get all annotations of one tag type as Strings.
	 */
	public abstract Collection<String> getStrings(int tag);

}