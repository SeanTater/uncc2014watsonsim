
/* First created by JCasGen Tue Apr 22 10:17:48 EDT 2014 */
package uncc2014watsonsim.uima.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.cas.TOP_Type;

/** 
 * Updated by JCasGen Tue Apr 22 10:55:59 EDT 2014
 * @generated */
public class question_Type extends TOP_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (question_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = question_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new question(addr, question_Type.this);
  			   question_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new question(addr, question_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = question.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uncc2014watsonsim.uima.types.question");
 
  /** @generated */
  final Feature casFeat_query;
  /** @generated */
  final int     casFeatCode_query;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getQuery(int addr) {
        if (featOkTst && casFeat_query == null)
      jcas.throwFeatMissing("query", "uncc2014watsonsim.uima.types.question");
    return ll_cas.ll_getRefValue(addr, casFeatCode_query);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setQuery(int addr, int v) {
        if (featOkTst && casFeat_query == null)
      jcas.throwFeatMissing("query", "uncc2014watsonsim.uima.types.question");
    ll_cas.ll_setRefValue(addr, casFeatCode_query, v);}
    
  
 
  /** @generated */
  final Feature casFeat_answerList;
  /** @generated */
  final int     casFeatCode_answerList;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getAnswerList(int addr) {
        if (featOkTst && casFeat_answerList == null)
      jcas.throwFeatMissing("answerList", "uncc2014watsonsim.uima.types.question");
    return ll_cas.ll_getRefValue(addr, casFeatCode_answerList);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAnswerList(int addr, int v) {
        if (featOkTst && casFeat_answerList == null)
      jcas.throwFeatMissing("answerList", "uncc2014watsonsim.uima.types.question");
    ll_cas.ll_setRefValue(addr, casFeatCode_answerList, v);}
    
  
 
  /** @generated */
  final Feature casFeat_category;
  /** @generated */
  final int     casFeatCode_category;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCategory(int addr) {
        if (featOkTst && casFeat_category == null)
      jcas.throwFeatMissing("category", "uncc2014watsonsim.uima.types.question");
    return ll_cas.ll_getStringValue(addr, casFeatCode_category);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCategory(int addr, String v) {
        if (featOkTst && casFeat_category == null)
      jcas.throwFeatMissing("category", "uncc2014watsonsim.uima.types.question");
    ll_cas.ll_setStringValue(addr, casFeatCode_category, v);}
    
  
 
  /** @generated */
  final Feature casFeat_qtype;
  /** @generated */
  final int     casFeatCode_qtype;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getQtype(int addr) {
        if (featOkTst && casFeat_qtype == null)
      jcas.throwFeatMissing("qtype", "uncc2014watsonsim.uima.types.question");
    return ll_cas.ll_getStringValue(addr, casFeatCode_qtype);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setQtype(int addr, String v) {
        if (featOkTst && casFeat_qtype == null)
      jcas.throwFeatMissing("qtype", "uncc2014watsonsim.uima.types.question");
    ll_cas.ll_setStringValue(addr, casFeatCode_qtype, v);}
    
  
 
  /** @generated */
  final Feature casFeat_id;
  /** @generated */
  final int     casFeatCode_id;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getId(int addr) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "uncc2014watsonsim.uima.types.question");
    return ll_cas.ll_getIntValue(addr, casFeatCode_id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setId(int addr, int v) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "uncc2014watsonsim.uima.types.question");
    ll_cas.ll_setIntValue(addr, casFeatCode_id, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public question_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_query = jcas.getRequiredFeatureDE(casType, "query", "uncc2014watsonsim.uima.types.QueryString", featOkTst);
    casFeatCode_query  = (null == casFeat_query) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_query).getCode();

 
    casFeat_answerList = jcas.getRequiredFeatureDE(casType, "answerList", "uncc2014watsonsim.uima.types.searchResultList", featOkTst);
    casFeatCode_answerList  = (null == casFeat_answerList) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_answerList).getCode();

 
    casFeat_category = jcas.getRequiredFeatureDE(casType, "category", "uima.cas.String", featOkTst);
    casFeatCode_category  = (null == casFeat_category) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_category).getCode();

 
    casFeat_qtype = jcas.getRequiredFeatureDE(casType, "qtype", "uima.cas.String", featOkTst);
    casFeatCode_qtype  = (null == casFeat_qtype) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_qtype).getCode();

 
    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.Integer", featOkTst);
    casFeatCode_id  = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_id).getCode();

  }
}



    