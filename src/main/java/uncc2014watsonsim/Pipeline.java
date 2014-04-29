package uncc2014watsonsim;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

import uncc2014watsonsim.research.*;
import uncc2014watsonsim.search.*;

/** The standard Question Analysis pipeline
 *
 */
public class Pipeline {
	
	private static final Searcher[] searchers = {
		//new CachingSearcher(new LuceneSearcher(), "lucene"),
		//new CachingSearcher(new IndriSearcher(), "indri"),
		//new CachingSearcher(new BingSearcher(), "bing"),
		//new CachingSearcher(new GoogleSearcher(), "google")
// usage without CachingSearcher
		new LuceneSearcher(),
		new IndriSearcher(),
		new BingSearcher()
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
		new PassageQuestionLengthRatio(),
		new PercentFilteredWordsInCommon(),
		new QuestionInPassageScorer(),
		//new ScorerIrene(), // TODO: Doesn't compile
		new NGram(),
	};

	private static final Researcher[] late_researchers = {
		new WekaTee(),
	};
	
	/*
	 * Initialize UIMA. 
	 * Why here? We do not want to reinstantiate the Analysis engine each time.
	 * We also don't want to load the POS models each time we ask a new question. Here we can hold the AE for the 
	 * entire duration of the Pipeline's life.
	 */
	public static AnalysisEngine uimaAE;
	
	static {
		try{
			XMLInputSource uimaAnnotatorXMLInputSource = new XMLInputSource("src/main/java/uncc2014watsonsim/uima/qAnalysis/qAnalysisApplicationDescriptor.xml");
			final ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(uimaAnnotatorXMLInputSource);
			//Generate AE
			uimaAE = UIMAFramework.produceAnalysisEngine(specifier);
		}catch(IOException e){
			e.printStackTrace();
		} catch (InvalidXMLException e) {
			e.printStackTrace();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
	}
	/* End UIMA */
	
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
