
/* First created by JCasGen Wed Mar 19 16:09:32 EDT 2014 */
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
 * Updated by JCasGen Fri Apr 04 17:08:24 EDT 2014
 * @generated */
public class searchResult_Type extends TOP_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (searchResult_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = searchResult_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new searchResult(addr, searchResult_Type.this);
  			   searchResult_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new searchResult(addr, searchResult_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = searchResult.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uncc2014watsonsim.uima.types.searchResult");



  /** @generated */
  final Feature casFeat_title;
  /** @generated */
  final int     casFeatCode_title;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTitle(int addr) {
        if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "uncc2014watsonsim.uima.types.searchResult");
    return ll_cas.ll_getStringValue(addr, casFeatCode_title);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTitle(int addr, String v) {
        if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "uncc2014watsonsim.uima.types.searchResult");
    ll_cas.ll_setStringValue(addr, casFeatCode_title, v);}
    
  
 
  /** @generated */
  final Feature casFeat_fullText;
  /** @generated */
  final int     casFeatCode_fullText;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getFullText(int addr) {
        if (featOkTst && casFeat_fullText == null)
      jcas.throwFeatMissing("fullText", "uncc2014watsonsim.uima.types.searchResult");
    return ll_cas.ll_getStringValue(addr, casFeatCode_fullText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFullText(int addr, String v) {
        if (featOkTst && casFeat_fullText == null)
      jcas.throwFeatMissing("fullText", "uncc2014watsonsim.uima.types.searchResult");
    ll_cas.ll_setStringValue(addr, casFeatCode_fullText, v);}
    
  
 
  /** @generated */
  final Feature casFeat_reference;
  /** @generated */
  final int     casFeatCode_reference;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getReference(int addr) {
        if (featOkTst && casFeat_reference == null)
      jcas.throwFeatMissing("reference", "uncc2014watsonsim.uima.types.searchResult");
    return ll_cas.ll_getStringValue(addr, casFeatCode_reference);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setReference(int addr, String v) {
        if (featOkTst && casFeat_reference == null)
      jcas.throwFeatMissing("reference", "uncc2014watsonsim.uima.types.searchResult");
    ll_cas.ll_setStringValue(addr, casFeatCode_reference, v);}
    
  
 
  /** @generated */
  final Feature casFeat_rank;
  /** @generated */
  final int     casFeatCode_rank;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public long getRank(int addr) {
        if (featOkTst && casFeat_rank == null)
      jcas.throwFeatMissing("rank", "uncc2014watsonsim.uima.types.searchResult");
    return ll_cas.ll_getLongValue(addr, casFeatCode_rank);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRank(int addr, long v) {
        if (featOkTst && casFeat_rank == null)
      jcas.throwFeatMissing("rank", "uncc2014watsonsim.uima.types.searchResult");
    ll_cas.ll_setLongValue(addr, casFeatCode_rank, v);}
    
  
 
  /** @generated */
  final Feature casFeat_engine;
  /** @generated */
  final int     casFeatCode_engine;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getEngine(int addr) {
        if (featOkTst && casFeat_engine == null)
      jcas.throwFeatMissing("engine", "uncc2014watsonsim.uima.types.searchResult");
    return ll_cas.ll_getStringValue(addr, casFeatCode_engine);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setEngine(int addr, String v) {
        if (featOkTst && casFeat_engine == null)
      jcas.throwFeatMissing("engine", "uncc2014watsonsim.uima.types.searchResult");
    ll_cas.ll_setStringValue(addr, casFeatCode_engine, v);}
    
  
 
  /** @generated */
  final Feature casFeat_score;
  /** @generated */
  final int     casFeatCode_score;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getScore(int addr) {
        if (featOkTst && casFeat_score == null)
      jcas.throwFeatMissing("score", "uncc2014watsonsim.uima.types.searchResult");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_score);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setScore(int addr, double v) {
        if (featOkTst && casFeat_score == null)
      jcas.throwFeatMissing("score", "uncc2014watsonsim.uima.types.searchResult");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_score, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public searchResult_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_title = jcas.getRequiredFeatureDE(casType, "title", "uima.cas.String", featOkTst);
    casFeatCode_title  = (null == casFeat_title) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_title).getCode();

 
    casFeat_fullText = jcas.getRequiredFeatureDE(casType, "fullText", "uima.cas.String", featOkTst);
    casFeatCode_fullText  = (null == casFeat_fullText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_fullText).getCode();

 
    casFeat_reference = jcas.getRequiredFeatureDE(casType, "reference", "uima.cas.String", featOkTst);
    casFeatCode_reference  = (null == casFeat_reference) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_reference).getCode();

 
    casFeat_rank = jcas.getRequiredFeatureDE(casType, "rank", "uima.cas.Long", featOkTst);
    casFeatCode_rank  = (null == casFeat_rank) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_rank).getCode();

 
    casFeat_engine = jcas.getRequiredFeatureDE(casType, "engine", "uima.cas.String", featOkTst);
    casFeatCode_engine  = (null == casFeat_engine) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_engine).getCode();

 
    casFeat_score = jcas.getRequiredFeatureDE(casType, "score", "uima.cas.Double", featOkTst);
    casFeatCode_score  = (null == casFeat_score) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_score).getCode();

  }
}



    