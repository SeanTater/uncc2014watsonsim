package uncc2014watsonsim;

import java.util.Optional;

/** 
 * A Passage that may not have any actual text.
 * 
 * It needs to be dereferenced in order to run scorers and such on it.
 * The purpose of this class is to have a compile-time way of expressing
 * that a passage needs to be further analyzed. (Such as loading a document
 * entry or loading a URL.)
 * @author Sean Gallagher
 *
 */
public class PassageRef {
	public final String reference;
	public final Optional<String> text;
	public final String engine_name;
	public final Optional<String> title;

	public PassageRef(String reference, String engine_name,
			Optional<String> title,
			Optional<String> text) {
		this.reference = reference;
		this.text = text;
		this.engine_name = engine_name;
		this.title = title;
	}
}
