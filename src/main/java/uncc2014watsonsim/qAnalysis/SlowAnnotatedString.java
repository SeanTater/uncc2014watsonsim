package uncc2014watsonsim.qAnalysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Annotated semi-UIMA style string wrapper enabling tagging substrings.
 * This method has a simple implementation but may waste memory.
 * 
 * The intended usage is something like this:
 * <pre>
 * static tag = astr.register("Ultra-cool tagger");
 * ...
 * add(tag, 7, 18);
 * add(tag, 0, 38);
 * add(tag, 10, 20);
 * addRegex(tag, "\d+");
 * remove(tag, 7, 18);
 * removeRegex(tag, "\d{3,5}");
 * ...
 * getStrings(tag) --> List
 * </pre>
 * Find the original string at {@link raw}.<p>
 * 
 * @author Sean Gallagher
 */
public class SlowAnnotatedString implements AnnotatedString {
	/** The raw original, immutable String. */
	public final String raw;
	
	/** The number of flag types registered */
	static int flag_type_count = 0;
	
	public Multimap<Integer, Span> notes = HashMultimap.create();
	
	/** Create a new AnnotatedString. */
	public SlowAnnotatedString(String raw) {
		this.raw = raw;
	}
	
	/* (non-Javadoc)
	 * @see uncc2014watsonsim.qAnalysis.AnnotatedString#register(java.lang.String)
	 */
	@Override
	public int register(String name) {
		/* TODO: Right now we don't do anything with the name. But it sounds
		 * like we probably should. So let's make it part of the API.
		 */
		flag_type_count++;
		return flag_type_count - 1;
	}
	
	
	/* (non-Javadoc)
	 * @see uncc2014watsonsim.qAnalysis.AnnotatedString#add(int, int, int)
	 */
	@Override
	public void add(int tag, int start, int end) {
		notes.put(tag, new Span(start, end));
	}
	
	/* (non-Javadoc)
	 * @see uncc2014watsonsim.qAnalysis.AnnotatedString#remove(int, int, int)
	 */
	@Override
	public void remove(int tag, int start, int end) {
		notes.remove(tag, new Span(start, end));
	}
	
	/* (non-Javadoc)
	 * @see uncc2014watsonsim.qAnalysis.AnnotatedString#addRegex(int, java.lang.String)
	 */
	@Override
	public void addRegex(int tag, String regex) {
		// TODO: Think about caching the patterns.
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(raw);
		while (m.find()) {
			add(tag, m.start(), m.end());
		}
	}
	
	/* (non-Javadoc)
	 * @see uncc2014watsonsim.qAnalysis.AnnotatedString#removeRegex(int, java.lang.String)
	 */
	@Override
	public void removeRegex(int tag, String regex) {
		// TODO: Think about caching the patterns.
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(raw);
		while (m.find()) {
			remove(tag, m.start(), m.end());
		}
	}
	
	/* (non-Javadoc)
	 * @see uncc2014watsonsim.qAnalysis.AnnotatedString#getSpans(int)
	 */
	@Override
	public Collection<Span> getSpans(int tag) {
		return notes.get(tag);
	}
	
	/* (non-Javadoc)
	 * @see uncc2014watsonsim.qAnalysis.AnnotatedString#getStrings(int)
	 */
	@Override
	public Collection<String> getStrings(int tag) {
		List<String> strings = new ArrayList<>();
		for (Span n : notes.get(tag)) {
			strings.add(raw.substring(n.start, n.end));
		}
		return strings;
	}
}

class Span {
	public int start;
	public int end;
	
	public Span(int start, int end) {
		this.start = start;
		this.end = end;
	}
}
