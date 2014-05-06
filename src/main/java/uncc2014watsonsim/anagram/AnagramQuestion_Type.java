
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
import org.apache.uima.jcas.cas.TOP_Type;

/** The base question type
 * Updated by JCasGen Tue May 06 03:49:45 EDT 2014
 * @generated */
public class AnagramQuestion_Type extends TOP_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (AnagramQuestion_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = AnagramQuestion_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new AnagramQuestion(addr, AnagramQuestion_Type.this);
  			   AnagramQuestion_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new AnagramQuestion(addr, AnagramQuestion_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = AnagramQuestion.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uncc2014watsonsim.anagram.AnagramQuestion");
 
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
      jcas.throwFeatMissing("fullText", "uncc2014watsonsim.anagram.AnagramQuestion");
    return ll_cas.ll_getStringValue(addr, casFeatCode_fullText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFullText(int addr, String v) {
        if (featOkTst && casFeat_fullText == null)
      jcas.throwFeatMissing("fullText", "uncc2014watsonsim.anagram.AnagramQuestion");
    ll_cas.ll_setStringValue(addr, casFeatCode_fullText, v);}
    
  
 
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
      jcas.throwFeatMissing("anagramText", "uncc2014watsonsim.anagram.AnagramQuestion");
    return ll_cas.ll_getStringValue(addr, casFeatCode_anagramText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAnagramText(int addr, String v) {
        if (featOkTst && casFeat_anagramText == null)
      jcas.throwFeatMissing("anagramText", "uncc2014watsonsim.anagram.AnagramQuestion");
    ll_cas.ll_setStringValue(addr, casFeatCode_anagramText, v);}
    
  
 
  /** @generated */
  final Feature casFeat_candidateAnswers;
  /** @generated */
  final int     casFeatCode_candidateAnswers;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getCandidateAnswers(int addr) {
        if (featOkTst && casFeat_candidateAnswers == null)
      jcas.throwFeatMissing("candidateAnswers", "uncc2014watsonsim.anagram.AnagramQuestion");
    return ll_cas.ll_getRefValue(addr, casFeatCode_candidateAnswers);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCandidateAnswers(int addr, int v) {
        if (featOkTst && casFeat_candidateAnswers == null)
      jcas.throwFeatMissing("candidateAnswers", "uncc2014watsonsim.anagram.AnagramQuestion");
    ll_cas.ll_setRefValue(addr, casFeatCode_candidateAnswers, v);}
    
  
 
  /** @generated */
  final Feature casFeat_questionCategory;
  /** @generated */
  final int     casFeatCode_questionCategory;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getQuestionCategory(int addr) {
        if (featOkTst && casFeat_questionCategory == null)
      jcas.throwFeatMissing("questionCategory", "uncc2014watsonsim.anagram.AnagramQuestion");
    return ll_cas.ll_getStringValue(addr, casFeatCode_questionCategory);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setQuestionCategory(int addr, String v) {
        if (featOkTst && casFeat_questionCategory == null)
      jcas.throwFeatMissing("questionCategory", "uncc2014watsonsim.anagram.AnagramQuestion");
    ll_cas.ll_setStringValue(addr, casFeatCode_questionCategory, v);}
    
  
 
  /** @generated */
  final Feature casFeat_QuestionText;
  /** @generated */
  final int     casFeatCode_QuestionText;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getQuestionText(int addr) {
        if (featOkTst && casFeat_QuestionText == null)
      jcas.throwFeatMissing("QuestionText", "uncc2014watsonsim.anagram.AnagramQuestion");
    return ll_cas.ll_getStringValue(addr, casFeatCode_QuestionText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setQuestionText(int addr, String v) {
        if (featOkTst && casFeat_QuestionText == null)
      jcas.throwFeatMissing("QuestionText", "uncc2014watsonsim.anagram.AnagramQuestion");
    ll_cas.ll_setStringValue(addr, casFeatCode_QuestionText, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public AnagramQuestion_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_fullText = jcas.getRequiredFeatureDE(casType, "fullText", "uima.cas.String", featOkTst);
    casFeatCode_fullText  = (null == casFeat_fullText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_fullText).getCode();

 
    casFeat_anagramText = jcas.getRequiredFeatureDE(casType, "anagramText", "uima.cas.String", featOkTst);
    casFeatCode_anagramText  = (null == casFeat_anagramText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_anagramText).getCode();

 
    casFeat_candidateAnswers = jcas.getRequiredFeatureDE(casType, "candidateAnswers", "uima.cas.FSList", featOkTst);
    casFeatCode_candidateAnswers  = (null == casFeat_candidateAnswers) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_candidateAnswers).getCode();

 
    casFeat_questionCategory = jcas.getRequiredFeatureDE(casType, "questionCategory", "uima.cas.String", featOkTst);
    casFeatCode_questionCategory  = (null == casFeat_questionCategory) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_questionCategory).getCode();

 
    casFeat_QuestionText = jcas.getRequiredFeatureDE(casType, "QuestionText", "uima.cas.String", featOkTst);
    casFeatCode_QuestionText  = (null == casFeat_QuestionText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_QuestionText).getCode();

  }
}



    