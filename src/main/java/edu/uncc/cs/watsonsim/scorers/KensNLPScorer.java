package edu.uncc.cs.watsonsim.scorers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import edu.uncc.cs.watsonsim.Question;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;

/**
 * Returns the ratio of the number of noun phrases from the question that are found in the passage
 *   vs. the total number of noun phrases in the passage.
 *  TODO: needs to be fine-tuned but does currently return a score in some cases   
 * 
 * @author Ken Overholt
 *
 */
public class KensNLPScorer extends PassageScorer {

	private boolean modelsAreInitialized=false;	

	public  String modelsPath="data/"; //models directory
	private File parserMFile; 
	private File sentDetectorMFile;
	private File posMFile;

	public SentenceModel sentenceModel; //sentence detection model 
	public ParserModel parserModel; //parsing model
	public POSTaggerME tagger;

	private List<String> questionNPs = new ArrayList<String>();
	private List<String> passageNPs  = new ArrayList<String>();
	

	//initialize all models needed for processing a passage of text (multiple sentences)
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
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.modelsAreInitialized=true;
	}
	
	//find sentences in a passage
	public String[] DivideIntoSentences(Passage p) throws InvalidFormatException{
		init();
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(this.sentenceModel);
		return sentenceDetector.sentDetect(p.text);
	}


	private void navigateTree(Parse[] currentLevel, int position, List<String> results) {
		//System.out.print("text: " + currentLevel[0].getText());			
		//System.out.print("; head: " + currentLevel[0].getHead());
		//System.out.print("; type: " + currentLevel[0].getType());
		//System.out.print("; label: " + currentLevel[0].getLabel());			
		//System.out.println();
		
		/*if (!currentLevel[position].getType().equals("TK")) {
			System.out.print(currentLevel[position].getType() + ": ");
			System.out.println(currentLevel[position].getCoveredText());
		}*/
		
		if (currentLevel[position].getType().equals("NP")) {
			//TODO: remove punctuation
			results.add(currentLevel[position].getCoveredText());
			//System.out.print(currentLevel[position].getType() + ": ");
			//System.out.println(currentLevel[position].getCoveredText());
		}
		
		if (currentLevel[position].getChildCount() < 1) {//leaf node

		} else {//recursively navigate each child
			Parse[] theChildren = currentLevel[position].getChildren();
			for (int i = 0; i < theChildren.length; i++) {	
				navigateTree(currentLevel[position].getChildren(), i, results);
			}
		}
	}
	

	@Override
	public double scorePassage(Phrase q, Answer a, Passage p) {
		
		int countOfQuestionNPsInPassage = 0;
		try {
			//prep NLP tools
			if (!this.modelsAreInitialized) init();
			Parser parser = ParserFactory.create(this.parserModel, 20, 0.95);

			//create question parse
			Parse[] questionParse = ParserTool.parseLine(q.text, parser, 1);

			//create passage parses (one for each sentence)
			String[] passageSentences = this.DivideIntoSentences(p);
			Parse[] passageParses = new Parse[passageSentences.length];
			Parse[] tempParse;
			for (int i=0; i < passageSentences.length; i++) {
				tempParse = ParserTool.parseLine(passageSentences[i], parser, 1);
				passageParses[i] = tempParse[0];
			}
			
			//retrieve NPs from the question parse
			navigateTree(questionParse, 0, questionNPs);

			//retrieve NPs from the passage parse
			for (int i=0; i < passageParses.length; i++) {
				navigateTree(passageParses, i, passageNPs);				
			}
			
			//count the number of question NPs that are in the passage NP set (A)
			for (String qNP: questionNPs) {
				for (String pNP: passageNPs) {
					//System.out.println("comparing " + qNP + " with " + pNP);
					if (qNP.equals(pNP)) {
						//System.out.println("match found");
						countOfQuestionNPsInPassage++;
					}
				}
			}
			//System.out.println(countOfQuestionNPsInPassage);
			
			//count the number of all NPs that are in the passage NP set (B)
			//passageNPs.size();
			
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}

		//calculate A/B and return as the score
		//System.out.print("******** score:  " + (double)countOfQuestionNPsInPassage/passageNPs.size() + "  *******");
		//System.out.println(" count:  " + passageNPs.size() + "  *******");
		if (passageNPs.size() == 0)
			return 0;
		else
			return (double)countOfQuestionNPsInPassage/passageNPs.size();
	}
	
	public static void main(String[] args) {

		//initialize test data
		Phrase q = new Question("This person invented the automobile");
		Passage p = new Passage("ken", "the automobile", "Henry Ford invented the automobile .  He was a great man.  The automobile industry was tranformed into a vast, new opportunity.", "1000");
		Answer a = new Answer("test", "test2", "test3", "test4");

		System.out.println("The score returned: " + new KensNLPScorer().scorePassage(q, a, p));

	}

}
