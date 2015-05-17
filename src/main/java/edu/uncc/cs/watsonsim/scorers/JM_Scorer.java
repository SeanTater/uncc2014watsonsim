package edu.uncc.cs.watsonsim.scorers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

public class JM_Scorer extends PassageScorer{
	public double matchChildren(Parse pa1, Parse pa2) {
		String p1NodeLabel = pa1.getLabel();
		String p2NodeLabel = pa2.getLabel();
		Parse[] children1 = pa1.getChildren();
		Parse[] children2 = pa2.getChildren();
		double matchFound = 0;
		
		if (pa1 == null || pa2 == null) {
			return 0;
		}
		
		if (p1NodeLabel.equals(p2NodeLabel)) {
			if (pa1.getCoveredText().equals(pa2.getCoveredText())) {
				matchFound = 1;
			}
		}
		
		return matchFound + matchChildren(children1[0], children2[0]) + matchChildren(children1[1], children2[1]);
	}
	
	//a simple scorer based on the number of matches; requires the first string to be in the passage
	public double scoreStructure(String ca, String q, String passage, boolean verbose) throws InvalidFormatException, IOException{
		POSTaggerME parserModel = new POSTaggerME(new POSModel(new FileInputStream(new File("en-pos-model.bin"))));
		Tokenizer tokenizer = new TokenizerME(new TokenizerModel(new FileInputStream(new File("en-token.bin"))));
		Parser parser = ParserFactory.create(new ParserModel(new FileInputStream(new File("en-parser.bin"))));
		double score = 0;
		
		Parse[] questionParse = ParserTool.parseLine(q, parser, 1);
		Parse[] passageParse = ParserTool.parseLine(q, parser, 1);
		
		if (passage.contains(ca)) {
			for (int i =0; i < questionParse.length; i++) {
				score += matchChildren(questionParse[i],passageParse[i]);
			}
		}
		
		return score;
	}
	
	public double scorePassage(Phrase q, Answer a, Passage p) {
		try {
			p.score("JM_Scorer", scoreStructure(q.text, a.text, p.text, false));
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Double.NaN;
	}
}
