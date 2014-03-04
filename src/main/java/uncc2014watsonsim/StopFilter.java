package uncc2014watsonsim;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;


/**
*@author Jagan Vujjini
*/
public class StopFilter {
	
	public static String filtered(String question) throws IOException {
		
		String mQuestion="";
		
		TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_46, new StringReader(question));
		tokenStream = new org.apache.lucene.analysis.core.StopFilter(Version.LUCENE_46, tokenStream, EnglishAnalyzer.getDefaultStopSet());
		CharTermAttribute token = tokenStream.addAttribute(CharTermAttribute.class);
		
		tokenStream.reset();
		
		while (tokenStream.incrementToken()) {
			mQuestion += token.toString() + " ";
		}
		
		tokenStream.close();
		
		return mQuestion.trim();
		
	}

}