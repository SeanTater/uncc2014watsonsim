package edu.uncc.cs.watsonsim;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

import edu.uncc.cs.watsonsim.researchers.*;
import edu.uncc.cs.watsonsim.scorers.*;
import edu.uncc.cs.watsonsim.search.*;

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
	private final Searcher[] searchers;
	private final Researcher early_researchers;
	private final Scorer[] scorers;
	private final Researcher late_researchers;

	private final Environment env = new Environment();
	/**
	 * Start a pipeline with an existing timestamp
	 * @param millis Millis since the Unix epoch, as in currentTimeMillis()
	 */
	public DefaultPipeline() {
		Timestamp run_start = new Timestamp(System.currentTimeMillis());
		
		/*
		 * Create the pipeline
		 */
		searchers = new Searcher[]{
			new LuceneSearcher(env),
			new IndriSearcher(env, false),
			// You may want to cache Bing results
			// new BingSearcher(config),
			new CachingSearcher(env, new BingSearcher(env), "bing"),
			new Anagrams(env)
		};
		early_researchers = Researcher.pipe(env.log,	
			//new RedirectSynonyms(env),
			new HyphenTrimmer(),
			new StrictFilters(),
			new AnswerTrimming(),
			new MergeByText(env),
			new MergeAnswers(),
			//new ChangeFitbAnswerToContentsOfBlanks(),
			new PassageRetrieval(env,
					new LucenePassageSearcher(env)
					//new IndriSearcher(env, true)
					//new CachingSearcher(new BingSearcher(env), "bing"),
				),
			new MergeByCommonSupport(),
			new PersonRecognition(),
			new TagLAT(env),
			new MergeByCommonSupport()
		);
		scorers = new Scorer[]{
			new AnswerLength(),
			new AnswerPOS(),
			new CommonConstituents(),
			new Correct(env),
			new DateMatches(),
			new LATCheck(env),
			new LATMentions(),
			new LuceneEcho(),
			new NGram(),
			new PassageTermMatch(),
			new PassageCount(),
			new PassageQuestionLengthRatio(),
			new QPKeywordMatch(),
			new QAKeywordMatch(),
			new SkipBigram(),
			new TopPOS(),
			new WordProximity(),
			new WPPageViews(env)
			//new RandomIndexingCosineSimilarity(),
			//new DistSemCosQAScore(),
			//new DistSemCosQPScore(),
		};
		late_researchers = Researcher.pipe(env.log,
			new Normalize(),
			new WekaTee(run_start),
			new CombineScores(),
			new StatsDump(run_start, env)
		);
	}
	
	public List<Answer> ask(String qtext) {
	    return ask(new Question(qtext));
	}
	
	public List<Answer> ask(Question question) {
	    return ask(question, System.out::println);
	}
	
    /** Run the full standard pipeline */
	public List<Answer> ask(Question question, Consumer<String> listener) {
		// Query every engine
		Log l = env.log;
		l.setListener(listener);
		
		l.info("Generating candidate answers..");
		List<Answer> answers = new ArrayList<>();
		for (Searcher s: searchers)
			for (Passage p : s.query(question))
				answers.add(new Answer(p));
		l.info("Generated " + answers.size() + " candidate answers.");
		
		
		answers = early_researchers.pull(question, answers);
    	
    	l.info("Scoring supporting evidence..");
        for (Scorer s: scorers)
        	s.scoreQuestion(question, answers);
        
        l.info("Computing confidence..");
        
        answers = late_researchers.pull(question, answers);
        env.db.release();
        return answers;
    }
}
