package uncc2014watsonsim.researchers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.PassageRef;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.scorers.Scored;
import uncc2014watsonsim.search.*;

public class PassageRetrieval {

	private static final Searcher[] searchers = {
		new LucenePassageSearcher(),
		//new IndriSearcher(),
		//new CachingSearcher(new LuceneSearcher(), "lucene"),
		//new CachingSearcher(new BingSearcher(), "bing"),
	};
	
	public List<Scored<PassageRef>> query(Question q, Answer a) {
    	String sr = getPassageQuery(q, a);
    	List<Scored<PassageRef>> refs = new ArrayList<>();
    	// Query every engine
    	for (Searcher s : searchers)
    		refs.addAll(s.query(sr));
    	return refs;
	}
	
	
	public static String getPassageQuery(Question q, Answer a) {
		return q.getRaw_text() + " " + Matcher.quoteReplacement(a.candidate_text);
	}

}
