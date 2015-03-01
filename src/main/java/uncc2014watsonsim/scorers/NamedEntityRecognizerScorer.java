package uncc2014watsonsim.scorers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;

/**
 * This scorer will return the number of named entities matched in a given
 * question
 * 
 * @author Jonathan Shuman
 * 
 */
public class NamedEntityRecognizerScorer extends PassageScorer {
	public double scorePassage(Question q, Answer a, Passage p) {

		// Jane Austen
		String c_t = StringUtils.join(p.text, " ");

		// Romantic novelist Jane Austen once wrote -the- book Emma.
		String q_t = q.text;

		return numberOfNamedPersonEntities(q_t, c_t);

	}

	private double numberOfNamedPersonEntities(String q_t, String c_t) {
		InputStream modelIn = null;
		double retVal = 0;
		try {
			modelIn = new FileInputStream("data/en-ner-person.bin");
			TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
			NameFinderME nameFinder = new NameFinderME(model);
			String[] c_words = SimpleTokenizer.INSTANCE.tokenize(c_t);
			String[] q_words = SimpleTokenizer.INSTANCE.tokenize(q_t);
			Span[] c_tokens = nameFinder.find(c_words);
			
			for (Span cS : c_tokens) {
				for (String q_word : q_words)
					if ((c_words[cS.getStart()]).contains(q_word))
						retVal++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return Double.NaN;
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
					return Double.NaN;
				}

			}
		}
		return retVal;
	}
}
