package uncc2014watsonsim.researchers;

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
		p.setText(
			p.getText()
			.replaceAll("\\{\\{[^}]+\\}\\}", "")
			.replaceAll("\\[\\[([^|\\]]+\\|)*([^|\\]]+)\\]\\]", "$2")
			.replaceAll("<ref( name=(\"[^\"]+\"|\\w+))?>[^<]*</ref>", "")
			.replace("''", "")
			.replace("===", "")
			.replace("==", "")
		);
	}

}
