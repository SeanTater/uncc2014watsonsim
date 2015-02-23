package uncc2014watsonsim.researchers;

import java.util.Properties;
import java.util.regex.Matcher;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.nlp.Environment;
import uncc2014watsonsim.search.*;

public class PassageRetrieval extends Researcher {
	private final Searcher[] searchers;
	
	public PassageRetrieval(Environment env) {
		searchers = new Searcher[]{
			new LucenePassageSearcher(env),
			//new IndriSearcher(env),
			//new CachingSearcher(new BingSearcher(env), "bing"),
		};
	}
	
	
	@Override
	public void answer(Question q, Answer a) {
    	String sr = getPassageQuery(q, a);
    	// Query every engine
    	for (Searcher s : searchers)
    		a.passages.addAll(s.query(sr));
	}
	
	
	private String getPassageQuery(Question q, Answer a) {
		return q.getRaw_text() + " " + Matcher.quoteReplacement(a.candidate_text);
	}

}
