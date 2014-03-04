/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uncc2014watsonsim;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

/**
 *
 * @author Phani Rahul
 */
public class NameRecognition {

    private static TokenNameFinderModel model = null;
    private static NameFinderME nameFinder = null;

    private static void init() throws IOException {
        InputStream is = null;

        
        is = new FileInputStream("C:\\Users\\PhaniRahul\\Documents"
                + "\\NetBeansProjects\\NLP\\data\\en-ner-person.bin");
        model = new TokenNameFinderModel(is);
        nameFinder = null;
        try {
            nameFinder = new NameFinderME(model);
        } catch (Exception ex) {
            Logger.getLogger(NameRecognition.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String hasNoun(String text) {

        if(nameFinder == null){
            try {
                init();
            } catch (IOException ex) {
                Logger.getLogger(NameRecognition.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Span nameSpans[] = null;
        String[] sentence = null;
        sentence = text.split("[,'()  ]+");

        nameSpans = nameFinder.find(sentence);
        nameFinder.clearAdaptiveData();

        StringBuilder ret = new StringBuilder();
        for (Span s : nameSpans) {

            for (int i = s.getStart(); i < s.getEnd(); i++) {
                ret.append(sentence[i]);
                ret.append(" ");
            }
        }
        return ret.toString();
    }

}
