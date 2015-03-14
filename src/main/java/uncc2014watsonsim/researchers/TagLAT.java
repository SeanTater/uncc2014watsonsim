package uncc2014watsonsim.researchers;

import org.apache.log4j.Logger;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Environment;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.nlp.ClueType;


public class TagLAT extends Researcher {
	private final ClueType backend;
	private final Logger log = Logger.getLogger(this.getClass());
	
	public TagLAT(Environment env) {
		backend = new ClueType(env);
	}
	/**
	 * Find the possible lexical types of a candidate, and label the answer.
	 */
	@Override
	public void question(Question q) {
		int have_any_types = 0;
		int total_types = 0;
		for (Answer a: q) {
			a.lexical_types = backend.fromCandidate(a.candidate_text);
			if (a.lexical_types.size() > 1) have_any_types++;
			total_types += a.lexical_types.size(); 
		}

		//System.out.println(text + " could be any of " + types);
		log.info("Found " + total_types + " DBPedia types for "
				+ have_any_types + " candidates. "
				+ (q.size() - have_any_types) + " candidates are unknown.");
	}

}

