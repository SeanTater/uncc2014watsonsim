
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
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon May 05 22:19:49 EDT 2014
 * @generated */
public class SupportingPassage_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (SupportingPassage_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = SupportingPassage_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new SupportingPassage(addr, SupportingPassage_Type.this);
  			   SupportingPassage_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new SupportingPassage(addr, SupportingPassage_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = SupportingPassage.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uncc2014watsonsim.anagram.SupportingPassage");
 
  /** @generated */
  final Feature casFeat_candidateAnswer;
  /** @generated */
  final int     casFeatCode_candidateAnswer;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCandidateAnswer(int addr) {
        if (featOkTst && casFeat_candidateAnswer == null)
      jcas.throwFeatMissing("candidateAnswer", "uncc2014watsonsim.anagram.SupportingPassage");
    return ll_cas.ll_getStringValue(addr, casFeatCode_candidateAnswer);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCandidateAnswer(int addr, String v) {
        if (featOkTst && casFeat_candidateAnswer == null)
      jcas.throwFeatMissing("candidateAnswer", "uncc2014watsonsim.anagram.SupportingPassage");
    ll_cas.ll_setStringValue(addr, casFeatCode_candidateAnswer, v);}
    
  
 
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
      jcas.throwFeatMissing("supportingPassage", "uncc2014watsonsim.anagram.SupportingPassage");
    return ll_cas.ll_getStringValue(addr, casFeatCode_supportingPassage);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSupportingPassage(int addr, String v) {
        if (featOkTst && casFeat_supportingPassage == null)
      jcas.throwFeatMissing("supportingPassage", "uncc2014watsonsim.anagram.SupportingPassage");
    ll_cas.ll_setStringValue(addr, casFeatCode_supportingPassage, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public SupportingPassage_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_candidateAnswer = jcas.getRequiredFeatureDE(casType, "candidateAnswer", "uima.cas.String", featOkTst);
    casFeatCode_candidateAnswer  = (null == casFeat_candidateAnswer) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_candidateAnswer).getCode();

 
    casFeat_supportingPassage = jcas.getRequiredFeatureDE(casType, "supportingPassage", "uima.cas.String", featOkTst);
    casFeatCode_supportingPassage  = (null == casFeat_supportingPassage) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_supportingPassage).getCode();

  }
}



    