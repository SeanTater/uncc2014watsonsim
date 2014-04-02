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
	
	static final LuceneSearcher passageSearcher = new LuceneSearcher();
	
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
		
        for (Answer a : question) {
        	// The merge researcher does what was once here.
	        	String sr = question.raw_text;
	        	sr = sr.replaceFirst(sr.split(" ")[0], a.getTitle());
	        	sr = StringUtils.filterRelevant(sr);
	        	// Query every engine
	        	try {
					a.passages.addAll(passageSearcher.runBaseQuery(sr));
					System.out.println("Found " + a.passages.size() + " passages.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	            /*for (int j =0 ; j< replaced.size(); j++){
	            	Answer rr = replaced.get(j);
	            	//bwr.write("\n passages.add(new Passage(\""+rr.getFullText().substring(0, 500).replace("\"", "").replace("\r","")+ "\","+j+",\""+rr.getTitle()+"\")");
	            	bwr.write("<candans>"+rr.getTitle()+"</candans>"+"\n" +"<passage>"+rr.getFullText().substring(0,500)+"</passage>"+"\n");
	            	}*/
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
