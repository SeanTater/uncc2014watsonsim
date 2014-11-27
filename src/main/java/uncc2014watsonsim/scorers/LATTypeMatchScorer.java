package uncc2014watsonsim.scorers;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;
import uncc2014watsonsim.uima.types.UIMAQuestion;

/**
 * If the LAT type detected is the same as the type in the answer, return a 1.
 * This is pretty basic, but at least demonstrates integrating UIMA and can be
 * further modified.
 * 
 * @author Jonathan Shuman
 * 
 */
public class LATTypeMatchScorer extends AnswerScorer {
	boolean debug = false;
	static POSTaggerME tagger;
	static
	{
		POSModel model = new POSModelLoader().load(new File("data/en-pos-maxent.bin"));
		tagger = new POSTaggerME(model);
	}

	/**
	 * Scorer Implementation
	 * 
	 * @param q
	 *            Question
	 * @param a
	 *            Answer
	 * @return The score for this answer, or NaN if not applicable.
	 */
	@Override
	public Scored<Answer> scoreAnswer(Question q, Answer a, List<Passage> passages) {
		double retVal = 0;
		try {
			UIMAQuestion uimaQuestion = UimaTools.getSingleton(q.getCAS(),
					UIMAQuestion.type);
			String lat = uimaQuestion.getLAT();
			String ansTokens[] = WhitespaceTokenizer.INSTANCE
					.tokenize(a.candidate_text);
			String[] answerParse = tagger.tag(ansTokens);
			
			if(lat == null) //Error check
				return Double.NaN;
			
			switch (lat) {
			case ("Noun"): {
				Pattern p = Pattern.compile("N.+");
				for (String s : answerParse) { // See if we have a noun.
					if (p.matcher(s).matches()) {
						retVal++;
						if (debug)
							System.out.println("Noun found.");
					} else {
						// If there are, say verbs, in the answer, this answer
						// type is not a noun.
						retVal = -1;
						break; //for loop
					}
				}

				if (retVal > 0)
					retVal = 1;
				else
					retVal = 0;
				
				break; //case
			}
			case ("Verb"): {
				Pattern p = Pattern.compile("V.+");
				for (String s : answerParse) { // See if we have a noun.
					if (p.matcher(s).matches()) {
						retVal++;
						if (debug)
							System.out.println("Verb found.");
					} else {
						// If there are, say nouns, in the answer, this answer
						// type is not a verb.
						retVal = -1;
						break; //case
					}
				}

				if (retVal > 0)
					retVal = 1;
				else
					retVal = 0;
				
				break;
			}
			case ("null"): {
				if(debug){
					System.out.println("Null LAT. Do nothing.");
					break; //case
				}
			}
			default:
				if (debug) {
					System.out
							.println("We haven't detected this LAT type yet.");
				}
			}
		} catch (UimaToolsException e) {
			e.printStackTrace();
		}
		return retVal;
	}

}