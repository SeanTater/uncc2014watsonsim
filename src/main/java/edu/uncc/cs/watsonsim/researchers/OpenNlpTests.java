package edu.uncc.cs.watsonsim.researchers;

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
	private File posMFile;

	public SentenceModel sentenceModel; //sentence detection model 
	public ParserModel parserModel; //parsing model
	public POSTaggerME tagger;

	public String ca="Jane Austen"; 
	public String q="Jane Austen wrote Emma .";
	public String passage="Jane Austen was very modest about her own genius.[7] She once famously described her work as "+
			"the little bit (two Inches wide) of Ivory, on which I work with so fine a brush, " +
			"as produces little effect after much labor [7]. " +
			"When she was a girl she wrote stories. Her works were printed only after much revision. " +
			"Only four of her novels were printed while she was alive. They were Sense and Sensibility (1811), " +
			"Pride and Prejudice (1813), Mansfield Park (1814) and Emma (1816). " +
			"Two other novels, Northanger Abbey and Persuasion, were printed in 1817 with " +
			"a biographical notice by her brother, Henry Austen. Persuasion was written shortly before her death. " +
			"She also wrote two earlier works, Lady Susan, and an unfinished novel, The Watsons. " +
			"She had been working on a new novel, Sanditon, but she died before she could finish it.";




	//initialize all models needed for processing a passage of text (multiple sentences)
	//TODO: allow partial initialization parserInit() and chunkerInit()
	public void init() throws InvalidFormatException{
		File modelsDir = new File(this.modelsPath);

		this.parserMFile = new File(modelsDir, "en-parser-chunking.bin");
		this.sentDetectorMFile = new File(modelsDir, "en-sent.bin");
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

	
	//parses a segment of text and shows the parse, and children of the top node
	public void parserTest1() throws IOException {
		if (!this.modelsAreInitialized) init();
		Parser parser = ParserFactory.create(
				this.parserModel,
				20, // beam size
				0.95); 
		Parse[] results = ParserTool.parseLine("Jane Austen was very modest about her own genius ."+this.q,
				parser, 1);
		Parse[] qResults = ParserTool.parseLine(this.q,parser, 1);
		Parse[] rChn = (results[0].getChildren())[0].getChildren();
		
		results[0].expandTopNode(results[0]);
		for (int i = 0; i < results.length; i++) {
			results[i].show();
		}
		for (int i = 0; i < qResults.length; i++) {
			qResults[i].show();
		}
		System.out.print("\n\n");
		for (int i = 0; i < rChn.length; i++) {
			rChn[i].show();
			System.out.print("\n");
		}
	}

//find sentences in a text
	public void testSentDetector(String testSents) throws InvalidFormatException{
		init();
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(this.sentenceModel);
		String[] sentences = sentenceDetector.sentDetect(testSents);
		for (int i=0;i<sentences.length; i++)
			System.err.println("sent: "+sentences[i]);
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
			//StringTokenizer st = new StringTokenizer(tks[i]); 
			//There are several tokenizers available. SimpleTokenizer works best

			String sent= StringUtils.join(tks," ");
			System.out.println("Found sentence " + sent);
			Parse[] sentResults = ParserTool.parseLine(sent,parser, 1);
			results[i]=sentResults[0];
		}
		return results;
	}

	public void taggerTest(){
		String[] words = SimpleTokenizer.INSTANCE.tokenize(
				"The quick, red fox jumped over the lazy, brown dogs.");
		String[] result = tagger.tag(words);
		for (int i=0 ; i < words.length; i++) {
			System.err.print(words[i] + "/" + result[i] + " ");
		}
		System.err.println("n");
	}

	//
	public Parse[] getAllChildren(Parse[] parseAr){
		Parse[] allChildren = parseAr;
		Parse[] allChldr;		
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

//computes the number of matches between two sets of parses
//a match means same label over the same string 
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
				if (verbose) System.out.println("span: "+pa2[j].getSpan());
				if (verbose) System.out.println("type: "+pa2[j].getType());
			};
		}
		if (verbose) System.out.println("numMatches "+numMatches);
		return numMatches;
	}

	//a simple scorer based on the number of matches; requires the first string to be in the passage
	public double scoreStructure(String ca, String q, String passage, boolean verbose) throws InvalidFormatException{
		double score1=0, score2=0;
		Parse[] caParse = this.parsePassageText(ca);
		Parse[] qParse = this.parsePassageText(q);
		Parse[] pasParse = this.parsePassageText(passage);
		Parse[] caParseCh = getAllChildren(caParse);
		Parse[] qParseCh = getAllChildren(qParse);
		Parse[] pasParseCh = getAllChildren(pasParse);
		score1=compareParseChunks(qParseCh, pasParseCh,verbose);
		score2=compareParseChunks(caParseCh, pasParseCh,verbose);
		return score1*score2;
	}

	//normalized scorer. 
	//TODO divide by passage length containing the matches, not the full passage length
	public double scoreStructureNorm(String ca, String q, String passage, boolean verbose) throws InvalidFormatException{
		double score1=0, score2=0;
		//OnlpParserTest pt= new OnlpParserTest();
		Parse[] caParse = this.parsePassageText(ca);
		Parse[] qParse = this.parsePassageText(q);
		Parse[] pasParse = this.parsePassageText(passage);
		Parse[] caParseCh = getAllChildren(caParse);
		Parse[] qParseCh = getAllChildren(qParse);
		Parse[] pasParseCh = getAllChildren(pasParse);
		score1=compareParseChunks(qParseCh, pasParseCh,verbose);
		score2=compareParseChunks(caParseCh, pasParseCh,verbose);
		return score1*score2/passage.length();
	}	


	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		OpenNlpTests pt= new OpenNlpTests();
		Parse[] parses = pt.parsePassageText("this is a cat . this is a dog .");
		for (int i=0;i<parses.length;i++){
			System.out.println("parses: ");
			parses[i].show();
		}
		String q="red fox jumped over brown dogs ."; 
		String pas="red fox jumped over brown dogs . The quick , red fox jumped over the lazy , brown dogs . ";
		//parses = pt.parsePassageText("The quick, red fox jumped over the lazy, brown dogs. The quick , red fox jumped over the lazy , brown dogs . ");
		parses = pt.parsePassageText(pas);
		Parse[][] parsecs = new Parse[2][];
		for (int i=0;i<parses.length;i++){
			System.out.print("parse["+i+"]: ");
			parses[i].show();
			System.out.println(parses[i].getText());
			Parse[] parsec = pt.getAllChildren(parses[i]);
			parsecs[i]=parsec;
			for (int j=0;j<parsec.length;j++){
				System.out.print("parses child: ");
				parsec[j].show();
			}
			System.out.println("number of children in the parses: "+parsec.length);
		}
		pt.compareParseChunks(parsecs[0],parsecs[1],true);
		System.out.println();
		System.out.println("NormalizedScore: "+pt.scoreStructureNorm("red fox",pas,q ,false)); 
		System.out.println("Raw Score: "+pt.scoreStructure("red fox",pas,q ,false)); 
		System.out.println("\n\n");
		
		
		String ca="Jane Austen"; 
		String qq="Jane Austen wrote Emma";
		String passage="Jane Austen was very modest about her own genius.[7] She once famously described her work as "+
				"the little bit (two Inches wide) of Ivory, on which I work with so fine a brush, " +
				"as produces little effect after much labor [7]. " +
				"Jane Austen wrote Emma."+
				"When she was a girl she wrote stories. Her works were printed only after much revision. " +
				"Only four of her novels were printed while she was alive. They were Sense and Sensibility (1811), " +
				"Pride and Prejudice (1813), Mansfield Park (1814) and Emma (1816). " +
				"Two other novels, Northanger Abbey and Persuasion, were printed in 1817 with " +
				"a biographical notice by her brother, Henry Austen. Persuasion was written shortly before her death. " +
				"She also wrote two earlier works, Lady Susan, and an unfinished novel, The Watsons. " +
				"She had been working on a new novel, Sanditon, but she died before she could finish it.";
		
		System.out.println();
		System.out.println("NormalizedScore: "+pt.scoreStructureNorm(ca,qq, passage,false));
		System.out.println("Raw Score: "+pt.scoreStructure(ca,qq, passage,false));
		
		//pt.taggerTest();
		//pt.testSentDetector(passage);
	}
}

