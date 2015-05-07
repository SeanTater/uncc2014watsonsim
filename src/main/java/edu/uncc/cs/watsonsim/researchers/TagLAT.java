package edu.uncc.cs.watsonsim.researchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import edu.stanford.nlp.util.Pair;
import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.nlp.DBPediaCandidateType;
import edu.uncc.cs.watsonsim.nlp.SupportCandidateType;
import edu.uncc.cs.watsonsim.nlp.Synonyms;


public class TagLAT extends Researcher {
	private final DBPediaCandidateType dbpedia;
	private final Synonyms syn;
	
	public TagLAT(Environment env) {
		dbpedia = new DBPediaCandidateType(env);
		syn = new Synonyms(env);
	}
	
	public List<Answer> pull(Question q, List<Answer> answers) {
		return pull(q, answers, 0);
	}
	
	public List<Answer> pull(Question q, List<Answer> answers, int depth) {
		return question(q, chain.pull(q, answers), depth);
	}
	
	
	/**
	 * Find the possible lexical types of a candidate, and label the answer.
	 */
	public List<Answer> question(Question q, List<Answer> answers, int depth) {
		int have_any_types = 0;
		
		int dbpedia_types = 0;
		int support_types = 0;
		
		List<Answer> suggestions = new ArrayList<>();
		
		for (Answer a: answers) {
			
			// Handle DBPedia types
			
			a.lexical_types = dbpedia.viaDBPedia(a.text);
			for (String type: a.lexical_types) {
				a.log(this, "DBPedia says it's a %s", type);
			}
			if (a.lexical_types.isEmpty())
				a.log(this, "DBPedia has no type information for it.");
			dbpedia_types += a.lexical_types.size(); 
			
			// Handle Support types
			
			for (Passage p: a.passages) {
				List<Pair<String, String>> types = p.memo(SupportCandidateType::extract);
				for (Pair<String, String> name_and_type : types) {
					String name = name_and_type.first;
					String type = name_and_type.second;
					if (syn.matchViaSearch(name, a.text)) {
						a.log(this, "Passage %s says it's a %s.", p.reference, type);
						a.lexical_types.add(type);
						support_types++;
					} else if (syn.matchViaSearch(type, q.simple_lat)) {
						Answer suggestion = new Answer(name);
						suggestion.lexical_types = Arrays.asList(type);
						suggestion.log(this, "Found it's a %s, while reading about %s in %s", type, a, p.reference);
						if (!(suggestions.contains(suggestion)
								|| answers.contains(suggestion))) {
							log.info("Suggesting " + name);
							suggestions.add(suggestion);
						}
						
					}
				}
			}
			if (!a.lexical_types.isEmpty()) have_any_types++;
		}
		
		// This is the chain magic:
		// We can pull the new suggestions through the pipeline and merge them!
		List<Answer> new_answers = new ArrayList<>();
		if (!suggestions.isEmpty() && depth < 3)
			new_answers.addAll(pull(q, suggestions, depth+1));
		new_answers.addAll(answers);
		

		//System.out.println(text + " could be any of " + types);
		log.info("Found " + (dbpedia_types+support_types) + " types for "
				+ have_any_types + " candidates. ("+ support_types +" by reading) "
				+ (answers.size() - have_any_types) + " candidates are unknown.");
		return new_answers;
	}

}

