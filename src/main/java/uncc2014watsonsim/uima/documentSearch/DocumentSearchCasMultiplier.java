/**
 * 
 */
package uncc2014watsonsim.uima.documentSearch;

import org.apache.uima.analysis_component.JCasMultiplier_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.AbstractCas;
import org.apache.uima.jcas.JCas;

/**
 * @author Jonathan Shuman
 * This class will generate new CASes filled with documents from each search engine.
 * It will be called from the DocumentSearchApplicationDescriptor which
 * is responsible for generating the new Top level Cas
 */
public class DocumentSearchCasMultiplier extends JCasMultiplier_ImplBase{

	@Override
	public boolean hasNext() throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AbstractCas next() throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub
		
	}

}
