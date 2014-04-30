

/* First created by JCasGen Mon Apr 28 12:44:39 EDT 2014 */
package uncc2014watsonsim.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.cas.TOP;


import org.apache.uima.jcas.cas.StringArray;


/** 
 * Updated by JCasGen Tue Apr 29 17:51:30 EDT 2014
 * XML source: /home/jonathan/workspace/uncc2014watsonsim/src/main/java/uncc2014watsonsim/uima/types.xml
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
  //* Feature: query

  /** getter for query - gets The Query
   * @generated
   * @return value of the feature 
   */
  public QueryString getQuery() {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_query == null)
      jcasType.jcas.throwFeatMissing("query", "uncc2014watsonsim.uima.types.UIMAQuestion");
    return (QueryString)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_query)));}
    
  /** setter for query - sets The Query 
   * @generated
   * @param v value to set into the feature 
   */
  public void setQuery(QueryString v) {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_query == null)
      jcasType.jcas.throwFeatMissing("query", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.ll_cas.ll_setRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_query, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
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
  //* Feature: LAT

  /** getter for LAT - gets 
   * @generated
   * @return value of the feature 
   */
  public String getLAT() {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_LAT == null)
      jcasType.jcas.throwFeatMissing("LAT", "uncc2014watsonsim.uima.types.UIMAQuestion");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_LAT);}
    
  /** setter for LAT - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLAT(String v) {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_LAT == null)
      jcasType.jcas.throwFeatMissing("LAT", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.ll_cas.ll_setStringValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_LAT, v);}    
   
    
  //*--------------*
  //* Feature: FitbBlanks

  /** getter for FitbBlanks - gets FITB Blanks annotation under question
   * @generated
   * @return value of the feature 
   */
  public Annotation getFitbBlanks() {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_FitbBlanks == null)
      jcasType.jcas.throwFeatMissing("FitbBlanks", "uncc2014watsonsim.uima.types.UIMAQuestion");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_FitbBlanks)));}
    
  /** setter for FitbBlanks - sets FITB Blanks annotation under question 
   * @generated
   * @param v value to set into the feature 
   */
  public void setFitbBlanks(Annotation v) {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_FitbBlanks == null)
      jcasType.jcas.throwFeatMissing("FitbBlanks", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.ll_cas.ll_setRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_FitbBlanks, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: FitbSection1

  /** getter for FitbSection1 - gets 
   * @generated
   * @return value of the feature 
   */
  public Annotation getFitbSection1() {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_FitbSection1 == null)
      jcasType.jcas.throwFeatMissing("FitbSection1", "uncc2014watsonsim.uima.types.UIMAQuestion");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_FitbSection1)));}
    
  /** setter for FitbSection1 - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFitbSection1(Annotation v) {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_FitbSection1 == null)
      jcasType.jcas.throwFeatMissing("FitbSection1", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.ll_cas.ll_setRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_FitbSection1, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: FitbSection2

  /** getter for FitbSection2 - gets 
   * @generated
   * @return value of the feature 
   */
  public Annotation getFitbSection2() {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_FitbSection2 == null)
      jcasType.jcas.throwFeatMissing("FitbSection2", "uncc2014watsonsim.uima.types.UIMAQuestion");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_FitbSection2)));}
    
  /** setter for FitbSection2 - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFitbSection2(Annotation v) {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_FitbSection2 == null)
      jcasType.jcas.throwFeatMissing("FitbSection2", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.ll_cas.ll_setRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_FitbSection2, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: queryParse

  /** getter for queryParse - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getQueryParse() {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_queryParse == null)
      jcasType.jcas.throwFeatMissing("queryParse", "uncc2014watsonsim.uima.types.UIMAQuestion");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_queryParse)));}
    
  /** setter for queryParse - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setQueryParse(StringArray v) {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_queryParse == null)
      jcasType.jcas.throwFeatMissing("queryParse", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.ll_cas.ll_setRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_queryParse, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for queryParse - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getQueryParse(int i) {
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_queryParse == null)
      jcasType.jcas.throwFeatMissing("queryParse", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_queryParse), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_queryParse), i);}

  /** indexed setter for queryParse - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setQueryParse(int i, String v) { 
    if (UIMAQuestion_Type.featOkTst && ((UIMAQuestion_Type)jcasType).casFeat_queryParse == null)
      jcasType.jcas.throwFeatMissing("queryParse", "uncc2014watsonsim.uima.types.UIMAQuestion");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_queryParse), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((UIMAQuestion_Type)jcasType).casFeatCode_queryParse), i, v);}
  }

    