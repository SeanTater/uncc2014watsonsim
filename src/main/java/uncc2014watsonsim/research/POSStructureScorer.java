package uncc2014watsonsim.research;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;


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

//@author Dhaval Patel
/*
 * Scores based on matching POS structure of 'Question+Answer' and 'each sentence of passage'.
 * I haven't made it extending the PassageScorer. Just showed how it works with sample strings.
 * 
 */

public class POSStructureScorer {

	public static String modelsPath = "PASTE-PATH-HERE"; // models directory
	private static File parserMFile;
	private static File sentDetectorMFile;
	private static File posMFile;

	public static SentenceModel sentenceModel; // sentence detection model
	public static ParserModel parserModel; // parsing model
	public static POSTaggerME tagger;
	

	public static void init() throws InvalidFormatException {
		File modelsDir = new File(modelsPath);

		parserMFile = new File(modelsDir, "en-parser-chunking.bin");
		sentDetectorMFile = new File(modelsDir, "en-sent.bin");
		posMFile = new File(modelsDir, "en-pos-maxent.bin");

		InputStream sentModelIn = null;
		FileInputStream parserStream;
		try {
			// for finding sentences
			sentModelIn = new FileInputStream(sentDetectorMFile);
			sentenceModel = new SentenceModel(sentModelIn);
			// for finding POS
			FileInputStream posModelStream = new FileInputStream(posMFile);
			POSModel model = new POSModel(posModelStream);
			tagger = new POSTaggerME(model);
			// for parsing
			parserStream = new FileInputStream(parserMFile);
			parserModel = new ParserModel(parserStream);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static Parse[] parsePassageText(String p) throws InvalidFormatException{
		
		//initialize 	 
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);
		Parser parser = ParserFactory.create(
				parserModel,
				20, // beam size
				0.95); // advance percentage
	 	 	 
		String[] sentences = sentenceDetector.sentDetect(p);
		Parse[] results = new Parse[sentences.length];
		for (int i=0;i<sentences.length;i++){
			String[] tks = SimpleTokenizer.INSTANCE.tokenize(sentences[i]);
	

			String sent= StringUtils.join(tks," ");
			System.out.println("Found sentence " + sent);
			Parse[] sentResults = ParserTool.parseLine(sent,parser, 1);
			results[i]=sentResults[0];
		}
		return results;
	}

	public static void main(String[] args) throws InvalidFormatException {


		
		init();
		
		String sampleQuestion = "Jane Austen";
		String sampleAnswer = "Jane Austen wrote Emma";
		String samplePassage = "Jane Austen was very modest about her own genius.[7] She once famously described her work as "+
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
		
		
		String sampleQACombined = sampleAnswer + sampleQuestion;
		Parse[] sentences = parsePassageText(samplePassage);
		
		int[] scorerModelQA = POSScoreSentece(sampleQACombined);
		int[] scorerModelEachSentenceInPassage;
		double tempScore = 0;
		double finalScore = 0;
		for (int i = 0; i < sentences.length; i++) {
			scorerModelEachSentenceInPassage = POSScoreSentece(sentences[i].toString());
			tempScore = AbsoluteScorerModelSubtractor(scorerModelQA,scorerModelEachSentenceInPassage);
			System.out.println("tempScore = "+tempScore);
			if(tempScore<= 0.1*sentences[i].toString().length())
			{
				finalScore = finalScore + tempScore;
			}
		}
		
		System.out.println("Final Score is : " + finalScore);

	}

	private static double AbsoluteScorerModelSubtractor(int[] scorerModelQA,
			int[] scorerModelEachSentenceInPassage) {
		// TODO Auto-generated method stub
		return Math.abs(scorerModelQA[0]-scorerModelEachSentenceInPassage[0])+
				Math.abs(scorerModelQA[1]-scorerModelEachSentenceInPassage[1])+
				Math.abs(scorerModelQA[2]-scorerModelEachSentenceInPassage[2])+
				Math.abs(scorerModelQA[3]-scorerModelEachSentenceInPassage[3])+
				Math.abs(scorerModelQA[4]-scorerModelEachSentenceInPassage[4])+
				Math.abs(scorerModelQA[5]-scorerModelEachSentenceInPassage[5]);
		
		
	}

	private static int[] POSScoreSentece(String sampleQACombined) {
		// TODO Auto-generated method stub
		
		
		int[] scorerModel = { 0, 0, 0, 0, 0, 0 };
		String[] words = SimpleTokenizer.INSTANCE.tokenize(sampleQACombined);
		String[] result = tagger.tag(words);
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i]);
		}
		for (int i=0 ; i < words.length; i++) {
			if(result[i].equals("CD")){
				scorerModel[0]++;
			}else if(result[i].equals("EX")){
				scorerModel[1]++;
			}else if(result[i].equals("JJ") || result[i].equals("JJR") || result[i].equals("JJS")){
				
				scorerModel[2]++;
			}else if(result[i].equals("NN") || result[i].equals("NNS") || result[i].equals("NNP") || result[i].equals("NNPS")){
				scorerModel[3]++;
			}else if(result[i].equals("RB") || result[i].equals("RBR") || result[i].equals("RBS")){
				scorerModel[4]++;
			}else if(result[i].equals("VB") || result[i].equals("VBD") || result[i].equals("VBG") || result[i].equals("VBN") || result[i].equals("VBP") || result[i].equals("VBZ")){
				scorerModel[5]++;
			}
		}
		return scorerModel;
	}
}
