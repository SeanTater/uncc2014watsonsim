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
		List<Rule> rules = Rule.rulesFromURL("file:src/main/parse.rules");
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
						log.info("Discovered " + pasteTogetherNoun(graph, subj)
								+ " is a(n) " + pasteTogetherNoun(graph, obj));
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
			case "cd":
			case "amod":
				phrase.append(edge.getDependent().originalText());
				phrase.append(' ');
				break;
			case "prep":
				if (edge.getRelation().getSpecific().equals("of")) {
					phrase.append(edge.getDependent().originalText());
					phrase.append(' ');
				}
				break;
			}
		}
		phrase.append(rightmost.originalText());
		return phrase.toString();
	}
}
