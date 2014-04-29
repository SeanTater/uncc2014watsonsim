package uncc2014watsonsim.datapreparation;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.DBQuestionResultsSource;
import uncc2014watsonsim.DBQuestionSource;
import uncc2014watsonsim.DBResultsPassagesSource;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;
import uncc2014watsonsim.research.Correct;
import uncc2014watsonsim.research.NGram;
import uncc2014watsonsim.research.PassageCount;
import uncc2014watsonsim.research.PassageQuestionLengthRatio;
import uncc2014watsonsim.research.PassageRetrieval;
import uncc2014watsonsim.research.PassageTermMatch;
import uncc2014watsonsim.research.PercentFilteredWordsInCommon;
import uncc2014watsonsim.research.QuestionInPassageScorer;
import uncc2014watsonsim.research.Scorer;
import uncc2014watsonsim.research.SkipBigram;
import uncc2014watsonsim.research.WekaTee;
import uncc2014watsonsim.research.WordProximity;
import uncc2014watsonsim.search.IndriSearcher;
import uncc2014watsonsim.search.LuceneSearcher;
import uncc2014watsonsim.search.GoogleSearcher;
import uncc2014watsonsim.search.Searcher;

/**
 *
 * @author walid shalaby (adapted from GenerateSearchResultDataset)
 */
public class GenerateScorersArff {

    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	Initialize();
    	int curq = -1;
    	DBResultsPassagesSource dbquestions = new DBResultsPassagesSource("");
    	for (Question q : dbquestions) {
    		new ScorersCollector(q).score();    		
    		if(curq!=q.id) {
    			curq = q.id;
    			System.out.print(""+ q.id + " ");
    			// Somewhere around once in 80 times..
    			if (q.id % 80 == 0) System.out.println();
    		}
    	}
    	for (Question q : dbquestions) {
			ScorersCollector.arffWriter.question(q);
		}
		ScorersCollector.arffWriter.complete();
        System.out.println("Done.");
    }

	private static void Initialize() {
		// TODO Auto-generated method stub
		Score.registerAnswerScore("CORRECT");
		Score.registerAnswerScore("INDRI_RANK");
		Score.registerAnswerScore("INDRI_SCORE");
		Score.registerAnswerScore("LUCENE_RANK");
		Score.registerAnswerScore("LUCENE_SCORE");
		Score.registerAnswerScore("BING_RANK");
		Score.registerAnswerScore("BING_SCORE");
		Score.registerPassageScore("LUCENE_RANK");
		Score.registerPassageScore("LUCENE_SCORE");
		//Score.registerPassageScore("INDRI_RANK");
		//Score.registerPassageScore("INDRI_SCORE");		
		//Score.registerPassageScore("BING_RANK");
		//Score.registerPassageScore("BING_SCORE");
	}
}

class ScorersCollector extends Thread {
	Question q;
	
	private static final Scorer[] scorers = {
		//new WordProximity(),
		//new Correct(),
		//new SkipBigram(),
		//new PassageTermMatch(),
		//new PassageCount(),
		//new PassageQuestionLengthRatio(),
		//new PercentFilteredWordsInCommon(),
		//new QuestionInPassageScorer(),
		//new NGram(),		 
	};
	public static WekaTee arffWriter = new WekaTee();
	
	public ScorersCollector(Question q) {
		this.q = q;
	}
	
	public void score() {
		for (Scorer s: scorers) {
	    	s.scoreQuestion(q);
		}
	}
}
