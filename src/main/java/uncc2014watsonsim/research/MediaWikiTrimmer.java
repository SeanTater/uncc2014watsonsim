package uncc2014watsonsim.research;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

/**
 * Clean MediaWiki syntax with a few Regexes
 * This should probably be done with annotations but that has not been worked
 * out just yet.
 * @author Sean
 *
 */
public class MediaWikiTrimmer extends Researcher {

	@Override
	public void passage(Question q, Answer a, Passage p) {
		// TODO: This should annotate instead of mutate really
		// Get rid of citations and images
		p.text = p.text.replaceAll("\\{\\{[^}]+\\}\\}", "");
		// Take the only field of a [[Link]] or the last field of a [[http://example|Special Link]]
		p.text = p.text.replaceAll("\\[\\[([^|\\]]+\\|)*([^|\\]]+)\\]\\]", "$2");
		// Get rid of formatting (plain text)
		p.text = p.text.replace("''", "").replace("===", "").replace("==", "");
		// Get rid of references
		p.text = p.text.replaceAll("<ref( name=(\"[^\"]+\"|\\w+))?>(([^<]|\n)*)</ref>", "");
	}

}
