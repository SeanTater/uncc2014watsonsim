package uncc2014watsonsim.anagramPipeline;

import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;

import uncc2014watsonsim.anagram.AnagramQuestion;
import uncc2014watsonsim.anagram.CandidateAnswer;
import uncc2014watsonsim.anagramPipeline.wordtree.*;
import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
public class AnagramGenerateSolutions extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub
		WordTree tree;
		AnagramSolver anagramSolver = new AnagramSolver();
		AnagramQuestion question;
		FSList CandidateAnswersList = new EmptyFSList(aJCas);
		CandidateAnswersList.addToIndexes();
		try {
			tree = WordTreeGenerator.deserializeWordTree("data/wordtreebinary");
			question = UimaTools.getSingleton(aJCas, AnagramQuestion.type);
		}
		catch (Exception e ) {
			throw new AnalysisEngineProcessException(e);
		}
		
		anagramSolver.setWordTree(tree);
		String [] possibleSolutions = anagramSolver.generateCandidateSolutions(question.getAnagramText());
		for (String str : possibleSolutions) {
			CandidateAnswer ca = new CandidateAnswer(aJCas);
			ca.setAnswer(str);
			ca.setQuestionText(question.getQuestionText());
			ca.addToIndexes();
			try {
				CandidateAnswersList = UimaTools.addToFSList(CandidateAnswersList, ca);
			}
			catch (UimaToolsException e) {
				e.printStackTrace();
			}
		}
		question.setCandidateAnswers(CandidateAnswersList);
	}
	
}
