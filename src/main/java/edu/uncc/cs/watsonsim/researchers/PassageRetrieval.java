package edu.uncc.cs.watsonsim.researchers;

import java.util.List;
import java.util.regex.Matcher;

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
	
	public PassageRetrieval(Environment env, Searcher... searchers) {
		this.searchers = searchers;
	}
	
	
	@Override
	public List<Answer> question(Question q, List<Answer> answers) {
		
		int total_passages = answers.stream().mapToInt(a -> {
			// Query every engine
			int count = 0;
	    	for (Searcher s : searchers) {
	    		List<Passage> passages = s.query(
	    				q.text + " " + Matcher.quoteReplacement(a.text));
	    		a.passages.addAll(passages);
	    		count += passages.size();
	    	}
	    	return count;
		}).sum();
	    	
		
		q.log.info("Found " + total_passages + " supporting passages.");
		return answers;
	}

}
