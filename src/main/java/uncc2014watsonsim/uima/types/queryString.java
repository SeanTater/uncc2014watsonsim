

/* First created by JCasGen Wed Mar 19 16:41:59 EDT 2014 */
package uncc2014watsonsim.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** A query string (question)
 * Updated by JCasGen Fri Apr 04 17:08:24 EDT 2014
 * XML source: /home/jonathan/workspace/uncc2014watsonsim/src/main/java/uncc2014watsonsim/uima/documentSearch/documentSearchApplicationDescriptor.xml
 * @generated */
public class queryString extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(queryString.class);
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
  protected queryString() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public queryString(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public queryString(JCas jcas) {
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

  /** getter for query - gets 
   * @generated
   * @return value of the feature 
   */
  public String getQuery() {
    if (queryString_Type.featOkTst && ((queryString_Type)jcasType).casFeat_query == null)
      jcasType.jcas.throwFeatMissing("query", "uncc2014watsonsim.uima.types.queryString");
    return jcasType.ll_cas.ll_getStringValue(addr, ((queryString_Type)jcasType).casFeatCode_query);}
    
  /** setter for query - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setQuery(String v) {
    if (queryString_Type.featOkTst && ((queryString_Type)jcasType).casFeat_query == null)
      jcasType.jcas.throwFeatMissing("query", "uncc2014watsonsim.uima.types.queryString");
    jcasType.ll_cas.ll_setStringValue(addr, ((queryString_Type)jcasType).casFeatCode_query, v);}    
  }

    