

/* First created by JCasGen Mon May 05 22:09:31 EDT 2014 */
package uncc2014watsonsim.anagram;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.cas.StringArray;


/** The base question type
 * Updated by JCasGen Mon May 05 22:19:49 EDT 2014
 * XML source: C:/Users/Jacob/Documents/GitHub/uncc2014watsonsim/src/main/java/uncc2014watsonsim/anagramPipeline/parseQuestion.xml
 * @generated */
public class question extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(question.class);
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
  protected question() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public question(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public question(JCas jcas) {
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
  //* Feature: rawText

  /** getter for rawText - gets The raw question text, unformatted
   * @generated
   * @return value of the feature 
   */
  public String getRawText() {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_rawText == null)
      jcasType.jcas.throwFeatMissing("rawText", "uncc2014watsonsim.anagram.question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((question_Type)jcasType).casFeatCode_rawText);}
    
  /** setter for rawText - sets The raw question text, unformatted 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRawText(String v) {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_rawText == null)
      jcasType.jcas.throwFeatMissing("rawText", "uncc2014watsonsim.anagram.question");
    jcasType.ll_cas.ll_setStringValue(addr, ((question_Type)jcasType).casFeatCode_rawText, v);}    
   
    
  //*--------------*
  //* Feature: anagramText

  /** getter for anagramText - gets The anagram text
   * @generated
   * @return value of the feature 
   */
  public String getAnagramText() {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_anagramText == null)
      jcasType.jcas.throwFeatMissing("anagramText", "uncc2014watsonsim.anagram.question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((question_Type)jcasType).casFeatCode_anagramText);}
    
  /** setter for anagramText - sets The anagram text 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnagramText(String v) {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_anagramText == null)
      jcasType.jcas.throwFeatMissing("anagramText", "uncc2014watsonsim.anagram.question");
    jcasType.ll_cas.ll_setStringValue(addr, ((question_Type)jcasType).casFeatCode_anagramText, v);}    
   
    
  //*--------------*
  //* Feature: anagramSolutions

  /** getter for anagramSolutions - gets The list of possible solutions to the anagram text
   * @generated
   * @return value of the feature 
   */
  public StringArray getAnagramSolutions() {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_anagramSolutions == null)
      jcasType.jcas.throwFeatMissing("anagramSolutions", "uncc2014watsonsim.anagram.question");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((question_Type)jcasType).casFeatCode_anagramSolutions)));}
    
  /** setter for anagramSolutions - sets The list of possible solutions to the anagram text 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnagramSolutions(StringArray v) {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_anagramSolutions == null)
      jcasType.jcas.throwFeatMissing("anagramSolutions", "uncc2014watsonsim.anagram.question");
    jcasType.ll_cas.ll_setRefValue(addr, ((question_Type)jcasType).casFeatCode_anagramSolutions, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for anagramSolutions - gets an indexed value - The list of possible solutions to the anagram text
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getAnagramSolutions(int i) {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_anagramSolutions == null)
      jcasType.jcas.throwFeatMissing("anagramSolutions", "uncc2014watsonsim.anagram.question");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((question_Type)jcasType).casFeatCode_anagramSolutions), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((question_Type)jcasType).casFeatCode_anagramSolutions), i);}

  /** indexed setter for anagramSolutions - sets an indexed value - The list of possible solutions to the anagram text
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setAnagramSolutions(int i, String v) { 
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_anagramSolutions == null)
      jcasType.jcas.throwFeatMissing("anagramSolutions", "uncc2014watsonsim.anagram.question");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((question_Type)jcasType).casFeatCode_anagramSolutions), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((question_Type)jcasType).casFeatCode_anagramSolutions), i, v);}
   
    
  //*--------------*
  //* Feature: supportingPassages

  /** getter for supportingPassages - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getSupportingPassages() {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_supportingPassages == null)
      jcasType.jcas.throwFeatMissing("supportingPassages", "uncc2014watsonsim.anagram.question");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((question_Type)jcasType).casFeatCode_supportingPassages)));}
    
  /** setter for supportingPassages - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSupportingPassages(FSArray v) {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_supportingPassages == null)
      jcasType.jcas.throwFeatMissing("supportingPassages", "uncc2014watsonsim.anagram.question");
    jcasType.ll_cas.ll_setRefValue(addr, ((question_Type)jcasType).casFeatCode_supportingPassages, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for supportingPassages - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public SupportingPassage getSupportingPassages(int i) {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_supportingPassages == null)
      jcasType.jcas.throwFeatMissing("supportingPassages", "uncc2014watsonsim.anagram.question");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((question_Type)jcasType).casFeatCode_supportingPassages), i);
    return (SupportingPassage)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((question_Type)jcasType).casFeatCode_supportingPassages), i)));}

  /** indexed setter for supportingPassages - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setSupportingPassages(int i, SupportingPassage v) { 
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_supportingPassages == null)
      jcasType.jcas.throwFeatMissing("supportingPassages", "uncc2014watsonsim.anagram.question");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((question_Type)jcasType).casFeatCode_supportingPassages), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((question_Type)jcasType).casFeatCode_supportingPassages), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    