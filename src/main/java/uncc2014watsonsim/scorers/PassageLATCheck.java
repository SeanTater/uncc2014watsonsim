package uncc2014watsonsim.scorers;

import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.IterableIterator;
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

		// Rules
		String ruleSrc = "[(?noun_r urn:sent:type ?type_r) <- "	// Joan is an actress.
				+ "(?type_r urn:sent:nsubj ?noun_r),"			// JOAN is an ACTRESS.
				+ "(?type_r urn:sent:det ?det),"				// Joan is AN ACTRESS.
				+ "(?type_r urn:sent:cop ?cop),"				// Joan IS an ACTRESS.
				+ "(?type_r urn:sent:tag urn:sent:NN),"			// Joan is an ACTRESS
				//+ "(?type_r urn:sent:idx ?type_idx),"
				//+ "(?noun_r urn:sent:idx ?noun_idx)"
				+ "]";
		List<Rule> rules = Rule.rulesFromURL("file:src/main/parse.rules");
		//List<Rule> rules = Rule.parseRules(ruleSrc);
		Reasoner reasoner = new GenericRuleReasoner(rules);
		
		for (Passage p: a.passages) {
			for (SemanticGraph graph : p.graphs) {
				
				// Load data into a model
				Model model = ModelFactory.createMemModelMaker().createFreshModel();
				
				// Add all the edges
				for (SemanticGraphEdge edge : graph.edgeIterable()) {
					model.add(
							model.createResource("urn:sent:" + edge.getGovernor().index()),
							model.createProperty("urn:sent:" + edge.getRelation().getShortName()),
							model.createResource("urn:sent:" + edge.getDependent().index()));
				}
				// Index the words
				for (IndexedWord word : graph.vertexSet()) {
					model.add(
							model.createResource("urn:sent:" + word.index()),
							model.createProperty("urn:sent:tag"),
							model.createResource("urn:sent:" + word.tag()));
				}
				// Create an inference model
				InfModel infmodel = ModelFactory.createInfModel(reasoner, model);
				// Query the model
				StmtIterator iter = infmodel.listStatements(
						(Resource)null,
						model.createProperty("urn:sent:type"),
						(RDFNode)null);
				// Get the resulting matches
				for (Statement stmt : new IterableIterator<Statement>(iter)) {
					int subj_id= Integer.parseInt(stmt.getSubject().asResource().getURI().split(":")[2]);
					int obj_id= Integer.parseInt(stmt.getObject().asResource().getURI().split(":")[2]);
					IndexedWord subj = graph.getNodeByIndex(subj_id);
					IndexedWord obj = graph.getNodeByIndex(obj_id);
					if (subj.tag().startsWith("NN")
							&& obj.tag().startsWith("NN")) {
						log.info("Discovered (method b)" + pasteTogetherNoun(graph, subj)
								+ " is a(n) " + pasteTogetherNoun(graph, obj));
					}
					
				}
				
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
			switch (edge.getRelation().getShortName()) {
			case "nn":
				phrase.append(edge.getDependent().originalText());
				phrase.append(' ');
				break;
			case "prep_of":
				phrase.append("of ");
				phrase.append(edge.getDependent().originalText());
				phrase.append(' ');
				break;
			}
		}
		phrase.append(rightmost.originalText());
		return phrase.toString();
	}
}
