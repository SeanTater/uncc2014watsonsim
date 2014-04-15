

/* First created by JCasGen Mon Apr 14 12:25:00 EDT 2014 */
package uncc2014watsonsim.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Tue Apr 15 07:00:09 EDT 2014
 * XML source: C:/Users/Jacob/Documents/GitHub/uncc2014watsonsim/src/main/java/uncc2014watsonsim/uima/uimaexperiment/mainEngine.xml
 * @generated */
public class UIMAQuestion extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(UIMAQuestion.class);
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
  protected UIMAQuestion() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public UIMAQuestion(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public UIMAQuestion(JCas jcas) {
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
  //* Feature: answerList

  /** getter for answerList - gets The list of possible answers, in order by rank
   * @generated
   * @return value of the feature 
   */
  public searchResultList getAnswerList() {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_answerList == null)
      jcasType.jcas.throwFeatMissing("answerList", "uncc2014watsonsim.uima.types.UIMAQuestion");
    return (searchResultList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_answerList)));}
    
  /** setter for answerList - sets The list of possible answers, in order by rank 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnswerList(searchResultList v) {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_answerList == null)
      jcasType.jcas.throwFeatMissing("answerList", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.ll_cas.ll_setRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_answerList, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: category

  /** getter for category - gets Category of J! question
   * @generated
   * @return value of the feature 
   */
  public String getCategory() {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category", "uncc2014watsonsim.uima.types.UIMAQuestion");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_category);}
    
  /** setter for category - sets Category of J! question 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCategory(String v) {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.ll_cas.ll_setStringValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_category, v);}    
   
    
  //*--------------*
  //* Feature: qtype

  /** getter for qtype - gets FITB, FACTOID, QUOTE, etc.
   * @generated
   * @return value of the feature 
   */
  public String getQtype() {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_qtype == null)
      jcasType.jcas.throwFeatMissing("qtype", "uncc2014watsonsim.uima.types.UIMAQuestion");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_qtype);}
    
  /** setter for qtype - sets FITB, FACTOID, QUOTE, etc. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setQtype(String v) {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_qtype == null)
      jcasType.jcas.throwFeatMissing("qtype", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.ll_cas.ll_setStringValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_qtype, v);}    
   
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets optional, an ID from the J! database
   * @generated
   * @return value of the feature 
   */
  public int getId() {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "uncc2014watsonsim.uima.types.UIMAQuestion");
    return jcasType.ll_cas.ll_getIntValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets optional, an ID from the J! database 
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(int v) {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.ll_cas.ll_setIntValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: filtered_text

  /** getter for filtered_text - gets The query text, filtered for stop words
   * @generated
   * @return value of the feature 
   */
  public String getFiltered_text() {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_filtered_text == null)
      jcasType.jcas.throwFeatMissing("filtered_text", "uncc2014watsonsim.uima.types.UIMAQuestion");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_filtered_text);}
    
  /** setter for filtered_text - sets The query text, filtered for stop words 
   * @generated
   * @param v value to set into the feature 
   */
  public void setFiltered_text(String v) {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_filtered_text == null)
      jcasType.jcas.throwFeatMissing("filtered_text", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.ll_cas.ll_setStringValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_filtered_text, v);}    
   
    
  //*--------------*
  //* Feature: raw_text

  /** getter for raw_text - gets The raw query text, as given to the pipeline
   * @generated
   * @return value of the feature 
   */
  public String getRaw_text() {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_raw_text == null)
      jcasType.jcas.throwFeatMissing("raw_text", "uncc2014watsonsim.uima.types.UIMAQuestion");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_raw_text);}
    
  /** setter for raw_text - sets The raw query text, as given to the pipeline 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRaw_text(String v) {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_raw_text == null)
      jcasType.jcas.throwFeatMissing("raw_text", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.ll_cas.ll_setStringValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_raw_text, v);}    
  }

    