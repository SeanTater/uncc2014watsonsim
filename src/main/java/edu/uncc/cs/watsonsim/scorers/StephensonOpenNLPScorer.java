package edu.uncc.cs.watsonsim.scorers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;

/*
 * Author: Chris Stephenson
 */

public class StephensonOpenNLPScorer {
  private boolean modelsAreInitialized=false;	
	public  String modelsPath="data/"; //models directory
	private File parserMFile; 
	private File sentDetectorMFile;
	private File chunkerMFile;
	private File posMFile;

	public SentenceModel sentenceModel; //sentence detection model 
	public ParserModel parserModel; //parsing model
	public POSTaggerME tagger;
	
	
	public void init() throws InvalidFormatException{
		File modelsDir = new File(this.modelsPath);

		this.parserMFile = new File(modelsDir, "en-parser-chunking.bin");
		this.sentDetectorMFile = new File(modelsDir, "en-sent.bin");
		this.chunkerMFile=new File(modelsDir,"en-chunker.bin");
		this.posMFile = new File(modelsDir,"en-pos-maxent.bin");

		InputStream sentModelIn = null;
		FileInputStream parserStream;
		try {
			//for finding sentences
			sentModelIn = new FileInputStream(sentDetectorMFile);
			this.sentenceModel = new SentenceModel(sentModelIn);
			//for finding POS
			FileInputStream posModelStream = new FileInputStream(posMFile);
			POSModel model = new POSModel(posModelStream);
			this.tagger = new POSTaggerME(model);
			//for parsing
			parserStream = new FileInputStream(parserMFile);
			this.parserModel = new ParserModel(parserStream);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.modelsAreInitialized=true;
	}
	
	public void testSentDetector(String testSents) throws InvalidFormatException{
		init();
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(this.sentenceModel);
		String[] sentences = sentenceDetector.sentDetect(testSents);
		for (int i=0;i<sentences.length; i++)
			System.err.println("sent: "+sentences[i]);
	}
	
	
}
