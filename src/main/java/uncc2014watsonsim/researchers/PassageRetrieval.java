package uncc2014watsonsim.researchers;

import java.util.regex.Matcher;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.search.*;

public class PassageRetrieval extends Researcher {

	private static final Searcher[] searchers = {
		new LucenePassageSearcher(),
		//new IndriSearcher(),
		//new CachingSearcher(new LuceneSearcher(), "lucene"),
		//new CachingSearcher(new BingSearcher(), "bing"),
	};
	
	
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
