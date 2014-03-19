package uncc2014watsonsim.uima.documentSearch;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;

import uncc2014watsonsim.uima.types.*;

/**
 * This is a sample primary document search AE. It expects a CAS with a QUERY view, and uses the
 * contents of the QUERY view to perform a document search. The output from this AE is a
 * SearchHitList added to the QUERY view of the CAS.
 * 
 * This is currently not doing anything, it is simply here so we can generate the rest of the xml files at this time (3/19/14)
 *
 * @author Jonathan Shuman 
 * with help from original:
 * @author Siddharth Patwardhan <sidd@patwardhans.net>
 */
public class DummyDocumentSearch extends JCasAnnotator_ImplBase {

  private static final String COMPONENT_ID = "DummyDocumentSearch";

  /**
   * Any initializations of data structures/engines (e.g., a retrieval engine) would go into the
   * initialize method. In this dummy class, we don't actually use a retrieval engine.
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
  }

  /**
   * The process method receives one "query" CAS at a time, and performs a document search for each.
   */
  @Override
  public void process(JCas cas) throws AnalysisEngineProcessException {
    JCas queryView;
    try {
      queryView = cas.getView("QUERY");
    } catch (CASException e) {
      throw new AnalysisEngineProcessException(e);
    }
    if (queryView == null)
      throw new AnalysisEngineProcessException(new Throwable(
              "Expecting QUERY view in CAS for primary document search"));
    try {
      if (UimaTools.casContainsView(cas, "EXPANDED")) //Shuman: need to figure out what all this does
        throw new AnalysisEngineProcessException(
                new Throwable(
                        "Flow problem: found EXPANDED view in CAS; primary search CASes must not have EXPANDED view"));
    } catch (CASException e) {
      throw new AnalysisEngineProcessException(e);
    }

    searchResultList hits;
    try {
      hits = UimaTools.getSingleton(queryView, searchResultList.type); 
    } catch (UimaToolsException e) {
      throw new AnalysisEngineProcessException(e);
    }
    if (hits == null) {
      hits = new SearchHitList(queryView);
      hits.setList(new EmptyFSList(queryView));
      hits.addToIndexes();
    }

    // At this point we would perform a document search using a retrieval engine. However, in this
    // dummy class, we simply create a bunch of dummy search hits.

    SearchHit hit0 = new SearchHit(queryView);
    hit0.setRank(0);
    hit0.setScore(0.9);
    hit0.setComponentId(COMPONENT_ID);
    hit0.setText("This is the first hit for " + queryView.getDocumentText());
    FSList expandedList;
    try {
      expandedList = UimaTools.addToFSList(hits.getList(), hit0);
    } catch (UimaToolsException e) {
      throw new AnalysisEngineProcessException(e);
    }
    hits.setList(expandedList);

    SearchHit hit1 = new SearchHit(queryView);
    hit1.setRank(1);
    hit1.setScore(0.8);
    hit1.setComponentId(COMPONENT_ID);
    hit1.setText("This is the second hit for " + queryView.getDocumentText());
    try {
      expandedList = UimaTools.addToFSList(hits.getList(), hit1);
    } catch (UimaToolsException e) {
      throw new AnalysisEngineProcessException(e);
    }
    hits.setList(expandedList);

    SearchHit hit2 = new SearchHit(queryView);
    hit2.setRank(2);
    hit2.setScore(0.7);
    hit2.setComponentId(COMPONENT_ID);
    hit2.setText("This is the third hit for " + queryView.getDocumentText());
    try {
      expandedList = UimaTools.addToFSList(hits.getList(), hit0);
    } catch (UimaToolsException e) {
      throw new AnalysisEngineProcessException(e);
    }
    hits.setList(expandedList);
  }

  /**
   * Any cleanup to be performed at the end of processing should go into this
   * collectionProcessComplete() method (e.g., closing any open files/connections, etc.)
   */
  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    super.collectionProcessComplete();
  }

}
