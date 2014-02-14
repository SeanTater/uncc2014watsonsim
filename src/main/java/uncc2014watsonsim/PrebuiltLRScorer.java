package uncc2014watsonsim;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import org.json.simple.parser.ParseException;

class AnswerTitleComparator implements Comparator<ResultSet> {
	public int compare(ResultSet a, ResultSet b) {
		return a.getTitle().compareToIgnoreCase(b.getTitle());
	}
}

public class PrebuiltLRScorer {
    /** Correlates search results for improved accuracy
     * It uses models prebuilt from Weka so it only makes sense to use this
     * implementation with Lucene and Indri*/
	public PrebuiltLRScorer() {}
	public PrebuiltLRScorer(String filename) throws FileNotFoundException, ParseException, IOException {
		train(new QuestionMap(filename));
	}
	
	public void train(QuestionMap dataset) {
		// No-op until ML code is added
	}
	
    public Question test(Question question) {
    	
    	for (ResultSet result : question) {
    		Engine lucene = result.first("lucene");
    		Engine indri = result.first("indri");
    		Engine combined = new Engine("combined", 0, 0);
    		if (lucene != null && indri != null) {
    			combined.score = scoreBoth(indri.score, lucene.score);
    		} else if (lucene != null) {
    			combined.score = scoreLucene(lucene.score);
    		} else if (indri != null) {
    			combined.score = scoreIndri(indri.score);
    		}
    		// In any of the above three cases, but not the "else":
    		if (lucene != null || indri != null)
    			result.engines.add(combined);
    	}
    	Collections.sort(question);
    	Collections.reverse(question);
    	return question;
    }
    
    double sigmoid(double x) {
    	return 1 / (1 + Math.exp(-x));
    }
    
    double scoreIndri(double score) {
    	return sigmoid(0.2115 * score - 1.4136);
    }
    double scoreLucene(double score) {
    	return sigmoid(0.2683 * score - 3.3227);
    }
    double scoreBoth(double indri, double lucene) {
    	return sigmoid(-0.1592 * indri
    			+ 0.4838 * lucene
				- 4.102);
    }
}
