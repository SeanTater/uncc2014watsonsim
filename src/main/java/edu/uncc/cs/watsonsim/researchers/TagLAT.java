package edu.uncc.cs.watsonsim.researchers;

import java.util.List;

import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.util.Pair;
import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.nlp.ClueType;
import edu.uncc.cs.watsonsim.nlp.DBPediaCandidateType;
import edu.uncc.cs.watsonsim.nlp.SupportCandidateType;


public class TagLAT extends Researcher {
	private final DBPediaCandidateType dbpedia;
	private final Logger log = Logger.getLogger(this.getClass());
	
	public TagLAT(Environment env) {
		dbpedia = new DBPediaCandidateType(env);
	}
	/**
	 * Find the possible lexical types of a candidate, and label the answer.
	 */
	@Override
	public void question(Question q) {
		int have_any_types = 0;
		int total_types = 0;
		
		for (Answer a: q) {
			a.lexical_types = dbpedia.viaDBPedia(a.candidate_text);
			for (Phrase p: a.passages) {
				List<Pair<IndexedWord, IndexedWord>> types = p.memo(SupportCandidateType::extract);
				for (Pair<IndexedWord, IndexedWord> name_and_type : types) {
					log.info("Looks like " + name_and_type.first.originalText() +
							" is a " + name_and_type.second.originalText());
				}
			}
			if (a.lexical_types.size() > 1) have_any_types++;
			total_types += a.lexical_types.size(); 
		}

		//System.out.println(text + " could be any of " + types);
		log.info("Found " + total_types + " DBPedia types for "
				+ have_any_types + " candidates. "
				+ (q.size() - have_any_types) + " candidates are unknown.");
	}

}

