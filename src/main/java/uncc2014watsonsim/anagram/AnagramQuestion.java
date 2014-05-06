

/* First created by JCasGen Mon May 05 22:20:46 EDT 2014 */
package uncc2014watsonsim.anagram;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.cas.StringArray;


/** The base question type
 * Updated by JCasGen Tue May 06 03:49:45 EDT 2014
 * XML source: C:/Users/Jacob/Documents/GitHub/uncc2014watsonsim/src/main/java/uncc2014watsonsim/anagramPipeline/AnagramSupportingPassageScoreEngine.xml
 * @generated */
public class AnagramQuestion extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AnagramQuestion.class);
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
  protected AnagramQuestion() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public AnagramQuestion(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public AnagramQuestion(JCas jcas) {
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
  //* Feature: fullText

  /** getter for fullText - gets The raw question text, unformatted
   * @generated
   * @return value of the feature 
   */
  public String getFullText() {
    if (AnagramQuestion_Type.featOkTst && ((AnagramQuestion_Type)jcasType).casFeat_fullText == null)
      jcasType.jcas.throwFeatMissing("fullText", "uncc2014watsonsim.anagram.AnagramQuestion");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AnagramQuestion_Type)jcasType).casFeatCode_fullText);}
    
  /** setter for fullText - sets The raw question text, unformatted 
   * @generated
   * @param v value to set into the feature 
   */
  public void setFullText(String v) {
    if (AnagramQuestion_Type.featOkTst && ((AnagramQuestion_Type)jcasType).casFeat_fullText == null)
      jcasType.jcas.throwFeatMissing("fullText", "uncc2014watsonsim.anagram.AnagramQuestion");
    jcasType.ll_cas.ll_setStringValue(addr, ((AnagramQuestion_Type)jcasType).casFeatCode_fullText, v);}    
   
    
  //*--------------*
  //* Feature: anagramText

  /** getter for anagramText - gets The anagram text
   * @generated
   * @return value of the feature 
   */
  public String getAnagramText() {
    if (AnagramQuestion_Type.featOkTst && ((AnagramQuestion_Type)jcasType).casFeat_anagramText == null)
      jcasType.jcas.throwFeatMissing("anagramText", "uncc2014watsonsim.anagram.AnagramQuestion");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AnagramQuestion_Type)jcasType).casFeatCode_anagramText);}
    
  /** setter for anagramText - sets The anagram text 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnagramText(String v) {
    if (AnagramQuestion_Type.featOkTst && ((AnagramQuestion_Type)jcasType).casFeat_anagramText == null)
      jcasType.jcas.throwFeatMissing("anagramText", "uncc2014watsonsim.anagram.AnagramQuestion");
    jcasType.ll_cas.ll_setStringValue(addr, ((AnagramQuestion_Type)jcasType).casFeatCode_anagramText, v);}    
   
    
  //*--------------*
  //* Feature: candidateAnswers

  /** getter for candidateAnswers - gets The list of possible solutions to the anagram text
   * @generated
   * @return value of the feature 
   */
  public FSList getCandidateAnswers() {
    if (AnagramQuestion_Type.featOkTst && ((AnagramQuestion_Type)jcasType).casFeat_candidateAnswers == null)
      jcasType.jcas.throwFeatMissing("candidateAnswers", "uncc2014watsonsim.anagram.AnagramQuestion");
    return (FSList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AnagramQuestion_Type)jcasType).casFeatCode_candidateAnswers)));}
    
  /** setter for candidateAnswers - sets The list of possible solutions to the anagram text 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCandidateAnswers(FSList v) {
    if (AnagramQuestion_Type.featOkTst && ((AnagramQuestion_Type)jcasType).casFeat_candidateAnswers == null)
      jcasType.jcas.throwFeatMissing("candidateAnswers", "uncc2014watsonsim.anagram.AnagramQuestion");
    jcasType.ll_cas.ll_setRefValue(addr, ((AnagramQuestion_Type)jcasType).casFeatCode_candidateAnswers, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: questionCategory

  /** getter for questionCategory - gets 
   * @generated
   * @return value of the feature 
   */
  public String getQuestionCategory() {
    if (AnagramQuestion_Type.featOkTst && ((AnagramQuestion_Type)jcasType).casFeat_questionCategory == null)
      jcasType.jcas.throwFeatMissing("questionCategory", "uncc2014watsonsim.anagram.AnagramQuestion");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AnagramQuestion_Type)jcasType).casFeatCode_questionCategory);}
    
  /** setter for questionCategory - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setQuestionCategory(String v) {
    if (AnagramQuestion_Type.featOkTst && ((AnagramQuestion_Type)jcasType).casFeat_questionCategory == null)
      jcasType.jcas.throwFeatMissing("questionCategory", "uncc2014watsonsim.anagram.AnagramQuestion");
    jcasType.ll_cas.ll_setStringValue(addr, ((AnagramQuestion_Type)jcasType).casFeatCode_questionCategory, v);}    
   
    
  //*--------------*
  //* Feature: QuestionText

  /** getter for QuestionText - gets 
   * @generated
   * @return value of the feature 
   */
  public String getQuestionText() {
    if (AnagramQuestion_Type.featOkTst && ((AnagramQuestion_Type)jcasType).casFeat_QuestionText == null)
      jcasType.jcas.throwFeatMissing("QuestionText", "uncc2014watsonsim.anagram.AnagramQuestion");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AnagramQuestion_Type)jcasType).casFeatCode_QuestionText);}
    
  /** setter for QuestionText - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setQuestionText(String v) {
    if (AnagramQuestion_Type.featOkTst && ((AnagramQuestion_Type)jcasType).casFeat_QuestionText == null)
      jcasType.jcas.throwFeatMissing("QuestionText", "uncc2014watsonsim.anagram.AnagramQuestion");
    jcasType.ll_cas.ll_setStringValue(addr, ((AnagramQuestion_Type)jcasType).casFeatCode_QuestionText, v);}    
  }

    