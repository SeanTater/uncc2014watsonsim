

/* First created by JCasGen Mon May 05 22:09:31 EDT 2014 */
package uncc2014watsonsim.anagram;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon May 05 22:19:49 EDT 2014
 * XML source: C:/Users/Jacob/Documents/GitHub/uncc2014watsonsim/src/main/java/uncc2014watsonsim/anagramPipeline/parseQuestion.xml
 * @generated */
public class SupportingPassage extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SupportingPassage.class);
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
  protected SupportingPassage() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public SupportingPassage(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public SupportingPassage(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public SupportingPassage(JCas jcas, int begin, int end) {
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
  //* Feature: candidateAnswer

  /** getter for candidateAnswer - gets 
   * @generated
   * @return value of the feature 
   */
  public String getCandidateAnswer() {
    if (SupportingPassage_Type.featOkTst && ((SupportingPassage_Type)jcasType).casFeat_candidateAnswer == null)
      jcasType.jcas.throwFeatMissing("candidateAnswer", "uncc2014watsonsim.anagram.SupportingPassage");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SupportingPassage_Type)jcasType).casFeatCode_candidateAnswer);}
    
  /** setter for candidateAnswer - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCandidateAnswer(String v) {
    if (SupportingPassage_Type.featOkTst && ((SupportingPassage_Type)jcasType).casFeat_candidateAnswer == null)
      jcasType.jcas.throwFeatMissing("candidateAnswer", "uncc2014watsonsim.anagram.SupportingPassage");
    jcasType.ll_cas.ll_setStringValue(addr, ((SupportingPassage_Type)jcasType).casFeatCode_candidateAnswer, v);}    
   
    
  //*--------------*
  //* Feature: supportingPassage

  /** getter for supportingPassage - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSupportingPassage() {
    if (SupportingPassage_Type.featOkTst && ((SupportingPassage_Type)jcasType).casFeat_supportingPassage == null)
      jcasType.jcas.throwFeatMissing("supportingPassage", "uncc2014watsonsim.anagram.SupportingPassage");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SupportingPassage_Type)jcasType).casFeatCode_supportingPassage);}
    
  /** setter for supportingPassage - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSupportingPassage(String v) {
    if (SupportingPassage_Type.featOkTst && ((SupportingPassage_Type)jcasType).casFeat_supportingPassage == null)
      jcasType.jcas.throwFeatMissing("supportingPassage", "uncc2014watsonsim.anagram.SupportingPassage");
    jcasType.ll_cas.ll_setStringValue(addr, ((SupportingPassage_Type)jcasType).casFeatCode_supportingPassage, v);}    
  }

    