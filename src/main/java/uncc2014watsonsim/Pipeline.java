package uncc2014watsonsim;

import java.io.IOException;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

import uncc2014watsonsim.research.*;
import uncc2014watsonsim.search.*;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.types.UIMAQuestion;

/** The standard Question Analysis pipeline
 *
 */
public class Pipeline {
	static final Searcher[] searchers = {
		new LuceneSearcher(),
		new IndriSearcher(),
		//new BingSearcher(),
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

	
	public static Question ask(String qtext) throws Exception {
		try {
			ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(new XMLInputSource("src/main/java/uncc2014watsonsim/uima/uimaexperiment/mainEngine.xml"));
			AnalysisEngine main = UIMAFramework.produceAnalysisEngine(specifier);
			JCas cas = main.newJCas();
			cas.setDocumentText(qtext);
			main.process(cas);
			UIMAQuestion question = UimaTools.getSingleton(cas, UIMAQuestion.type);
			Question q = new Question();
			q.setCategory(q.getCategory());
			q.setRaw_text(question.getRaw_text());
			q.setText(question.getFiltered_text());
			q.setType(QType.valueOf(question.getQtype()));
			return ask(q);
		}
		catch (Exception e) {
			throw e;
		}
	}
	
    /** Run the full standard pipeline */
	public static Question ask(Question question) {
		if (question.getType() == QType.FITB) {
			for (Searcher s: searchers) {
				if (s.getClass().getName().equals("uncc2014watsonsim.search.IndriSearcher")) {
					//System.out.println("text: " + question.text); //for debugging
					//System.out.println("raw_text: " + question.raw_text); //for debugging
					try {
						question.addAll(((IndriSearcher)s).runFitbQuery(question));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else	{
			// Query every engine
			for (Searcher s: searchers)
				try {
					question.addAll(s.runQuery(question.text));
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
