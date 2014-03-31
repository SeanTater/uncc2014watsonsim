package uncc2014watsonsim.research;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.QType;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.SampleData;

/**
 *
 * @author Phani Rahul
 */
public class PersonRecognitionResearcher extends Researcher {

    private static TokenNameFinderModel model = null;
    private static NameFinderME nameFinder = null;
    private boolean enabled=true;

    public PersonRecognitionResearcher() {
        InputStream is;
		try {
			is = new FileInputStream(SampleData.get_filename("en-ner-person.bin"));
	        model = new TokenNameFinderModel(is);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Missing NLP model data. Deactivating NameRecognitionResearcher.");
			enabled = false;
		}
        nameFinder = null;
        try {
            nameFinder = new NameFinderME(model);
        } catch (Exception ex) {
            Logger.getLogger(PersonRecognitionResearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void research(Question q) throws Exception {
    	if (q.getType() == QType.FITB && enabled){
    		super.research(q);
    	}
    }

    @Override
    public void research_answer(Answer answer) {
        Span nameSpans[] = null;
        String[] sentence = null;
        sentence = answer.getTitle().split("[,'()  ]+");

        nameSpans = nameFinder.find(sentence);
        nameFinder.clearAdaptiveData();

        StringBuilder ret = new StringBuilder();
        for (Span s : nameSpans) {

            for (int i = s.getStart(); i < s.getEnd(); i++) {
                ret.append(sentence[i]);
                ret.append(" ");
            }
        }
        if (!ret.toString().isEmpty()){
        	answer.setTitle(ret.toString());
        }
        	
    }

}
