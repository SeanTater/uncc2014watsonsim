package uncc2014watsonsim.researchers;

import java.util.Properties;
import java.util.regex.Matcher;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.search.*;

public class PassageRetrieval extends Researcher {
	private final Searcher[] searchers;
	
	public PassageRetrieval(Properties config) {
		searchers = new Searcher[]{
			new LucenePassageSearcher(config),
			//new IndriSearcher(config),
			//new CachingSearcher(new BingSearcher(config), "bing"),
		};
	}
	
	
	@Override
	public void answer(Question q, Answer a) {
    	String sr = getPassageQuery(q, a);
    	// Query every engine
    	for (Searcher s : searchers)
    		a.passages.addAll(s.query(sr));
	}
	
	
	public static String getPassageQuery(Question q, Answer a) {
		return q.getRaw_text() + " " + Matcher.quoteReplacement(a.candidate_text);
	}

}
