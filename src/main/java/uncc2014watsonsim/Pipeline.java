package uncc2014watsonsim;

import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

import uncc2014watsonsim.research.*;
import uncc2014watsonsim.search.*;

/** The standard Question Analysis pipeline
 *
 */
public class Pipeline {
	
	private static final Searcher[] searchers = {
		new CachingSearcher(new LuceneSearcher(), "lucene"),
		new CachingSearcher(new IndriSearcher(), "indri"),
		//new CachingSearcher(new BingSearcher(), "bing"),
		//new CachingSearcher(new GoogleSearcher(), "google")
// usage without CachingSearcher
//		new LuceneSearcher(),
//		new IndriSearcher(),
//		new BingSearcher()
	};
	
	private static final Researcher[] early_researchers = {
		new HyphenTrimmer(),
		new Merge(),
		new ChangeFitbAnswerToContentsOfBlanks(),
		new PassageRetrieval(),
		new PersonRecognition(),
	};
	
	private static final Scorer[] scorers = {
		new WordProximity(),
		new Correct(),
		new SkipBigram(),
		new PassageTermMatch(),
		new PassageCount(),
	};

	private static final Researcher[] late_researchers = {
		new WekaTee(),
	};
	
	
	private static final Learner learner = new WekaLearner();

	
	public static Question ask(String qtext) {
	    return ask(new Question(qtext));
	}
	
    /** Run the full standard pipeline */
	public static Question ask(Question question) {
		// Query every engine
		for (Searcher s: searchers)
			question.addPassages(s.runTranslatedQuery(question.text));
        
        /* TODO: filter strange results?
        HashSet<String> ignoreSet = new HashSet<String>();
        ignoreSet.add("J! Archive");
        ignoreSet.add("Jeopardy");
        */

		for (Researcher r : early_researchers)
			r.question(question);
    	
    	for (Researcher r : early_researchers)
    		r.complete();
    	

        for (Scorer s: scorers) {
        	s.scoreQuestion(question);
        }
        
        for (Researcher r : late_researchers)
			r.question(question);
    	
    	for (Researcher r : late_researchers)
    		r.complete();
    	
        try {
			learner.test(question);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return question;
    }
}
