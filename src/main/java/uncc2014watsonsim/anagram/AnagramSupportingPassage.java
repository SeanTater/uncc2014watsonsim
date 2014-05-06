

/* First created by JCasGen Mon May 05 22:20:46 EDT 2014 */
package uncc2014watsonsim.anagram;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue May 06 03:49:45 EDT 2014
 * XML source: C:/Users/Jacob/Documents/GitHub/uncc2014watsonsim/src/main/java/uncc2014watsonsim/anagramPipeline/AnagramSupportingPassageScoreEngine.xml
 * @generated */
public class AnagramSupportingPassage extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AnagramSupportingPassage.class);
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
  protected AnagramSupportingPassage() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public AnagramSupportingPassage(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public AnagramSupportingPassage(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public AnagramSupportingPassage(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
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
  //* Feature: passageTitle

  /** getter for passageTitle - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPassageTitle() {
    if (AnagramSupportingPassage_Type.featOkTst && ((AnagramSupportingPassage_Type)jcasType).casFeat_passageTitle == null)
      jcasType.jcas.throwFeatMissing("passageTitle", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AnagramSupportingPassage_Type)jcasType).casFeatCode_passageTitle);}
    
  /** setter for passageTitle - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPassageTitle(String v) {
    if (AnagramSupportingPassage_Type.featOkTst && ((AnagramSupportingPassage_Type)jcasType).casFeat_passageTitle == null)
      jcasType.jcas.throwFeatMissing("passageTitle", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    jcasType.ll_cas.ll_setStringValue(addr, ((AnagramSupportingPassage_Type)jcasType).casFeatCode_passageTitle, v);}    
   
    
  //*--------------*
  //* Feature: supportingPassage

  /** getter for supportingPassage - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSupportingPassage() {
    if (AnagramSupportingPassage_Type.featOkTst && ((AnagramSupportingPassage_Type)jcasType).casFeat_supportingPassage == null)
      jcasType.jcas.throwFeatMissing("supportingPassage", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AnagramSupportingPassage_Type)jcasType).casFeatCode_supportingPassage);}
    
  /** setter for supportingPassage - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSupportingPassage(String v) {
    if (AnagramSupportingPassage_Type.featOkTst && ((AnagramSupportingPassage_Type)jcasType).casFeat_supportingPassage == null)
      jcasType.jcas.throwFeatMissing("supportingPassage", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    jcasType.ll_cas.ll_setStringValue(addr, ((AnagramSupportingPassage_Type)jcasType).casFeatCode_supportingPassage, v);}    
   
    
  //*--------------*
  //* Feature: searcherName

  /** getter for searcherName - gets The name of the searcher that returned this supporting passage object
   * @generated
   * @return value of the feature 
   */
  public String getSearcherName() {
    if (AnagramSupportingPassage_Type.featOkTst && ((AnagramSupportingPassage_Type)jcasType).casFeat_searcherName == null)
      jcasType.jcas.throwFeatMissing("searcherName", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AnagramSupportingPassage_Type)jcasType).casFeatCode_searcherName);}
    
  /** setter for searcherName - sets The name of the searcher that returned this supporting passage object 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSearcherName(String v) {
    if (AnagramSupportingPassage_Type.featOkTst && ((AnagramSupportingPassage_Type)jcasType).casFeat_searcherName == null)
      jcasType.jcas.throwFeatMissing("searcherName", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    jcasType.ll_cas.ll_setStringValue(addr, ((AnagramSupportingPassage_Type)jcasType).casFeatCode_searcherName, v);}    
   
    
  //*--------------*
  //* Feature: searcherRank

  /** getter for searcherRank - gets The rank that the supporting passage got
   * @generated
   * @return value of the feature 
   */
  public int getSearcherRank() {
    if (AnagramSupportingPassage_Type.featOkTst && ((AnagramSupportingPassage_Type)jcasType).casFeat_searcherRank == null)
      jcasType.jcas.throwFeatMissing("searcherRank", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    return jcasType.ll_cas.ll_getIntValue(addr, ((AnagramSupportingPassage_Type)jcasType).casFeatCode_searcherRank);}
    
  /** setter for searcherRank - sets The rank that the supporting passage got 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSearcherRank(int v) {
    if (AnagramSupportingPassage_Type.featOkTst && ((AnagramSupportingPassage_Type)jcasType).casFeat_searcherRank == null)
      jcasType.jcas.throwFeatMissing("searcherRank", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    jcasType.ll_cas.ll_setIntValue(addr, ((AnagramSupportingPassage_Type)jcasType).casFeatCode_searcherRank, v);}    
   
    
  //*--------------*
  //* Feature: searcherScore

  /** getter for searcherScore - gets The score of the passage, if the searcher has one. If it doesn't, this value should be set to zero
   * @generated
   * @return value of the feature 
   */
  public double getSearcherScore() {
    if (AnagramSupportingPassage_Type.featOkTst && ((AnagramSupportingPassage_Type)jcasType).casFeat_searcherScore == null)
      jcasType.jcas.throwFeatMissing("searcherScore", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((AnagramSupportingPassage_Type)jcasType).casFeatCode_searcherScore);}
    
  /** setter for searcherScore - sets The score of the passage, if the searcher has one. If it doesn't, this value should be set to zero 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSearcherScore(double v) {
    if (AnagramSupportingPassage_Type.featOkTst && ((AnagramSupportingPassage_Type)jcasType).casFeat_searcherScore == null)
      jcasType.jcas.throwFeatMissing("searcherScore", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((AnagramSupportingPassage_Type)jcasType).casFeatCode_searcherScore, v);}    
  }

    