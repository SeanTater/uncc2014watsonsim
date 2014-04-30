

/* First created by JCasGen Sun Apr 06 17:13:31 EDT 2014 */
package uncc2014watsonsim.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


import org.apache.uima.jcas.cas.TOP;


/** A query string (question)
 * Updated by JCasGen Tue Apr 29 17:51:30 EDT 2014
 * XML source: /home/jonathan/workspace/uncc2014watsonsim/src/main/java/uncc2014watsonsim/uima/types.xml
 * @generated */
public class QueryString extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(QueryString.class);
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
  protected QueryString() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public QueryString(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public QueryString(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public QueryString(JCas jcas, int begin, int end) {
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
  //* Feature: query

  /** getter for query - gets 
   * @generated
   * @return value of the feature 
   */
  public String getQuery() {
    if (QueryString_Type.featOkTst && ((QueryString_Type)jcasType).casFeat_query == null)
      jcasType.jcas.throwFeatMissing("query", "uncc2014watsonsim.uima.types.QueryString");
    return jcasType.ll_cas.ll_getStringValue(addr, ((QueryString_Type)jcasType).casFeatCode_query);}
    
  /** setter for query - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setQuery(String v) {
    if (QueryString_Type.featOkTst && ((QueryString_Type)jcasType).casFeat_query == null)
      jcasType.jcas.throwFeatMissing("query", "uncc2014watsonsim.uima.types.QueryString");
    jcasType.ll_cas.ll_setStringValue(addr, ((QueryString_Type)jcasType).casFeatCode_query, v);}    
  }

    