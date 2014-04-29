package uncc2014watsonsim.research;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.parser.Parse;
import opennlp.tools.util.InvalidFormatException;

public class PassageScorerOpenNLPAda {
		
	OpenNlpTests t = new OpenNlpTests();
	public double compareParseType(Parse[] pa1, Parse[] pa2, boolean verbose){
		double numMatches=0;
		Map key1 = new HashMap();
		for (int i=0;i<pa1.length;i++){
			key1.put(pa1[i].getType(),"y");
			//pa1h.put(key[0],"y");
		}
		for (int j=0;j<pa2.length;j++){
			String key2=pa2[j].getType();
			if (key1.containsKey(key2)){ 
				numMatches++;
				if (verbose) System.out.println("\n");
				pa2[j].show();
				if (verbose) System.out.println("type: "+pa2[j].getType());
			}
		}
		if (verbose) System.out.println("numTypeMatches "+numMatches);
		return numMatches;
	}
	
	
	public double scoreStructure(String ca, String q, String passage, boolean verbose) throws InvalidFormatException{
		double score1=0, score2=0;
		Parse[] caParse = t.parsePassageText(ca);
		Parse[] qParse = t.parsePassageText(q);
		Parse[] pasParse = t.parsePassageText(passage);
		Parse[] caParseCh = t.getAllChildren(caParse);
		Parse[] qParseCh = t.getAllChildren(qParse);
		Parse[] pasParseCh = t.getAllChildren(pasParse);
		score1=this.compareParseType(qParseCh, pasParseCh,verbose);
		score2=this.compareParseType(caParseCh, pasParseCh,verbose);
		return score1*score2;
	}

	//normalized scorer. 
	public double scoreStructureNorm(String ca, String q, String passage, boolean verbose) throws InvalidFormatException{
		double score1=0, score2=0;
		//OnlpParserTest pt= new OnlpParserTest();
		Parse[] caParse = t.parsePassageText(ca);
		Parse[] qParse = t.parsePassageText(q);
		Parse[] pasParse = t.parsePassageText(passage);
		Parse[] caParseCh = t.getAllChildren(caParse);
		Parse[] qParseCh = t.getAllChildren(qParse);
		Parse[] pasParseCh = t.getAllChildren(pasParse);
		score1=this.compareParseType(qParseCh, pasParseCh,verbose);
		score2=this.compareParseType(caParseCh, pasParseCh,verbose);
		return score1*score2/passage.length();
	}


public static void main(String[] args) throws IOException{
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
	PassageScorerOpenNLPAda ps = new PassageScorerOpenNLPAda(); 
	System.out.println();
	System.out.println("NormalizedScore: "+ps.scoreStructureNorm(ca,qq, passage,true));
	System.out.println("Raw Score: "+ps.scoreStructure(ca,qq, passage,true));
}
}
