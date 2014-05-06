
/* First created by JCasGen Mon May 05 22:20:46 EDT 2014 */
package uncc2014watsonsim.anagram;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Tue May 06 03:49:45 EDT 2014
 * @generated */
public class AnagramSupportingPassage_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (AnagramSupportingPassage_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = AnagramSupportingPassage_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new AnagramSupportingPassage(addr, AnagramSupportingPassage_Type.this);
  			   AnagramSupportingPassage_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new AnagramSupportingPassage(addr, AnagramSupportingPassage_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = AnagramSupportingPassage.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uncc2014watsonsim.anagram.AnagramSupportingPassage");
 
  /** @generated */
  final Feature casFeat_passageTitle;
  /** @generated */
  final int     casFeatCode_passageTitle;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPassageTitle(int addr) {
        if (featOkTst && casFeat_passageTitle == null)
      jcas.throwFeatMissing("passageTitle", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    return ll_cas.ll_getStringValue(addr, casFeatCode_passageTitle);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPassageTitle(int addr, String v) {
        if (featOkTst && casFeat_passageTitle == null)
      jcas.throwFeatMissing("passageTitle", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    ll_cas.ll_setStringValue(addr, casFeatCode_passageTitle, v);}
    
  
 
  /** @generated */
  final Feature casFeat_supportingPassage;
  /** @generated */
  final int     casFeatCode_supportingPassage;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSupportingPassage(int addr) {
        if (featOkTst && casFeat_supportingPassage == null)
      jcas.throwFeatMissing("supportingPassage", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    return ll_cas.ll_getStringValue(addr, casFeatCode_supportingPassage);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSupportingPassage(int addr, String v) {
        if (featOkTst && casFeat_supportingPassage == null)
      jcas.throwFeatMissing("supportingPassage", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    ll_cas.ll_setStringValue(addr, casFeatCode_supportingPassage, v);}
    
  
 
  /** @generated */
  final Feature casFeat_searcherName;
  /** @generated */
  final int     casFeatCode_searcherName;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSearcherName(int addr) {
        if (featOkTst && casFeat_searcherName == null)
      jcas.throwFeatMissing("searcherName", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    return ll_cas.ll_getStringValue(addr, casFeatCode_searcherName);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSearcherName(int addr, String v) {
        if (featOkTst && casFeat_searcherName == null)
      jcas.throwFeatMissing("searcherName", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    ll_cas.ll_setStringValue(addr, casFeatCode_searcherName, v);}
    
  
 
  /** @generated */
  final Feature casFeat_searcherRank;
  /** @generated */
  final int     casFeatCode_searcherRank;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSearcherRank(int addr) {
        if (featOkTst && casFeat_searcherRank == null)
      jcas.throwFeatMissing("searcherRank", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    return ll_cas.ll_getIntValue(addr, casFeatCode_searcherRank);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSearcherRank(int addr, int v) {
        if (featOkTst && casFeat_searcherRank == null)
      jcas.throwFeatMissing("searcherRank", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    ll_cas.ll_setIntValue(addr, casFeatCode_searcherRank, v);}
    
  
 
  /** @generated */
  final Feature casFeat_searcherScore;
  /** @generated */
  final int     casFeatCode_searcherScore;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getSearcherScore(int addr) {
        if (featOkTst && casFeat_searcherScore == null)
      jcas.throwFeatMissing("searcherScore", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_searcherScore);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSearcherScore(int addr, double v) {
        if (featOkTst && casFeat_searcherScore == null)
      jcas.throwFeatMissing("searcherScore", "uncc2014watsonsim.anagram.AnagramSupportingPassage");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_searcherScore, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public AnagramSupportingPassage_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_passageTitle = jcas.getRequiredFeatureDE(casType, "passageTitle", "uima.cas.String", featOkTst);
    casFeatCode_passageTitle  = (null == casFeat_passageTitle) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_passageTitle).getCode();

 
    casFeat_supportingPassage = jcas.getRequiredFeatureDE(casType, "supportingPassage", "uima.cas.String", featOkTst);
    casFeatCode_supportingPassage  = (null == casFeat_supportingPassage) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_supportingPassage).getCode();

 
    casFeat_searcherName = jcas.getRequiredFeatureDE(casType, "searcherName", "uima.cas.String", featOkTst);
    casFeatCode_searcherName  = (null == casFeat_searcherName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_searcherName).getCode();

 
    casFeat_searcherRank = jcas.getRequiredFeatureDE(casType, "searcherRank", "uima.cas.Integer", featOkTst);
    casFeatCode_searcherRank  = (null == casFeat_searcherRank) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_searcherRank).getCode();

 
    casFeat_searcherScore = jcas.getRequiredFeatureDE(casType, "searcherScore", "uima.cas.Double", featOkTst);
    casFeatCode_searcherScore  = (null == casFeat_searcherScore) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_searcherScore).getCode();

  }
}



    