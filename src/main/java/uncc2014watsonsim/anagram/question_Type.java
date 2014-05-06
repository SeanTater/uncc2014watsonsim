
/* First created by JCasGen Mon May 05 22:09:31 EDT 2014 */
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
import org.apache.uima.jcas.cas.TOP_Type;

/** The base question type
 * Updated by JCasGen Mon May 05 22:19:49 EDT 2014
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
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uncc2014watsonsim.anagram.question");
 
  /** @generated */
  final Feature casFeat_rawText;
  /** @generated */
  final int     casFeatCode_rawText;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getRawText(int addr) {
        if (featOkTst && casFeat_rawText == null)
      jcas.throwFeatMissing("rawText", "uncc2014watsonsim.anagram.question");
    return ll_cas.ll_getStringValue(addr, casFeatCode_rawText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRawText(int addr, String v) {
        if (featOkTst && casFeat_rawText == null)
      jcas.throwFeatMissing("rawText", "uncc2014watsonsim.anagram.question");
    ll_cas.ll_setStringValue(addr, casFeatCode_rawText, v);}
    
  
 
  /** @generated */
  final Feature casFeat_anagramText;
  /** @generated */
  final int     casFeatCode_anagramText;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAnagramText(int addr) {
        if (featOkTst && casFeat_anagramText == null)
      jcas.throwFeatMissing("anagramText", "uncc2014watsonsim.anagram.question");
    return ll_cas.ll_getStringValue(addr, casFeatCode_anagramText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAnagramText(int addr, String v) {
        if (featOkTst && casFeat_anagramText == null)
      jcas.throwFeatMissing("anagramText", "uncc2014watsonsim.anagram.question");
    ll_cas.ll_setStringValue(addr, casFeatCode_anagramText, v);}
    
  
 
  /** @generated */
  final Feature casFeat_anagramSolutions;
  /** @generated */
  final int     casFeatCode_anagramSolutions;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getAnagramSolutions(int addr) {
        if (featOkTst && casFeat_anagramSolutions == null)
      jcas.throwFeatMissing("anagramSolutions", "uncc2014watsonsim.anagram.question");
    return ll_cas.ll_getRefValue(addr, casFeatCode_anagramSolutions);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAnagramSolutions(int addr, int v) {
        if (featOkTst && casFeat_anagramSolutions == null)
      jcas.throwFeatMissing("anagramSolutions", "uncc2014watsonsim.anagram.question");
    ll_cas.ll_setRefValue(addr, casFeatCode_anagramSolutions, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getAnagramSolutions(int addr, int i) {
        if (featOkTst && casFeat_anagramSolutions == null)
      jcas.throwFeatMissing("anagramSolutions", "uncc2014watsonsim.anagram.question");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_anagramSolutions), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_anagramSolutions), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_anagramSolutions), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setAnagramSolutions(int addr, int i, String v) {
        if (featOkTst && casFeat_anagramSolutions == null)
      jcas.throwFeatMissing("anagramSolutions", "uncc2014watsonsim.anagram.question");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_anagramSolutions), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_anagramSolutions), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_anagramSolutions), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_supportingPassages;
  /** @generated */
  final int     casFeatCode_supportingPassages;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSupportingPassages(int addr) {
        if (featOkTst && casFeat_supportingPassages == null)
      jcas.throwFeatMissing("supportingPassages", "uncc2014watsonsim.anagram.question");
    return ll_cas.ll_getRefValue(addr, casFeatCode_supportingPassages);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSupportingPassages(int addr, int v) {
        if (featOkTst && casFeat_supportingPassages == null)
      jcas.throwFeatMissing("supportingPassages", "uncc2014watsonsim.anagram.question");
    ll_cas.ll_setRefValue(addr, casFeatCode_supportingPassages, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getSupportingPassages(int addr, int i) {
        if (featOkTst && casFeat_supportingPassages == null)
      jcas.throwFeatMissing("supportingPassages", "uncc2014watsonsim.anagram.question");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_supportingPassages), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_supportingPassages), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_supportingPassages), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setSupportingPassages(int addr, int i, int v) {
        if (featOkTst && casFeat_supportingPassages == null)
      jcas.throwFeatMissing("supportingPassages", "uncc2014watsonsim.anagram.question");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_supportingPassages), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_supportingPassages), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_supportingPassages), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public question_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_rawText = jcas.getRequiredFeatureDE(casType, "rawText", "uima.cas.String", featOkTst);
    casFeatCode_rawText  = (null == casFeat_rawText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_rawText).getCode();

 
    casFeat_anagramText = jcas.getRequiredFeatureDE(casType, "anagramText", "uima.cas.String", featOkTst);
    casFeatCode_anagramText  = (null == casFeat_anagramText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_anagramText).getCode();

 
    casFeat_anagramSolutions = jcas.getRequiredFeatureDE(casType, "anagramSolutions", "uima.cas.StringArray", featOkTst);
    casFeatCode_anagramSolutions  = (null == casFeat_anagramSolutions) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_anagramSolutions).getCode();

 
    casFeat_supportingPassages = jcas.getRequiredFeatureDE(casType, "supportingPassages", "uima.cas.FSArray", featOkTst);
    casFeatCode_supportingPassages  = (null == casFeat_supportingPassages) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_supportingPassages).getCode();

  }
}



    