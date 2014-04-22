

/* First created by JCasGen Wed Mar 19 16:41:59 EDT 2014 */
package uncc2014watsonsim.sources.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.TOP;


/** Containing a list of search results
 * Updated by JCasGen Sun Apr 06 17:17:36 EDT 2014
 * XML source: /home/jonathan/workspace/uncc2014watsonsim/src/main/java/uncc2014watsonsim/uima/types.xml
 * @generated */
public class searchResultList extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(searchResultList.class);
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
  protected searchResultList() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public searchResultList(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public searchResultList(JCas jcas) {
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
  //* Feature: list

  /** getter for list - gets The implementation of the list
   * @generated
   * @return value of the feature 
   */
  public FSList getList() {
    if (searchResultList_Type.featOkTst && ((searchResultList_Type)jcasType).casFeat_list == null)
      jcasType.jcas.throwFeatMissing("list", "uncc2014watsonsim.uima.types.searchResultList");
    return (FSList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((searchResultList_Type)jcasType).casFeatCode_list)));}
    
  /** setter for list - sets The implementation of the list 
   * @generated
   * @param v value to set into the feature 
   */
  public void setList(FSList v) {
    if (searchResultList_Type.featOkTst && ((searchResultList_Type)jcasType).casFeat_list == null)
      jcasType.jcas.throwFeatMissing("list", "uncc2014watsonsim.uima.types.searchResultList");
    jcasType.ll_cas.ll_setRefValue(addr, ((searchResultList_Type)jcasType).casFeatCode_list, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    