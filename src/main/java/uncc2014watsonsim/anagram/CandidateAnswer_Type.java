
/* First created by JCasGen Tue May 06 01:26:28 EDT 2014 */
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

/** 
 * Updated by JCasGen Tue May 06 03:49:45 EDT 2014
 * @generated */
public class CandidateAnswer_Type extends TOP_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (CandidateAnswer_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = CandidateAnswer_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new CandidateAnswer(addr, CandidateAnswer_Type.this);
  			   CandidateAnswer_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new CandidateAnswer(addr, CandidateAnswer_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = CandidateAnswer.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uncc2014watsonsim.anagram.CandidateAnswer");
 
  /** @generated */
  final Feature casFeat_answer;
  /** @generated */
  final int     casFeatCode_answer;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAnswer(int addr) {
        if (featOkTst && casFeat_answer == null)
      jcas.throwFeatMissing("answer", "uncc2014watsonsim.anagram.CandidateAnswer");
    return ll_cas.ll_getStringValue(addr, casFeatCode_answer);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAnswer(int addr, String v) {
        if (featOkTst && casFeat_answer == null)
      jcas.throwFeatMissing("answer", "uncc2014watsonsim.anagram.CandidateAnswer");
    ll_cas.ll_setStringValue(addr, casFeatCode_answer, v);}
    
  
 
  /** @generated */
  final Feature casFeat_questionText;
  /** @generated */
  final int     casFeatCode_questionText;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getQuestionText(int addr) {
        if (featOkTst && casFeat_questionText == null)
      jcas.throwFeatMissing("questionText", "uncc2014watsonsim.anagram.CandidateAnswer");
    return ll_cas.ll_getStringValue(addr, casFeatCode_questionText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setQuestionText(int addr, String v) {
        if (featOkTst && casFeat_questionText == null)
      jcas.throwFeatMissing("questionText", "uncc2014watsonsim.anagram.CandidateAnswer");
    ll_cas.ll_setStringValue(addr, casFeatCode_questionText, v);}
    
  
 
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
      jcas.throwFeatMissing("supportingPassages", "uncc2014watsonsim.anagram.CandidateAnswer");
    return ll_cas.ll_getRefValue(addr, casFeatCode_supportingPassages);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSupportingPassages(int addr, int v) {
        if (featOkTst && casFeat_supportingPassages == null)
      jcas.throwFeatMissing("supportingPassages", "uncc2014watsonsim.anagram.CandidateAnswer");
    ll_cas.ll_setRefValue(addr, casFeatCode_supportingPassages, v);}
    
  
 
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
      jcas.throwFeatMissing("score", "uncc2014watsonsim.anagram.CandidateAnswer");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_score);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setScore(int addr, double v) {
        if (featOkTst && casFeat_score == null)
      jcas.throwFeatMissing("score", "uncc2014watsonsim.anagram.CandidateAnswer");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_score, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CandidateAnswer_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_answer = jcas.getRequiredFeatureDE(casType, "answer", "uima.cas.String", featOkTst);
    casFeatCode_answer  = (null == casFeat_answer) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_answer).getCode();

 
    casFeat_questionText = jcas.getRequiredFeatureDE(casType, "questionText", "uima.cas.String", featOkTst);
    casFeatCode_questionText  = (null == casFeat_questionText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_questionText).getCode();

 
    casFeat_supportingPassages = jcas.getRequiredFeatureDE(casType, "supportingPassages", "uima.cas.FSList", featOkTst);
    casFeatCode_supportingPassages  = (null == casFeat_supportingPassages) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_supportingPassages).getCode();

 
    casFeat_score = jcas.getRequiredFeatureDE(casType, "score", "uima.cas.Double", featOkTst);
    casFeatCode_score  = (null == casFeat_score) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_score).getCode();

  }
}



    