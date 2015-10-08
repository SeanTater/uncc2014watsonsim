package edu.uncc.cs.watsonsim.nlp;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.CharSetUtils;
import org.apache.log4j.Logger;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Theory;
import alice.tuprolog.UnknownVarException;

import com.google.common.io.Files;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.Pair;
import edu.uncc.cs.watsonsim.Phrase;

public class SupportCandidateType {

	private static final Logger log = Logger.getLogger(SupportCandidateType.class);
	
	private static final Prolog static_engine = new Prolog();
	static {
		try {
			static_engine.setTheory(new Theory(
					Files.toString(new File("src/main/parse.pl"), Charset.forName("UTF-8"))));
		} catch (InvalidTheoryException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String clean(String text) {
        return CharSetUtils.keep(text.toLowerCase(), "abcdefghijklmnopqrstuvwxyz_");
	}
	
	private static String wordID(IndexedWord word) {
		return "w" + clean(word.word())  + "_" + word.index();
	}
	
	private static IndexedWord idWord(SemanticGraph graph, String id) {
		int idx = Integer.parseInt(id.substring(id.lastIndexOf('_')+1));
		return graph.getNodeByIndex(idx);
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
		for (SemanticGraph graph: p.getGraphs()){
			StringBuilder theory = new StringBuilder();
			// Load data into a model
			
			// Add all the edges
			for (SemanticGraphEdge edge : graph.edgeIterable()) {
				// I like the specific prepositions better
				// so change them to match
				GrammaticalRelation rel = edge.getRelation();
				String relation_name = rel.getShortName();
				if ( (rel.getShortName().equals("prep")
						|| rel.getShortName().equals("conj"))
						&& rel.getSpecific() != null
						&& !rel.getSpecific().isEmpty()) {
					relation_name = rel.getShortName() + "_" + CharSetUtils.keep(rel.getSpecific().toLowerCase(), "abcdefghijklmnopqrstuvwxyz");
				}
				theory.append(relation_name);
				theory.append('(');
				theory.append(wordID(edge.getGovernor()));
				theory.append(',');
				theory.append(wordID(edge.getDependent()));
				theory.append(").\n");
			}
			// Index the words
			for (IndexedWord word : graph.vertexSet()) {
				theory.append("tag(");
				theory.append(wordID(word));
				theory.append(',');
				String tag = clean(word.tag());
				theory.append(tag.isEmpty() ? "misc" : tag);
				theory.append(").\n");
			}

			Prolog engine = new Prolog();
			try {
				engine.setTheory(new Theory(
						Files.toString(new File("src/main/parse.pl"), Charset.forName("UTF-8"))));
				log.debug(theory);
				engine.addTheory(new Theory(theory.toString()));
				
				SolveInfo info = engine.solve("type_c(X, Y).");

				// Get the resulting matches
				while (info.isSuccess()) {
					IndexedWord subj_idx = idWord(graph, info.getTerm("X").toString());
					IndexedWord obj_idx = idWord(graph, info.getTerm("Y").toString());
					if (subj_idx.tag().startsWith("NN")
							&& obj_idx.tag().startsWith("NN")) {
						String noun = Trees.concatNoun(graph, subj_idx);
						String type = obj_idx.originalText(); //concatNoun(graph, obj_idx);
						log.info("Discovered " + noun + " is a(n) " + type);
						names_and_types.add(new Pair<>(noun,type));
					}
					if (engine.hasOpenAlternatives()) {
						info = engine.solveNext();
					} else {
						break;
					}
				}
				
			} catch (IOException | InvalidTheoryException
					| MalformedGoalException | NoSolutionException
					| NoMoreSolutionException | UnknownVarException e) {
                System.out.println(theory);
				e.printStackTrace();
			}
		}
		return names_and_types;
	}
}
