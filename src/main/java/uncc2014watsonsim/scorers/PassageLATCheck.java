package uncc2014watsonsim.scorers;

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
		for (Passage p: a.passages) {
			for (SemanticGraph graph : p.graphs) {
				for (IndexedWord word : graph.vertexSet()) {
					
					if (word.tag().startsWith("NN")) {
						String phrase = pasteTogetherNoun(graph, word);

						//System.out.println(phrase);
						if (syn.matchViaLevenshtein(
							phrase,
							a.candidate_text)) {
							System.out.println("!!" + phrase);
						}
					}
					
				}
				/*for (SemanticGraphEdge edge : graph.edgeIterable()) {
					if (edge.getRelation().getShortName() == "nsubj"
							// && syn.matchViaLevenshtein(
							//		edge.getDependent().originalText(),
							//		a.candidate_text)) {
							){

						System.out.println("maybe "+ edge.getDependent().originalText() +" is a " + edge.getGovernor().originalText());
						System.out.println(graph);
					}
						
				}*/
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
			if (edge.getRelation().getShortName() == "nn") {
				phrase.append(edge.getDependent().originalText());
				phrase.append(' ');
			}
		}
		phrase.append(rightmost.originalText());
		return phrase.toString();
	}
}
