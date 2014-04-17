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
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

import uncc2014watsonsim.research.*;
import uncc2014watsonsim.search.*;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.types.UIMAQuestion;

/** The standard Question Analysis pipeline
 *
 */
public class Pipeline {

	private static final Searcher[] searchers = {
		new CachingSearcher(new LuceneSearcher(), "lucene"),
		new CachingSearcher(new IndriSearcher(), "indri"),
		new CachingSearcher(new BingSearcher(), "bing"),
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
		new PassageTermMatch(),
	};

	private static final Researcher[] late_researchers = {
		new WekaTee(),
	};
	
	
	private static final Learner learner = new WekaLearner();

	
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
