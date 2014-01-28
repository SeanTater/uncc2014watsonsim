package watson;
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
	
    public Engine test(Question question) {
    	// Look for identical results from multiple engines
    	
    	// Presort to allow easy matching 
    	Comparator<ResultSet> comp = new AnswerTitleComparator();
    	for (Engine engine : question) 
    		Collections.sort(engine, comp);

    	Engine out = new Engine("Prebuilt");
    	
    	int ii = 0; // Indri index
    	int li = 0; // Lucene index
    	Engine indri=null, lucene=null;
    	for (Engine e : question)
    		if (e.name.equalsIgnoreCase("indri"))
    			indri = e;
    		else
    			lucene = e;
    		
    	while (ii < indri.size() || li < lucene.size()) {
    		if (ii == indri.size())
    			// Indri ended. Finish lucene.
    			for (; li < lucene.size(); li++)
    				out.add(scoreLucene(lucene.get(li)));
    		else if (li == lucene.size())
    			// Lucene ended. Finish indri.
    			for (; ii < indri.size(); ii++)
    				out.add(scoreIndri(indri.get(ii)));
    		else {
    			// Both continue.
    			int swtch = comp.compare(indri.get(ii), lucene.get(li));
				if (swtch == 0) {
    				// Match found
    				out.add(scoreBoth(indri.get(ii), lucene.get(li)));
    				li++;
    				ii++;
				} else if (swtch < 0) {
					// Indri is behind
					out.add(scoreIndri(indri.get(ii)));
					ii++;
				} else {
					// Lucene is behind
					out.add(scoreLucene(lucene.get(li)));
					li++;
    			}
    		}
    	}
    	
    	Collections.sort(out);
    	Collections.reverse(out);
    	return out;
    }
    
    double sigmoid(double x) {
    	return 1 / (1 + Math.exp(-x));
    }
    
    ResultSet scoreIndri(ResultSet result) {
    	ResultSet newr = new ResultSet(result);
    	newr.setScore(sigmoid(0.2115 * result.getScore() - 1.4136));
    	return newr;
    }
    ResultSet scoreLucene(ResultSet result) {
    	ResultSet newr = new ResultSet(result);
    	newr.setScore(sigmoid(0.2683 * result.getScore() - 3.3227));
    	return newr;
    }
    ResultSet scoreBoth(ResultSet indri, ResultSet lucene) {
    	ResultSet newr = new ResultSet(indri);
    	newr.setScore(
    			sigmoid(-0.1592 * indri.getScore()
    			+ 0.4838 * lucene.getScore()
				- 4.102));
    	return newr;
    }
}
