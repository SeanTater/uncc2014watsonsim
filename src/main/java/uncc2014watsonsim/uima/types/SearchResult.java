

/* First created by JCasGen Wed Mar 19 16:09:32 EDT 2014 */
package uncc2014watsonsim.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Sun Apr 06 17:17:36 EDT 2014
 * XML source: /home/jonathan/workspace/uncc2014watsonsim/src/main/java/uncc2014watsonsim/uima/types.xml
 * @generated */
public class SearchResult extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SearchResult.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected SearchResult() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public SearchResult(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public SearchResult(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
  //*--------------*
  //* Feature: title

  /** getter for title - gets Title of the retrieved article
   * @generated
   * @return value of the feature 
   */
  public String getTitle() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "uncc2014watsonsim.uima.types.searchResult");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_title);}
    
  /** setter for title - sets Title of the retrieved article 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTitle(String v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "uncc2014watsonsim.uima.types.searchResult");
    jcasType.ll_cas.ll_setStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_title, v);}    
   
    
  //*--------------*
  //* Feature: fullText

  /** getter for fullText - gets Text of this search result
   * @generated
   * @return value of the feature 
   */
  public String getFullText() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_fullText == null)
      jcasType.jcas.throwFeatMissing("fullText", "uncc2014watsonsim.uima.types.searchResult");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_fullText);}
    
  /** setter for fullText - sets Text of this search result 
   * @generated
   * @param v value to set into the feature 
   */
  public void setFullText(String v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_fullText == null)
      jcasType.jcas.throwFeatMissing("fullText", "uncc2014watsonsim.uima.types.searchResult");
    jcasType.ll_cas.ll_setStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_fullText, v);}    
   
    
  //*--------------*
  //* Feature: reference

  /** getter for reference - gets A Reference
   * @generated
   * @return value of the feature 
   */
  public String getReference() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_reference == null)
      jcasType.jcas.throwFeatMissing("reference", "uncc2014watsonsim.uima.types.searchResult");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_reference);}
    
  /** setter for reference - sets A Reference 
   * @generated
   * @param v value to set into the feature 
   */
  public void setReference(String v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_reference == null)
      jcasType.jcas.throwFeatMissing("reference", "uncc2014watsonsim.uima.types.searchResult");
    jcasType.ll_cas.ll_setStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_reference, v);}    
   
    
  //*--------------*
  //* Feature: rank

  /** getter for rank - gets Rank of this result from the engine
   * @generated
   * @return value of the feature 
   */
  public long getRank() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_rank == null)
      jcasType.jcas.throwFeatMissing("rank", "uncc2014watsonsim.uima.types.searchResult");
    return jcasType.ll_cas.ll_getLongValue(addr, ((SearchResult_Type)jcasType).casFeatCode_rank);}
    
  /** setter for rank - sets Rank of this result from the engine 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRank(long v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_rank == null)
      jcasType.jcas.throwFeatMissing("rank", "uncc2014watsonsim.uima.types.searchResult");
    jcasType.ll_cas.ll_setLongValue(addr, ((SearchResult_Type)jcasType).casFeatCode_rank, v);}    
   
    
  //*--------------*
  //* Feature: engine

  /** getter for engine - gets Name of the search engine
   * @generated
   * @return value of the feature 
   */
  public String getEngine() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_engine == null)
      jcasType.jcas.throwFeatMissing("engine", "uncc2014watsonsim.uima.types.searchResult");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_engine);}
    
  /** setter for engine - sets Name of the search engine 
   * @generated
   * @param v value to set into the feature 
   */
  public void setEngine(String v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_engine == null)
      jcasType.jcas.throwFeatMissing("engine", "uncc2014watsonsim.uima.types.searchResult");
    jcasType.ll_cas.ll_setStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_engine, v);}    
   
    
  //*--------------*
  //* Feature: score

  /** getter for score - gets The combined, or not combined, score of the result
   * @generated
   * @return value of the feature 
   */
  public double getScore() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "uncc2014watsonsim.uima.types.searchResult");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((SearchResult_Type)jcasType).casFeatCode_score);}
    
  /** setter for score - sets The combined, or not combined, score of the result 
   * @generated
   * @param v value to set into the feature 
   */
  public void setScore(double v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "uncc2014watsonsim.uima.types.searchResult");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((SearchResult_Type)jcasType).casFeatCode_score, v);}    
  }

    