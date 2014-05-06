

/* First created by JCasGen Tue May 06 00:47:22 EDT 2014 */
package uncc2014watsonsim.anagram;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Tue May 06 00:54:00 EDT 2014
 * XML source: C:/Users/Jacob/Documents/GitHub/uncc2014watsonsim/src/main/java/uncc2014watsonsim/anagramPipeline/AnagramLucenePassageSearch.xml
 * @generated */
public class AnagramSolution extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AnagramSolution.class);
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
  protected AnagramSolution() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public AnagramSolution(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public AnagramSolution(JCas jcas) {
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
    if (AnagramSolution_Type.featOkTst && ((AnagramSolution_Type)jcasType).casFeat_answer == null)
      jcasType.jcas.throwFeatMissing("answer", "uncc2014watsonsim.anagram.AnagramSolution");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AnagramSolution_Type)jcasType).casFeatCode_answer);}
    
  /** setter for answer - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnswer(String v) {
    if (AnagramSolution_Type.featOkTst && ((AnagramSolution_Type)jcasType).casFeat_answer == null)
      jcasType.jcas.throwFeatMissing("answer", "uncc2014watsonsim.anagram.AnagramSolution");
    jcasType.ll_cas.ll_setStringValue(addr, ((AnagramSolution_Type)jcasType).casFeatCode_answer, v);}    
  }

    