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

import uncc2014watsonsim.researchers.*;
import uncc2014watsonsim.scorers.*;
import uncc2014watsonsim.search.*;

/** The standard Question Analysis pipeline.
 * 
 * The pipeline is central to the DeepQA framework.
 * It consists of {@link Searcher}s, {@link Researcher}s, {@link Scorer}s, and
 * a {@link Learner}.<p>
 * 
 * Each step in the pipeline takes and possibly transforms a {@link Question}.
 * {@link Question}s aggregate {@link Answer}s, and a correct {@link Answer} (if it is
 *     known).
 * {@link Answer}s aggregate scores (which are primitive doubles) and
 *     {@link Passage}s, and contain a candidate text.
 * {@link Passage}s aggregate more scores, and provide some utilities for
 *     processing the text they contain.<p>
 * 
 * A {@link Searcher} takes the {@link Question}, runs generic transformations
 *     on its text and runs a search engine on it. The Passages it creates are
 *     promoted into {@link Answer}s, where the Passage title is the candidate
 *     {@link Answer} text and each {@link Answer} has one Passage. The passage
 *     Searchers do the same but are optimized for taking {@link Answer}s and
 *     finding supporting evidence as Passages. In that case, the resulting
 *     Passages are not promoted.<p>
 * 
 * A {@link Researcher} takes a {@link Question} and performs a transformation
 *     on it. There is no contract regarding what it can do to the
 *     {@link Question}, so they can't be safely run in parallel and the order
 *     of execution matters. Read the source for an idea of the intended order.
 *     <p>
 * 
 * A {@link Scorer} takes a {@link Question} and generates scores for either
 *     {@link Answer}s or {@link Passage}s (inheriting from
 *     {@link AnswerScorer} or {@link PassageScorer} respectively.)<p>
 *
 */
public class DefaultPipeline {
	
	private static final Searcher[] searchers = {
		new LuceneSearcher(),
		//new IndriSearcher(),
// You may want to cache Bing results
//		new BingSearcher()
		new CachingSearcher(new BingSearcher(), "bing"),
	};
	
	private static final Researcher[] early_researchers = {
		new MediaWikiTrimmer(), // Before passage retrieval
		new HyphenTrimmer(),
		/* +0.06 recall
		 * -0.30 MRR
		 * new RedirectSynonyms(),
		 */
		new Merge(),
		//new ChangeFitbAnswerToContentsOfBlanks(),
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
		new LATTypeMatchScorer(),
		new WPPageViews(),
		//new RandomIndexingCosineSimilarity(),
		new DistSemCosQAScore(),
		//new DistSemCosQPScore(),
		//new ScorerAda(),      // TODO: Introduce something new
		//new WShalabyScorer(), // TODO: Introduce something new
		//new SentenceSimilarity(),
		//new CoreNLPSentenceSimilarity(),
	};

	private static final Researcher[] late_researchers = {
		new WekaTee(),
		new CombineScores()
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
	
	public static Question ask(String qtext) {
	    return ask(new Question(qtext));
	}
	
    /** Run the full standard pipeline */
	public static Question ask(Question question) {
		// Query every engine
		for (Searcher s: searchers)
			question.addPassages(s.query(question.getRaw_text()));

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
        
        return question;
    }
}
