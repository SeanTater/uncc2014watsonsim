package uncc2014watsonsim.scorers;

import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.util.Pair;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Environment;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Phrase;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.nlp.SupportCandidateType;
import uncc2014watsonsim.nlp.Synonyms;

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
		for (Passage p: a.passages) {
			for (SemanticGraph graph : p.graphs) {
				for (Pair<IndexedWord,IndexedWord> name_and_type : SupportCandidateType.extractTypeDeclarations(graph)) {
					String subj = SupportCandidateType.concatNoun(graph, name_and_type.first());
					String obj = SupportCandidateType.concatNoun(graph, name_and_type.second());
					log.info("Discovered " + subj + " is a(n) " + obj);
					
					if (syn.matchViaLevenshtein(subj, a.candidate_text)) {
						a.lexical_types.add(obj);
					} else if (syn.matchViaLevenshtein(obj, q.simple_lat)) {
						log.info("Let's examine " + subj 
								+ " since it's a(n) " + obj);
					}
				}
			}
		}
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
