package uncc2014watsonsim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import uncc2014watsonsim.anagram.AnagramQuestion;
import uncc2014watsonsim.anagram.AnagramSupportingPassage;
import uncc2014watsonsim.anagram.CandidateAnswer;
import uncc2014watsonsim.research.*;
import uncc2014watsonsim.search.*;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;

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
		//new BingSearcher()
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
		new LATTypeMatchScorer()
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
	public static AnalysisEngine anagramAE;
	
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
		
		try {
			anagramAE = UIMAFramework.produceAnalysisEngine(UIMAFramework.getXMLParser().parseResourceSpecifier(new XMLInputSource("src/main/java/uncc2014watsonsim/anagramPipeline/anagramMainEngine.xml")));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/* End UIMA */
	
	private static final Learner learner = new WekaLearner();

	
	public static Question ask(String qtext, String category) {
		if (QClassDetection.isAnagram(qtext, category)) {
			return askAnagramPipeline(qtext);
		}
	    return ask(new Question(qtext));
	}
	
	/**
	 * The anagram pipeline. Self contained in its own method for now
	 * @param qtext
	 * @return the resulting questions
	 */
	public static Question askAnagramPipeline(String qtext) {		
		JCas aJCas = null;
		Question result = new Question();
		try {
			aJCas = anagramAE.newJCas();
		} catch (ResourceInitializationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		aJCas.setDocumentText(qtext);
		try {
			anagramAE.process(aJCas);
		} catch (AnalysisEngineProcessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AnagramQuestion question;
		List<CandidateAnswer> canAns = null;
		try {
			question = UimaTools.getSingleton(aJCas, AnagramQuestion.type);
			canAns = UimaTools.getFSList(question.getCandidateAnswers());
		} catch (UimaToolsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result.setType(QType.ANAGRAM);
		for (CandidateAnswer a : canAns) {
			Answer r = new Answer();
			r.candidate_text = a.getAnswer();
			r.setExternalScore(a.getScore());
			r.passages = new ArrayList<Passage>();
			List<AnagramSupportingPassage> passages = null;
			try {
				passages = UimaTools.getFSList(a.getSupportingPassages());
			} catch (UimaToolsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (AnagramSupportingPassage p : passages) {
				Passage pass = new Passage();
				pass.text = p.getSupportingPassage();
				pass.title = p.getPassageTitle();
				r.passages.add(pass);
			}
			result.add(r);
		}
		
		/**
		 * sort answers in array by score
		 */
		double max_score = 0;
		int max_score_index = 0;
		for (int i = 0; i < result.size(); i++) {
			max_score = result.get(i).getExternalScore();
			max_score_index = i;
			for (int j = i+1; j < result.size(); j++) {
				if (result.get(j).getExternalScore() > max_score) {
					max_score = result.get(j).getExternalScore();
					max_score_index = j;
				}
			}
			Answer temp = result.get(i);
			result.set(i, result.get(max_score_index));
			result.set(max_score_index, temp);
		}
		
		return result;
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
