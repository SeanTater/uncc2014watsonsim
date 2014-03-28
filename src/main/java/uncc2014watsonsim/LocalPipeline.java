package uncc2014watsonsim;

import uncc2014watsonsim.search.IndriSearcher;
import uncc2014watsonsim.search.LuceneSearcher;
import uncc2014watsonsim.search.Searcher;

public class LocalPipeline extends Pipeline {
	static final Searcher[] searchers = {
		new LuceneSearcher(),
		new IndriSearcher(),
	};
}
