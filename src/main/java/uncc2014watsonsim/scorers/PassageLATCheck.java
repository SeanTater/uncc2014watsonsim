package uncc2014watsonsim.scorers;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.nlp.Environment;
import uncc2014watsonsim.nlp.Synonyms;

public class PassageLATCheck extends AnswerScorer {
	private final Synonyms syn;
	private final Dataset rdf;
	private final Logger log = Logger.getLogger(getClass());

	public PassageLATCheck(Environment env) {
		rdf = env.rdf;
		syn = new Synonyms(env);
	}
	public double scoreAnswer(Question q, Answer a) {
		/* We're looking for "<candidate_text> is a <lexical type>"
		 * We can check <lexical type> against q.simple_lat.
		 * 
		 * We only need to find it in one passage, then we can stop.
		 * We can also stop at three or so sentences if it becomes a sore
		 * performance point.
		 */
		
		/* arrows are toward the right
		 * 
		 * nsubj(LAT_W, CAND_W) .
		 * LAT_P = concat $ nn(LAT_W, _)
		 * CAND_P = concat $ nn(CAND_W, _)
		 *
		 * 
		 * 
		 */
		for (Passage p: a.passages) {
			for (SemanticGraph graph : p.graphs) {
				for (IndexedWord word : graph.vertexSet()) {
					// Look for the candidate
					if (word.tag().startsWith("NN")) {
						String candidate_noun = pasteTogetherNoun(graph, word);

						//System.out.println(phrase);
						if (syn.matchViaLevenshtein(
							candidate_noun,
							a.candidate_text)) {
							
							// Found the candidate. Get the LATs.
							for (SemanticGraphEdge edge : graph.incomingEdgeIterable(word)) {
								if (edge.getRelation().getShortName().equals("nsubj")
										&& edge.getGovernor().tag().startsWith("NN")) {
									String lat_noun = pasteTogetherNoun(graph, edge.getGovernor());

									log.info("Discovered " + candidate_noun + " is a(n) " + lat_noun);
								}
							}
						}
					}
				}
			}
		}
		return 0.0;
	}
	/**
	 * Wikipedia probably knows best in how much of the noun to reveal, rather
	 * than just one word. Paste them together along nn semantic edges.
	 * 
	 * Attaching the "prep_of" dependents at the end may also be cool.
	 * We haven't done that yet.
	 * @param graph
	 * @param rightmost
	 * @return
	 */
	private String pasteTogetherNoun(SemanticGraph graph, IndexedWord rightmost) {
		StringBuilder phrase = new StringBuilder();
		for (SemanticGraphEdge edge : graph.outgoingEdgeIterable(rightmost)) {
			if (edge.getRelation().getShortName().equals("nn")) {
				phrase.append(edge.getDependent().originalText());
				phrase.append(' ');
			}
		}
		phrase.append(rightmost.originalText());
		return phrase.toString();
	}
}
