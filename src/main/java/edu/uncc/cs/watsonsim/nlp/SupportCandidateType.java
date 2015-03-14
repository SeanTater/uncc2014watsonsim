package edu.uncc.cs.watsonsim.nlp;

import java.util.ArrayList;
import java.util.List;

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

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.IterableIterator;
import edu.stanford.nlp.util.Pair;
import edu.uncc.cs.watsonsim.Phrase;

public class SupportCandidateType {

	private static final Reasoner reasoner = new GenericRuleReasoner(
			Rule.rulesFromURL("file:src/main/parse.rules"));
	
	/**
	 * Make a resource to represent a word
	 */
	private static Resource wordResource(Model model, IndexedWord word) {
		return model.createResource("urn:sent:" + word.index());
	}
	
	/**
	 * Extract the word from a resource, inverse of wordResource
	 */
	private static IndexedWord resourceWord(SemanticGraph graph, Resource res) {
		// Turn urn:stmt:digits into digits as int
		int id= Integer.parseInt(res.getURI().split(":")[2]);
		return graph.getNodeByIndex(id);
	}

	/**
	 * The bare one-word nouns are usually not very good.
	 * "group", "set" and such are especially bad as they are basically just
	 * container types: "group of diseases", "set of rules"
	 * So we concat a few kinds of links to nouns.
	 */
	public static String concatNoun(SemanticGraph graph, IndexedWord rightmost) {
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

	/**
	 * Find simple statements of type in regular text, such as "Diabetes is a
	 * common disease"
	 * 
	 * Subclasses are very similarly stated, such as "A hummingbird is a kind
	 * of bird." But we don't distinguish between these yet. We should though.
	 * 
	 * @return Pairs of nouns and their types.
	 */
	public static List<Pair<String, String>> extract(Phrase p) {
		List<Pair<String, String>> names_and_types = new ArrayList<>();
		for (SemanticGraph graph: p.graphs){
			// Load data into a model
			Model model = ModelFactory.createMemModelMaker().createFreshModel();
			
			// Add all the edges
			for (SemanticGraphEdge edge : graph.edgeIterable()) {
				model.add(
						wordResource(model, edge.getGovernor()),
						model.createProperty("urn:sent:" + edge.getRelation().getShortName()),
						wordResource(model, edge.getDependent()));
			}
			// Index the words
			Property tag_property = model.createProperty("urn:sent:tag"); 
			for (IndexedWord word : graph.vertexSet()) {
				model.add(
						wordResource(model, word),
						tag_property,
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
				IndexedWord subj_idx = resourceWord(graph, stmt.getSubject());
				IndexedWord obj_idx = resourceWord(graph, stmt.getObject().asResource());
				if (subj_idx.tag().startsWith("NN")
						&& obj_idx.tag().startsWith("NN")) {
					names_and_types.add(new Pair<>(
							concatNoun(graph, subj_idx),
							concatNoun(graph, obj_idx)));
				}
			}
		}
		return names_and_types;
	}
}
