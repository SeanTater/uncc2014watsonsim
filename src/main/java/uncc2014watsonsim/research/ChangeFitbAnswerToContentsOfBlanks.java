package uncc2014watsonsim.research;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.jcas.JCas;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.QType;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;
import uncc2014watsonsim.uima.types.UIMAQuestion;

public class ChangeFitbAnswerToContentsOfBlanks extends Researcher {

	
	public ChangeFitbAnswerToContentsOfBlanks () {
		Score.registerPassageScore("FITB_EXACT_MATCH_SCORE");
	}
	
	@Override
	public void question(Question question) {
		
		// If the question is not FITB, set the value of each answer score to NaN and exit
		if (question.getType() != QType.FITB) {
			for (Answer a: question) {
				a.scores.put("FITB_EXACT_MATCH_SCORE", Double.NaN);				
			}
			return;
		}
		
		//else run the scorer on FITB questions
    	JCas annot = question.getCAS(); // See if we can pull out the annotations from the cas
    	UIMAQuestion uimaQuestion;
		try {
			uimaQuestion = UimaTools.getSingleton(annot, UIMAQuestion.type);
		} catch (UimaToolsException e) {
			// On error, do not continue.
			e.printStackTrace();
			return;
		}
    	
    	String section1 = uimaQuestion.getFitbSection1().getCoveredText();
    	String section2 = uimaQuestion.getFitbSection2().getCoveredText();
        section1 = section1.replaceAll("\"", ""); //remove quotes
        section2 = section2.replaceAll("\"", ""); //remove quotes
        //TODO: make search more flexible (such as removing punctuation)
        
    	StringBuffer str1 = new StringBuffer();
    	str1.append("(?i)");
        if (!section1.trim().equals("")) str1.append(section1);
        str1.append(".*");//append blanks here
        if (!section2.trim().equals("")) str1.append(section2);
        //System.out.println("str1: " + str1); //for debug
    	
		Matcher matcher1;
		Pattern pattern1 = Pattern.compile(str1.toString());
		int docPatternStart = -1; //start location of the found pattern within a document
		int docPatternEnd = -1; //end location of a found pattern within a document
		Matcher matcher2;
		Matcher matcher3;
		Pattern pattern2 = Pattern.compile("(?i)" + section1);
		Pattern pattern3 = Pattern.compile("(?i)" + section2);
		int answerStartLocation = -1;
		int answerEndLocation = -1;
		String result = null;
		
		for (Answer a: question) {
			matcher1 = pattern1.matcher(a.passages.get(0).text); //title is the text to be searched
			if (matcher1.find()) {	//find the question with blanks (str1) within the document (a.getFullText()); assign result as str1substring (question with blanks filled in)
				docPatternStart = matcher1.start();
				docPatternEnd = matcher1.end();
				String str1substring = a.passages.get(0).text.substring(docPatternStart, docPatternEnd);
				//System.out.println("found pattern in doc: " + str1substring + ": title: " + a.getTitle()); //for debug

				answerStartLocation = 0;
				answerEndLocation = str1substring.length();

				matcher2 = pattern2.matcher(str1substring);
				if (matcher2.find()) {
					answerStartLocation = matcher2.end();
					//System.out.print("answerStartLocation: " + answerStartLocation); //for debug
				};
				
				matcher3 = pattern3.matcher(str1substring);
				if (matcher3.find(answerStartLocation)) {
					answerEndLocation = matcher3.start();
					//System.out.println(" answerEndLocation: " + answerEndLocation); //for debug
				} else { //no final section found so it must be one or more blanks
					//TODO: add the number of words (from the doc) for the number of blanks in the question
					
				}
				
				result = str1substring.substring(answerStartLocation,answerEndLocation);
				//System.out.println("The answer: " + result); //for debug
				if (result != null && !result.equals("")) {
					a.candidate_text = result;
					a.scores.put("FITB_EXACT_MATCH_SCORE", 1.0);
				}
				else {
					a.candidate_text = "result was blank or null";
					a.scores.put("FITB_EXACT_MATCH_SCORE", Double.NaN);				
				}
				
			};
		}
	}
}
