package uncc2014watsonsim.researchers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.QType;
import uncc2014watsonsim.Question;

/**
 *
 * @author Phani Rahul
 */
public class PersonRecognition extends Researcher {

    private static TokenNameFinderModel model = null;
    private static NameFinderME nameFinder = null;
    private boolean enabled=true;

    public PersonRecognition() {
        InputStream is;
		try {
			is = new FileInputStream("data/en-ner-person.bin");
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
            Logger.getLogger(PersonRecognition.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

	@Override
    public void question(Question q) {
    	if (q.getType() == QType.FITB && enabled){
    		super.question(q);
    	}
    }

    @Override
    public void answer(Question q, Answer answer) {
        Span nameSpans[] = null;
        String[] sentence = null;
        sentence = answer.candidate_text.split("[,'()  ]+");

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
        	answer.candidate_text = ret.toString();
        }
        	
    }

}
