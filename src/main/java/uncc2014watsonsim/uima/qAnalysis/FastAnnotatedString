package uncc2014watsonsim.qAnalysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.mahout.math.Arrays;

/**
 * Annotated semi-UIMA style string wrapper enabling tagging substrings.
 * This was added to the UIMA qAnalysis
 * @author CHris Stephenson
 *
 */
public class FastAnnotatedString {
	/** The raw original, immutable String. */
	public final String raw;
	
	/** The number of flag types registered */
	static int flag_type_count = 0;
	
	/** Start and end regions for every tag */
	int[][] starts = new int[flag_type_count][16];
	int[][] ends = new int[flag_type_count][16];
	int[] fills = new int[flag_type_count];
	
	/** Create a new AnnotatedString. */
	public FastAnnotatedString(String raw) {
		this.raw = raw;
	}
	
	/**
	 * Register a tag. Only do this once for each type of tag you want.
	 * It returns the tag index, which you can later use for add() and friends.
	 * <p>
	 * The whole reason for going this route is that:
	 * 1: It's incredibly cheap.
	 * 2: It allows you to use the open-closed principle.
	 * 
	 */
	public int register(String name) {
		/* TODO: Right now we don't do anything with the name. But it sounds
		 * like we probably should. So let's make it part of the API.
		 */
		flag_type_count++;
		return flag_type_count - 1;
	}
	
	
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
	public void add(int tag, int start, int end) {
		int id = fills[tag];
		fills[tag] += 1;
		int fill = fills[tag];
		// Note: This Arrays function comes from Mahout.
		starts[tag] = Arrays.ensureCapacity(starts[tag], fill);
		ends[tag] = Arrays.ensureCapacity(ends[tag], fill);
		starts[tag][id] = start;
		ends[tag][id] = end;
	}
	
	/**
	 * Remove a tag.
	 * Removal is slow and involves a linear search. Avoid it if you can.
	 * Parameters are the same as add().
	 * 
	 * @param tag  The tag you registered with register().
	 * @param start  The start of the region, inclusive.
	 * @param end   The end of the region, exclusive.
	 */
	public void remove(int tag, int start, int end) {
	}
	
	/**
	 * Add all the results of a Regex.
	 */
	public void addRegex(int tag, String regex) {
		// TODO: Think about caching the patterns.
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(raw);
		while (m.find()) {
			add(tag, m.start(), m.end());
		}
	}
	
	/**
	 * Create annotations as needed for the Question's QType
	 */
	public void createAnnotations() {
		
	}
	
}
