package uncc2014watsonsim.scorers;

import java.util.ArrayList;
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
import edu.stanford.nlp.util.Pair;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Phrase;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.nlp.Environment;
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
	private final Dataset rdf;
	private final Reasoner reasoner;
	private final Logger log = Logger.getLogger(getClass());

	public PassageLATCheck(Environment env) {
		rdf = env.rdf;
		syn = new Synonyms(env);
		reasoner = new GenericRuleReasoner(
				Rule.rulesFromURL("file:src/main/parse.rules"));
	}
	/**
	 * Make a resource to represent a word
	 */
	private Resource wordResource(Model model, IndexedWord word) {
		return model.createResource("urn:sent:" + word.index());
	}
	
	/**
	 * Extract the word from a resource, inverse of wordResource
	 */
	private IndexedWord resourceWord(SemanticGraph graph, Resource res) {
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
	
	private List<Pair<IndexedWord, IndexedWord>> extractTypeDeclarations(SemanticGraph graph) {
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
		List<Pair<IndexedWord,IndexedWord>> names_and_types = new ArrayList<>();
		for (Statement stmt : new IterableIterator<Statement>(iter)) {
			IndexedWord subj_idx = resourceWord(graph, stmt.getSubject());
			IndexedWord obj_idx = resourceWord(graph, stmt.getObject().asResource());
			if (subj_idx.tag().startsWith("NN")
					&& obj_idx.tag().startsWith("NN")) {
				names_and_types.add(new Pair<>(subj_idx, obj_idx));
			}
		}
		return names_and_types;
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
				for (Pair<IndexedWord,IndexedWord> name_and_type : extractTypeDeclarations(graph)) {
					String subj = pasteTogetherNoun(graph, name_and_type.first());
					String obj = pasteTogetherNoun(graph, name_and_type.second());
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
