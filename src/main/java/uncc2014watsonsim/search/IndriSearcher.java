package uncc2014watsonsim.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import privatedata.UserSpecificConstants;
import uncc2014watsonism.qAnalysis.FITBAnnotations;
import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.Score;
import uncc2014watsonsim.Translation;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

/**
 *
 * @author Phani Rahul
 * @author Ken Overholt - added Passage Retrieval search
 */
public class IndriSearcher extends Searcher {
	private static QueryEnvironment q;
	static {
		// Only initialize the query environment and index once
		q = new QueryEnvironment();
		
	}

	public List<Passage> runQuery(String query) throws Exception {
		// Run the query
		
		// Either add the Indri index or die.
		try {
			q.addIndex(UserSpecificConstants.indriIndex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Indri index is missing or corrupt. Please check that you entered the right path in UserSpecificConstants.java.");
		}
		
		String main_query = Translation.getIndriQuery(query);
		
		ScoredExtentResult[] ser = IndriSearcher.q.runQuery(main_query, MAX_RESULTS);

		// Fetch all titles, texts
		String[] docnos = IndriSearcher.q.documentMetadata(ser, "docno");
		
		// If they have them, get the titles and full texts
		ParsedDocument[] full_texts = IndriSearcher.q.documents(ser);
		String[] titles = IndriSearcher.q.documentMetadata(ser, "title");

		// Compile them into a uniform format
		List<Passage> results = new ArrayList<Passage>();
		for (int i=0; i<ser.length; i++) {
	    	results.add(new Passage(
    			"indri",         	// Engine
    			titles[i],	        // Title
    			full_texts[i].text, // Full Text
				docnos[i])          // Reference
			.score(Score.INDRI_RANK, (double) i)
			.score(Score.INDRI_SCORE, ser[i].score));
		}
		// Indri's titles and full texts could be empty. If they are, fill them from sources.db
		return fillFromSources(results);
	}
	
	/**
	 * Search Indri passages for FITB-specific results
	 * @param question
	 * @return
	 * @throws Exception
	 */
	public List<Answer> runFitbQuery(Question question) throws Exception {
    	//regex: "(([^"\r\n\s]+)\b[:;?!.]?(\s)*){2,}"

		// Run the query
		
		// Either add the Indri index or die.
		try {
			q.addIndex(UserSpecificConstants.indriIndex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Indri index is missing or corrupt. Please check that you entered the right path in UserSpecificConstants.java.");
		}
		
		String main_query = Translation.getIndriPassageRetrievalQuery(question);
		
		ScoredExtentResult[] ser = IndriSearcher.q.runQuery(main_query, MAX_RESULTS);
		// Fetch all titles, texts
		String[] docnos = IndriSearcher.q.documentMetadata(ser, "docno");
		
		// If they have them, get the titles and full texts
		ParsedDocument[] full_texts = IndriSearcher.q.documents(ser);
		String[] titles = IndriSearcher.q.documentMetadata(ser, "title");

		//System.out.println("The titles are:"); //for debug
		//for (String t: titles) {				//for debug
		//	System.out.println("  title: " + t);
		//}
		
		// Compile them into a uniform format
		List<Answer> results = new ArrayList<Answer>();
		for (int i=0; i<ser.length; i++) {
	    	results.add(new Answer(
    			"indri",         	// Engine
    			titles[i],	        // Title
    			full_texts[i].text, // Full Text
				docnos[i])          // Reference
			.score(Score.INDRI_PASSAGE_RETRIEVAL_RANK, (double) i)
			.score(Score.INDRI_PASSAGE_RETRIEVAL_SCORE, ser[i].score));
		}
		setFitbTitles(question, results);
		return results;
	}
	
	/**
	 * Sets the values of the Titles in theList to the values the search says
	 *  should be in the FITB blanks
	 *  
	 *  @author Ken Overholt
	 */
	private void setFitbTitles (Question question, List<Answer> theList) {
		//Pattern fullPattern = Pattern.compile("\"(([^\"\\r\\n\\s]+)\\b[:;?!,.]?(\\s)*){2,}\"");
		//regex: "(([^"\r\n\s]+)\b[:;?!.]?(\s)*){2,}"
    	FITBAnnotations annot = question.getFITBAnnotations(); //temp annotation holder
    	String theText = question.getRaw_text();
    	String section1 = theText.substring(annot.getSection1Begin(), annot.getSection1End());
    	String section2 = theText.substring(annot.getSection2Begin(), annot.getSection2End());
        //section1 = section1.replaceAll("\\", "\\\\");
        //section2 = section2.replaceAll("\\", "\\\\");
        section1 = section1.replaceAll("\"", ""); //remove quotes
        section2 = section2.replaceAll("\"", ""); //remove quotes
        //TODO: make search more flexible (such as removing punctuation)
        
    	StringBuffer str1 = new StringBuffer();
    	str1.append("(?i)");
        if (!section1.trim().equals("")) str1.append(section1);
        str1.append(".*");//append blanks here
        if (!section2.trim().equals("")) str1.append(section2);
        //System.out.println("str1: " + str1); //for debug
    	
		Matcher matcher1;
		Pattern pattern1 = Pattern.compile(str1.toString());
		int docPatternStart = -1; //start location of the found pattern within a document
		int docPatternEnd = -1; //end location of a found pattern within a document
		Matcher matcher2;
		Matcher matcher3;
		Pattern pattern2 = Pattern.compile("(?i)" + section1);
		Pattern pattern3 = Pattern.compile("(?i)" + section2);
		int answerStartLocation = -1;
		int answerEndLocation = -1;
		String result = null;
		
		for (Answer a: theList) {
			matcher1 = pattern1.matcher(a.getFullText()); //title is the text to be searched
			if (matcher1.find()) {	//find the question with blanks (str1) within the document (a.getFullText()); assign result as str1substring (question with blanks filled in)
				docPatternStart = matcher1.start();
				docPatternEnd = matcher1.end();
				String str1substring = a.getFullText().substring(docPatternStart, docPatternEnd);
				//System.out.println("found pattern in doc: " + str1substring + ": title: " + a.getTitle()); //for debug

				answerStartLocation = 0;
				answerEndLocation = str1substring.length();

				matcher2 = pattern2.matcher(str1substring);
				if (matcher2.find()) {
					answerStartLocation = matcher2.end();
					//System.out.print("answerStartLocation: " + answerStartLocation); //for debug
				};
				
				matcher3 = pattern3.matcher(str1substring);
				if (matcher3.find(answerStartLocation)) {
					answerEndLocation = matcher3.start();
					//System.out.println(" answerEndLocation: " + answerEndLocation); //for debug
				} else { //no final section found so it must be one or more blanks
					//TODO: add the number of words (from the doc) for the number of blanks in the question
					
				}
				
				result = str1substring.substring(answerStartLocation,answerEndLocation);
				//System.out.println("The answer: " + result); //for debug
				if (result != null && !result.equals("")) {
					a.setTitle(result);
				}
				else {
					a.setTitle("result was blank or null");
				}
				
			};
		}
		
	}

}

