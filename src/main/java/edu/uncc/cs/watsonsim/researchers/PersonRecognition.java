package edu.uncc.cs.watsonsim.researchers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.QType;
import edu.uncc.cs.watsonsim.Question;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

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
    public List<Answer> question(Question q, List<Answer> answers) {
    	if (q.getType() == QType.FITB && enabled){
    		answers = super.question(q, answers);
    	}
    	return answers;
    }

    @Override
    public Answer answer(Question q, Answer answer) {
        Span nameSpans[] = null;
        String[] sentence = null;
        sentence = answer.text.split("[,'()  ]+");

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
        	return new Answer(answer, ret.toString());
        }
        return answer;	
    }

}
