package edu.uncc.cs.watsonsim.scorers;

import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.util.Pair;
import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.nlp.SupportCandidateType;
import edu.uncc.cs.watsonsim.nlp.Synonyms;

/**
 * Read lexical type definitions from passages. For example
 * "Influenza, or the Flu, is a contagious disease."
 * should result in {
 * 	"Influenza" -> "contageous disease",
 *  "Flu" -> "contageous disease"
 *  }
 */
public class PassageLATCheck extends AnswerScorer {
	private final Synonyms syn;
	private final Logger log = Logger.getLogger(getClass());

	public PassageLATCheck(Environment env) {
		syn = new Synonyms(env);
	}


	/**
	 * We're looking for "<candidate_text> is a <lexical type>"
	 * We can check <lexical type> against q.simple_lat.
	 * 
	 * We only need to find it in one passage, then we can stop.
	 * We can also stop at three or so sentences if it becomes a sore
	 * performance point.
	 */
	public double scoreAnswer(Question q, Answer a) {
		for (String type : a.lexical_types) {
			boolean matches = syn.matchViaLevenshtein(type, q.simple_lat);
			
			log.info("Matching " + type + " against " + q.simple_lat
					+ (matches ? " succeeds." : " fails."));
			if (matches) {
				return 1.0;
			}
		}
		return 0.0;
	}
}
