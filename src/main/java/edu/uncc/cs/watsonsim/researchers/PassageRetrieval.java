package edu.uncc.cs.watsonsim.researchers;

import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Environment;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Question;
import edu.uncc.cs.watsonsim.search.*;

/**
 * Search for documents having relevance to both the question and a candidate
 * answer.
 */
public class PassageRetrieval extends Researcher {
	private final Searcher[] searchers;
	private final Logger log = Logger.getLogger(this.getClass());
	
	public PassageRetrieval(Environment env) {
		searchers = new Searcher[]{
			new LucenePassageSearcher(env),
			//new IndriSearcher(env),
			//new CachingSearcher(new BingSearcher(env), "bing"),
		};
	}
	
	
	@Override
	public List<Answer> question(Question q, List<Answer> answers) {
		int total_passages=0; // logging
		
		for (Answer a: answers) {
	    	// Query every engine
	    	for (Searcher s : searchers) {
	    		List<Passage> passages = s.query(
	    				q.text + " " + Matcher.quoteReplacement(a.text));
	    		total_passages += passages.size();
	    		a.passages.addAll(passages);
	    	}
		}
		
		log.info("Found " + total_passages + " supporting passages.");
		return answers;
	}

}
