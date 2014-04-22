/**
 * 
 */
package uncc2014watsonsim.sources.uima.documentSearch;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_component.JCasMultiplier_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.AbstractCas;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasCopier;

import uncc2014watsonsim.uima.UimaTools;
import uncc2014watsonsim.uima.UimaToolsException;
import uncc2014watsonsim.uima.types.SearchResult;
import uncc2014watsonsim.uima.types.searchResultList;

/**
 * @author Columbia Watsonsim team
 * @author Jonathan Shuman (modifications for uncc watsonsim2014)
 * This class will generate new CASes filled with documents from each search engine.
 * It will be called from the DocumentSearchApplicationDescriptor which
 * is responsible for generating the new Top level Cas.
 * This is here to create different CASes for each search engine.
 */
public class DocumentSearchCasMultiplier extends JCasMultiplier_ImplBase{

	 private JCas baseCas;

	  private List<SearchResult> searches;

	  private String queryIdPrefix;

	  private int nextIndex;

	  private boolean primary;

	  /* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.AnalysisComponent#hasNext()
	 */
	@Override
	  public boolean hasNext() throws AnalysisEngineProcessException {
	    return nextIndex < searches.size();
	  }

	  /* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.AnalysisComponent#next()
	 */
	@Override
	  public AbstractCas next() throws AnalysisEngineProcessException {
		  SearchResult nextHit = searches.get(nextIndex++);
	    JCas newCas = getEmptyJCas();
	    CasCopier copier = new CasCopier(baseCas.getCas(), newCas.getCas());
	    try {
	      if (UimaTools.casContainsView(baseCas, "QUERY"))
	        copier.copyCasView(baseCas.getView("QUERY").getCas(), true);
	      // (UimaTools.casContainsView(baseCas, "EXPANDED"))
	      //  copier.copyCasView(baseCas.getView("EXPANDED").getCas(), true);
	    } catch (CASException e) {
	      throw new AnalysisEngineProcessException(e);
	    }
	    try {
	      JCas docView = newCas.createView("DOCUMENT");
	      docView.setDocumentLanguage("en");
	      docView.setDocumentText(nextHit.getFullText());
	    } catch (Exception e) {
	      throw new AnalysisEngineProcessException(e);
	    }
	    return newCas;
	  }

	  /* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.JCasMultiplier_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	  public void process(JCas arg0) throws AnalysisEngineProcessException {
	    baseCas = arg0;
	    JCas queryView = null;
	    try {
	      queryView = arg0.getView("QUERY");
	    } catch (CASException e) {
	      throw new AnalysisEngineProcessException(e);
	    }
	    if (queryView == null)
	      throw new AnalysisEngineProcessException(new Throwable("Expecting QUERY view in CAS"));
	    try {
	      JCas initView = arg0.getView(CAS.NAME_DEFAULT_SOFA);
	      queryIdPrefix = ((SourceDocumentInformation) UimaTools.getSingleton(initView,
	              SourceDocumentInformation.type)).getUri().replaceAll("^file:/", "");
	      primary = true;
	      if (UimaTools.casContainsView(arg0, "EXPANDED")) {
	        queryView = arg0.getView("EXPANDED");
	        primary = false;
	      }
	      searchResultList searchList = UimaTools.getSingleton(queryView, searchResultList.type);
	      if (searchList != null && searchList.getList() != null)
	        searches = UimaTools.getFSList(searchList.getList());
	      else
	        searches = new ArrayList<SearchResult>();
	    } catch (CASException e) {
	      throw new AnalysisEngineProcessException(e);
	    } catch (UimaToolsException e) {
	      throw new AnalysisEngineProcessException(e);
	    }
	    nextIndex = 0;
	  }

}
