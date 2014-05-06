

/* First created by JCasGen Tue May 06 01:26:28 EDT 2014 */
package uncc2014watsonsim.anagram;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Tue May 06 03:49:45 EDT 2014
 * XML source: C:/Users/Jacob/Documents/GitHub/uncc2014watsonsim/src/main/java/uncc2014watsonsim/anagramPipeline/AnagramSupportingPassageScoreEngine.xml
 * @generated */
public class CandidateAnswer extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CandidateAnswer.class);
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
  protected CandidateAnswer() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public CandidateAnswer(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CandidateAnswer(JCas jcas) {
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
  //* Feature: answer

  /** getter for answer - gets 
   * @generated
   * @return value of the feature 
   */
  public String getAnswer() {
    if (CandidateAnswer_Type.featOkTst && ((CandidateAnswer_Type)jcasType).casFeat_answer == null)
      jcasType.jcas.throwFeatMissing("answer", "uncc2014watsonsim.anagram.CandidateAnswer");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CandidateAnswer_Type)jcasType).casFeatCode_answer);}
    
  /** setter for answer - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnswer(String v) {
    if (CandidateAnswer_Type.featOkTst && ((CandidateAnswer_Type)jcasType).casFeat_answer == null)
      jcasType.jcas.throwFeatMissing("answer", "uncc2014watsonsim.anagram.CandidateAnswer");
    jcasType.ll_cas.ll_setStringValue(addr, ((CandidateAnswer_Type)jcasType).casFeatCode_answer, v);}    
   
    
  //*--------------*
  //* Feature: questionText

  /** getter for questionText - gets 
   * @generated
   * @return value of the feature 
   */
  public String getQuestionText() {
    if (CandidateAnswer_Type.featOkTst && ((CandidateAnswer_Type)jcasType).casFeat_questionText == null)
      jcasType.jcas.throwFeatMissing("questionText", "uncc2014watsonsim.anagram.CandidateAnswer");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CandidateAnswer_Type)jcasType).casFeatCode_questionText);}
    
  /** setter for questionText - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setQuestionText(String v) {
    if (CandidateAnswer_Type.featOkTst && ((CandidateAnswer_Type)jcasType).casFeat_questionText == null)
      jcasType.jcas.throwFeatMissing("questionText", "uncc2014watsonsim.anagram.CandidateAnswer");
    jcasType.ll_cas.ll_setStringValue(addr, ((CandidateAnswer_Type)jcasType).casFeatCode_questionText, v);}    
   
    
  //*--------------*
  //* Feature: supportingPassages

  /** getter for supportingPassages - gets 
   * @generated
   * @return value of the feature 
   */
  public FSList getSupportingPassages() {
    if (CandidateAnswer_Type.featOkTst && ((CandidateAnswer_Type)jcasType).casFeat_supportingPassages == null)
      jcasType.jcas.throwFeatMissing("supportingPassages", "uncc2014watsonsim.anagram.CandidateAnswer");
    return (FSList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CandidateAnswer_Type)jcasType).casFeatCode_supportingPassages)));}
    
  /** setter for supportingPassages - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSupportingPassages(FSList v) {
    if (CandidateAnswer_Type.featOkTst && ((CandidateAnswer_Type)jcasType).casFeat_supportingPassages == null)
      jcasType.jcas.throwFeatMissing("supportingPassages", "uncc2014watsonsim.anagram.CandidateAnswer");
    jcasType.ll_cas.ll_setRefValue(addr, ((CandidateAnswer_Type)jcasType).casFeatCode_supportingPassages, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: score

  /** getter for score - gets 
   * @generated
   * @return value of the feature 
   */
  public double getScore() {
    if (CandidateAnswer_Type.featOkTst && ((CandidateAnswer_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "uncc2014watsonsim.anagram.CandidateAnswer");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((CandidateAnswer_Type)jcasType).casFeatCode_score);}
    
  /** setter for score - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setScore(double v) {
    if (CandidateAnswer_Type.featOkTst && ((CandidateAnswer_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "uncc2014watsonsim.anagram.CandidateAnswer");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((CandidateAnswer_Type)jcasType).casFeatCode_score, v);}    
  }

    