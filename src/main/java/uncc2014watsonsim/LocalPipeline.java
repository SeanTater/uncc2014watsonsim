package uncc2014watsonsim;

import uncc2014watsonsim.search.*;

public class LocalPipeline extends Pipeline {
	static final Searcher[] searchers = {
		new LuceneSearcher(),
		new IndriSearcher(),
		//new BingSearcher()
	};
}
