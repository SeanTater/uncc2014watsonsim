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
		new LuceneSearcher(),
		new IndriSearcher(),
		new BingSearcher(),
		//new GoogleSearcher()
	};
	
	private static final Researcher[] researchers = {
		new HyphenTrimmer(),
		new Merge(),
		new PassageRetrieval(),
		new PersonRecognition(),
		new WekaTee(),
	};
	
	private static final Scorer[] scorers = {
		new WordProximity(),
		new Correct(),
	};
	
	
	private static final Learner learner = new WekaLearner();

	
	public static Question ask(String qtext) {
	    return ask(new Question(qtext));
	}
	
    /** Run the full standard pipeline */
	public static Question ask(Question question) {
		if (question.getType() == QType.FITB) {
			for (Searcher s: searchers) {
				try {
					question.addAll(s.runFitbQuery(question));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else	{
			// Query every engine
			for (Searcher s: searchers)
				try {
					question.addPassages(s.runQuery(question.text));
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

        /* This is Jagan's quotes FITB code. I do not have quotes indexed separately so I can't do this.
        for (Searcher s : searchers){
        	// Query every engine
        	if(question.getType() == QType.FACTOID){
        		question.addAll(s.runQuery(question.text, UserSpecificConstants.indriIndex, UserSpecificConstants.luceneIndex));
        	} else if (question.getType() == QType.FITB) {
        		question.addAll(s.runQuery(question.text, UserSpecificConstants.quotesIndriIndex, UserSpecificConstants.quotesLuceneIndex));
        	} else {
        		return;
        	}
        }*/
        
        /* TODO: filter strange results?
        HashSet<String> ignoreSet = new HashSet<String>();
        ignoreSet.add("J! Archive");
        ignoreSet.add("Jeopardy");
        */
        
        
    	for (Researcher r : researchers)
			r.question(question);
    	
    	for (Researcher r : researchers)
    		r.complete();
    	

        for (Scorer s: scorers) {
        	s.scoreQuestion(question);
        }
    	
        try {
			learner.test(question);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //order answers by rank
        if (question.getType() == QType.FITB) {
        	Collections.sort(question, new RankOrder());
        }
        
        return question;
    }
}
