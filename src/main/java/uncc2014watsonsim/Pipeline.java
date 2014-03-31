package uncc2014watsonsim;

import uncc2014watsonsim.research.*;
import uncc2014watsonsim.search.*;

/** The standard Question Analysis pipeline
 *
 */
public class Pipeline {
	static final Searcher[] searchers = {
		new LuceneSearcher(),
		new IndriSearcher(),
		new BingSearcher(),
		//new GoogleSearcher()
	};
	
	static final Researcher[] researchers = {
		new MergeResearcher(),
		new PersonRecognitionResearcher(),
		new WordProximityResearcher(),
		new CorrectResearcher(),
		new WekaTeeResearcher(),
	};
	
	static final Learner learner = new WekaLearner();

	
	public static Question ask(String qtext) {
	    return ask(new Question(qtext));
	}
	
    /** Run the full standard pipeline */
    public static Question ask(Question question) {
        
    	// Query every engine
        for (Searcher s: searchers)
			try {
				question.addAll(s.runQuery(question.text));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			try {
				r.research(question);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    	for (Researcher r : researchers)
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
