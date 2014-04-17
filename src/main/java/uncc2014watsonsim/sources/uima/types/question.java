

/* First created by JCasGen Wed Mar 19 16:56:45 EDT 2014 */
package uncc2014watsonsim.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Sun Apr 06 17:17:36 EDT 2014
 * XML source: /home/jonathan/workspace/uncc2014watsonsim/src/main/java/uncc2014watsonsim/uima/types.xml
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
  //* Feature: query

  /** getter for query - gets The Query
   * @generated
   * @return value of the feature 
   */
  public QueryString getQuery() {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_query == null)
      jcasType.jcas.throwFeatMissing("query", "uncc2014watsonsim.uima.types.question");
    return (QueryString)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((question_Type)jcasType).casFeatCode_query)));}
    
  /** setter for query - sets The Query 
   * @generated
   * @param v value to set into the feature 
   */
  public void setQuery(QueryString v) {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_query == null)
      jcasType.jcas.throwFeatMissing("query", "uncc2014watsonsim.uima.types.question");
    jcasType.ll_cas.ll_setRefValue(addr, ((question_Type)jcasType).casFeatCode_query, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: answerList

  /** getter for answerList - gets The list of possible answers, in order by rank
   * @generated
   * @return value of the feature 
   */
  public searchResultList getAnswerList() {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_answerList == null)
      jcasType.jcas.throwFeatMissing("answerList", "uncc2014watsonsim.uima.types.question");
    return (searchResultList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((question_Type)jcasType).casFeatCode_answerList)));}
    
  /** setter for answerList - sets The list of possible answers, in order by rank 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnswerList(searchResultList v) {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_answerList == null)
      jcasType.jcas.throwFeatMissing("answerList", "uncc2014watsonsim.uima.types.question");
    jcasType.ll_cas.ll_setRefValue(addr, ((question_Type)jcasType).casFeatCode_answerList, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: category

  /** getter for category - gets Category of J! question
   * @generated
   * @return value of the feature 
   */
  public String getCategory() {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category", "uncc2014watsonsim.uima.types.question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((question_Type)jcasType).casFeatCode_category);}
    
  /** setter for category - sets Category of J! question 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCategory(String v) {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category", "uncc2014watsonsim.uima.types.question");
    jcasType.ll_cas.ll_setStringValue(addr, ((question_Type)jcasType).casFeatCode_category, v);}    
   
    
  //*--------------*
  //* Feature: qtype

  /** getter for qtype - gets FITB, FACTOID, QUOTE, etc.
   * @generated
   * @return value of the feature 
   */
  public String getQtype() {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_qtype == null)
      jcasType.jcas.throwFeatMissing("qtype", "uncc2014watsonsim.uima.types.question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((question_Type)jcasType).casFeatCode_qtype);}
    
  /** setter for qtype - sets FITB, FACTOID, QUOTE, etc. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setQtype(String v) {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_qtype == null)
      jcasType.jcas.throwFeatMissing("qtype", "uncc2014watsonsim.uima.types.question");
    jcasType.ll_cas.ll_setStringValue(addr, ((question_Type)jcasType).casFeatCode_qtype, v);}    
   
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets optional, an ID from the J! database
   * @generated
   * @return value of the feature 
   */
  public int getId() {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "uncc2014watsonsim.uima.types.question");
    return jcasType.ll_cas.ll_getIntValue(addr, ((question_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets optional, an ID from the J! database 
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(int v) {
    if (question_Type.featOkTst && ((question_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "uncc2014watsonsim.uima.types.question");
    jcasType.ll_cas.ll_setIntValue(addr, ((question_Type)jcasType).casFeatCode_id, v);}    
  }

    