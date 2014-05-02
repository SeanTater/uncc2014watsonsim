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
		//new CachingSearcher(new LuceneSearcher(), "lucene"),
		//new CachingSearcher(new IndriSearcher(), "indri"),
		new CachingSearcher(new BingSearcher(), "bing"),
		//new CachingSearcher(new GoogleSearcher(), "google")
// usage without CachingSearcher
		new LuceneSearcher(),
		new IndriSearcher(),
		//new BingSearcher()
	};
	
	private static final Researcher[] early_researchers = {
		new MediaWikiTrimmer(), // Before passage retrieval
		new HyphenTrimmer(),
		/* +0.06 recall
		 * -0.30 MRR
		 * new RedirectSynonyms(),
		 */
		new Merge(),
		new ChangeFitbAnswerToContentsOfBlanks(),
		new PassageRetrieval(),
		new MediaWikiTrimmer(), // Rerun after passage retrieval
		new PersonRecognition(),
	};
	
	private static final Scorer[] scorers = {
		new LuceneRank(),
		new LuceneScore(),
		new IndriRank(),
		new IndriScore(),
		new BingRank(),
		new GoogleRank(),
		new WordProximity(),
		new Correct(),
		new SkipBigram(),
		new PassageTermMatch(),
		new PassageCount(),
		new PassageQuestionLengthRatio(),
		new PercentFilteredWordsInCommon(),
		new QuestionInPassageScorer(),
		//new ScorerIrene(), // TODO: Introduce something new
		new NGram(),
		//new ScorerAda(),      // TODO: Introduce something new
		//new WShalabyScorer(), // TODO: Introduce something new
		//new SentenceSimilarity(),
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
			question.addPassages(s.query(question.text));

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
