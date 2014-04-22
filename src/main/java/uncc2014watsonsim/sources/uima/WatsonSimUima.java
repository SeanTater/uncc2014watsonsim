package uncc2014watsonsim.sources.uima;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Pipeline;
import uncc2014watsonsim.Question;

/**
 * A demo pipeline for UIMA for uncc2014watsonsim.
 * @author Jonathan Shuman
 */
public class WatsonSimUima {

	/**
	 * A test class for the uimaPipeline. Will fold into a future demo
	 * @param args
	 */
	public static void main(String[] args) {
		// Initialize UIMA Pipeline
		try {
			UimaPipeline uimaPipeline = new UimaPipeline();
			uimaPipeline.runQueryDemo();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		
	}

}