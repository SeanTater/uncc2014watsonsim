package uncc2014watsonsim.research;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.InvalidFormatException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

//@author Wlodek
/*
 * initializes the OpenNLP pipeline models and shows how to compare two parse structures 
 * 
 */

public class OpenNlpTests {
	private boolean modelsAreInitialized=false;	

	public  String modelsPath="data/"; //models directory
	private File parserMFile; 
	private File sentDetectorMFile;
	private File chunkerMFile;
	private File posMFile;

	public SentenceModel sentenceModel; //sentence detection model 
	public ParserModel parserModel; //parsing model
	public POSTaggerME tagger;

	//initialize all models needed for processing a passage of text (multiple sentences)
	//TODO: allow partial initialization parserInit() and chunkerInit()
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
	
	public Parse[] parsePassageText(String p) throws InvalidFormatException{
		if (!modelsAreInitialized)init();
		//initialize 	 
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(this.sentenceModel);
		Parser parser = ParserFactory.create(
				this.parserModel,
				20, // beam size
				0.95); // advance percentage
		//find sentences, tokenize each, parse each, return top parse for each 	 	 
		String[] sentences = sentenceDetector.sentDetect(p);
		Parse[] results = new Parse[sentences.length];
		for (int i=0;i<sentences.length;i++){
			String[] tks = SimpleTokenizer.INSTANCE.tokenize(sentences[i]);
			String sent= StringUtils.join(tks," ");
			Parse[] sentResults = ParserTool.parseLine(sent,parser, 1);
			results[i]=sentResults[0];
		}
		return results;
	}

	//
	public Parse[] getAllChildren(Parse[] parseAr){
		Parse[] allChildren = parseAr;
		Parse[] allChldr;	
		if(parseAr.length > 1)
		for (int i=0; i<parseAr.length;i++){
			Parse[] children = parseAr[i].getChildren();			
			allChldr= getAllChildren(children);
			allChildren  =ArrayUtils.addAll(allChildren, allChldr);
		}	
		return allChildren;		
	}


	public Parse[] getAllChildren(Parse parse){
		Parse[] allChildren = new Parse[1];
		allChildren[0]=parse;
		Parse[] allChldr;
		Parse[] children = parse.getChildren();			
		allChldr= getAllChildren(children);
		allChildren  =ArrayUtils.addAll(allChildren, allChldr);
		return allChildren;		
	}

	public double compareParseChunks(Parse[] pa1, Parse[] pa2, boolean verbose){
		HashMap<String,String> pa1h= new HashMap<String, String>();
		double numMatches=0;
		for (int i=0;i<pa1.length;i++){
			String[] key = new String[2];
			key[0]=pa1[i].getCoveredText();
			key[1]=pa1[i].getLabel();
			pa1h.put(key[1]+key[0],"y");
		}
		for (int j=0;j<pa2.length;j++){
			String[] key = new String[2];
			key[0]=pa2[j].getCoveredText();
			key[1]=pa2[j].getLabel();
			if (pa1h.containsKey(key[1]+key[0])){ 
				numMatches++;
				if (verbose) System.out.println("\n");
				pa2[j].show();
				if (verbose) System.out.println("type: "+pa2[j].getType());
			};
		}
		if (verbose) System.out.println("numMatches "+numMatches);
		return numMatches;
	}

	public static void main(String[] args) throws IOException {
		OpenNlpTests pt= new OpenNlpTests();
		String pas="Bad habits, footballs, buckets.";
		Parse[] parses = pt.parsePassageText(pas);
		Parse[][] parsecs = new Parse[2][];
		for (int i=0;i<parses.length;i++){
//			System.out.print("parse["+i+"]: ");
//			parses[i].show();
			System.out.println(parses[i].getText());
			Parse[] parsec = pt.getAllChildren(parses[i]);
			parsecs[i]=parsec;
		}
		pt.compareParseChunks(parsecs[0], parsecs[0], true);
		System.out.println("");
	}
}

