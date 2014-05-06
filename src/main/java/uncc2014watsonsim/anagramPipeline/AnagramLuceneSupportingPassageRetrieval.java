package uncc2014watsonsim.anagramPipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.FSList;
import org.apache.lucene.search.ScoreDoc;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.Translation;
import uncc2014watsonsim.anagram.AnagramQuestion;
import uncc2014watsonsim.anagram.AnagramSolution;
import uncc2014watsonsim.anagram.AnagramSupportingPassage;
import uncc2014watsonsim.anagram.CandidateAnswer;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;

public class AnagramLuceneSupportingPassageRetrieval extends
		JCasAnnotator_ImplBase {
	
	final int MAX_RESULTS = 10;
	
	@Override
	public synchronized void process(JCas aJCas) throws AnalysisEngineProcessException {
		//initialize the lucene stuff
		Analyzer analyzer;
		IndexReader indexReader;
		IndexSearcher indexSearcher;
		QueryParser queryParser;
		List<CandidateAnswer> candidateAnswers = null;
		List<String> queryStrings;
		ScoreDoc[] hits = null;
		
		/**
		 * Retrieve the question information from the Cas
		 */
		AnagramQuestion question = null;
		try {
			question = UimaTools.getSingleton(aJCas, AnagramQuestion.type);
			candidateAnswers = UimaTools.getFSList(question.getCandidateAnswers());
		}
		catch (UimaToolsException e) {
			e.printStackTrace();
		}
		
		/**
		 * Initialize the lucene stuff
		 */
		analyzer = new StandardAnalyzer(Version.LUCENE_46);
		queryParser = new QueryParser(Version.LUCENE_46, "text", analyzer);
		queryParser.setAllowLeadingWildcard(true);
		try {
			indexReader = DirectoryReader.open(FSDirectory.open(new File(UserSpecificConstants.luceneIndex)));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Lucene index is missing. Check that you filled in the right path in UserSpecificConstants.java.");
		}
		indexSearcher = new IndexSearcher(indexReader);
		
		/**
		 * Generate the query strings
		 */
		queryStrings = new ArrayList<String>(candidateAnswers.size());
		for (CandidateAnswer ca : candidateAnswers) {
			queryStrings.add(Translation.getLuceneQueryAnagram(ca.getQuestionText(), ca.getAnswer()));
		}
		
		/**
		 * Get the supporting documents for each query string
		 */
		for (int i = 0; i < queryStrings.size(); i++) {
			String query = queryStrings.get(i);
			CandidateAnswer currentCan = candidateAnswers.get(i);
			FSList supportingPassagesList = new EmptyFSList(aJCas);
			supportingPassagesList.addToIndexes();
			try {
				hits = indexSearcher.search(queryParser.parse(query), MAX_RESULTS).scoreDocs;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			/**
			 * Add the supporting documents to the candidate answer
			 */
			
			for (int j = 0; j < hits.length; j++) {
				ScoreDoc d = hits[j];
				AnagramSupportingPassage asp = new AnagramSupportingPassage(aJCas);
				asp.setSearcherName("lucene");
				Document doc = null;
				try {
					doc = indexSearcher.doc(d.doc);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				asp.setPassageTitle(doc.get("title"));
				asp.setSupportingPassage(doc.get("text"));
				asp.setSearcherRank(j);
				asp.setSearcherScore(d.score);
				asp.addToIndexes();
				try {
					supportingPassagesList = UimaTools.addToFSList(supportingPassagesList, asp);
				}
				catch (UimaToolsException e) {
					e.printStackTrace();
				}
			}
			currentCan.setSupportingPassages(supportingPassagesList);
		}

	}

}
