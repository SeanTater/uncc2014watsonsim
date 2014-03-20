

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

import opennlp.tools.parser.Parse;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.parser.Parser;

import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.POSModel;


import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SentenceDetector {
	
	static Scanner input;

    public static void main(String[] args) throws FileNotFoundException {
    	
    	input = new Scanner(System.in);

    	InputStream modelIn = null;
    	InputStream modelIn1 = null;
    	InputStream modelIn2 = null;
    	InputStream modelIn3 = null;
    	InputStream modelIn4 = null;
    	InputStream modelIn5 = null;
    	InputStream modelIn6 = null;
    	
    	modelIn = new FileInputStream("en-pos-maxent.bin");
    	modelIn1 = new FileInputStream("en-token.bin");
    	modelIn2 = new FileInputStream("en-parser-chunking.bin");
    	modelIn3 = new FileInputStream("en-ner-person.bin");
    	modelIn4 = new FileInputStream("en-ner-location.bin");
    	modelIn5 = new FileInputStream("en-ner-date.bin");
    	modelIn6 = new FileInputStream("en-ner-organization.bin");
    	
    	POSModel model = null;
    	TokenizerModel model1 = null;
    	ParserModel model2 = null;
    	TokenNameFinderModel model3 = null;
    	TokenNameFinderModel model4 = null;
    	TokenNameFinderModel model5 = null;
    	TokenNameFinderModel model6 = null;
    	
    	
        try {
           
        	model = new POSModel(modelIn);
        	model1 = new TokenizerModel(modelIn1);
        	model2 = new ParserModel(modelIn2);
        	model3 = new TokenNameFinderModel(modelIn3);
        	model4 = new TokenNameFinderModel(modelIn4);
        	model5 = new TokenNameFinderModel(modelIn5);
        	model6 = new TokenNameFinderModel(modelIn6);
        	
        }
        catch (IOException e) {
          e.printStackTrace();
        }
        finally {
          if (modelIn != null && modelIn1 != null && modelIn2 != null && modelIn3 != null && modelIn4 != null && modelIn5 != null && modelIn6 != null) {
            try {
              modelIn.close();
              modelIn1.close();
              modelIn2.close();
              modelIn3.close();
              modelIn4.close();
              modelIn5.close();
              modelIn6.close();
            }
            catch (IOException e) {
            }
          }
        }
        
       
        
        Tokenizer tokenizer = new TokenizerME(model1);
        System.out.println("Enter sentence \n");
        String sentence;
        sentence = input.nextLine();
        String tokens[] = tokenizer.tokenize(sentence);
        
        POSTaggerME tagger = new POSTaggerME(model);
        String tags[] = tagger.tag(tokens);
        System.out.println("POS tags: ");
        System.out.println(Arrays.toString(tags));
        
        Parser parser = ParserFactory.create(model2);
        Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
        System.out.println("Parsed string: ");
        for(Parse p:topParses)
        	p.show();
        
        NameFinderME nameFinder = new NameFinderME(model3);
        Span nameSpans[] = nameFinder.find(tokens);
        System.out.println("Name Entity Recogntion names: ");
        System.out.println(Arrays.toString(nameSpans));
        if(nameSpans.length != 0)
        {
        	System.out.println("Not null");
        	String pattern = ".*\\\".*\\\".*";
        	Pattern r = Pattern.compile(pattern);
        	Matcher m = r.matcher(sentence);
        	if(m.find())
        	{
        		System.out.println("Has quotes");
        		// Run wiki quotes and google search
        	}
        	else
        	{
        		System.out.println("Factoid question");
        		//Run regular search
        	}
        	
        	
        }
        
        
        NameFinderME locationFinder = new NameFinderME(model4);
        Span nameSpansLocation[] = locationFinder.find(tokens);
        System.out.println("Name Entity Recogntion location: ");
        System.out.println(Arrays.toString(nameSpansLocation));
        
        NameFinderME dateFinder = new NameFinderME(model5);
        Span nameSpansDate[] = dateFinder.find(tokens);
        System.out.println("Name Entity Recogntion date: ");
        System.out.println(Arrays.toString(nameSpansDate));
        
        NameFinderME organizationFinder = new NameFinderME(model6);
        Span nameSpansOrganization[] = organizationFinder.find(tokens);
        System.out.println("Name Entity Recogntion date: ");
        System.out.println(Arrays.toString(nameSpansOrganization));
        
      
        
    }
}