
/* First created by JCasGen Mon Apr 14 12:07:18 EDT 2014 */
package org.apache.uima.examples;

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

/** Stores detailed information about the original source document from which the current CAS was initialized. All information (like size) refers to the source document and not to the document in the CAS which may be converted and filtered by a CAS Initializer. For example this information will be written to the Semantic Search index so that the original document contents can be retrieved by queries.
 * Updated by JCasGen Tue Apr 22 10:55:59 EDT 2014
 * @generated */
public class SourceDocumentInformation_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (SourceDocumentInformation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = SourceDocumentInformation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new SourceDocumentInformation(addr, SourceDocumentInformation_Type.this);
  			   SourceDocumentInformation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new SourceDocumentInformation(addr, SourceDocumentInformation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = SourceDocumentInformation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.uima.examples.SourceDocumentInformation");
 
  /** @generated */
  final Feature casFeat_uri;
  /** @generated */
  final int     casFeatCode_uri;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getUri(int addr) {
        if (featOkTst && casFeat_uri == null)
      jcas.throwFeatMissing("uri", "org.apache.uima.examples.SourceDocumentInformation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_uri);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUri(int addr, String v) {
        if (featOkTst && casFeat_uri == null)
      jcas.throwFeatMissing("uri", "org.apache.uima.examples.SourceDocumentInformation");
    ll_cas.ll_setStringValue(addr, casFeatCode_uri, v);}
    
  
 
  /** @generated */
  final Feature casFeat_offsetInSource;
  /** @generated */
  final int     casFeatCode_offsetInSource;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getOffsetInSource(int addr) {
        if (featOkTst && casFeat_offsetInSource == null)
      jcas.throwFeatMissing("offsetInSource", "org.apache.uima.examples.SourceDocumentInformation");
    return ll_cas.ll_getIntValue(addr, casFeatCode_offsetInSource);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOffsetInSource(int addr, int v) {
        if (featOkTst && casFeat_offsetInSource == null)
      jcas.throwFeatMissing("offsetInSource", "org.apache.uima.examples.SourceDocumentInformation");
    ll_cas.ll_setIntValue(addr, casFeatCode_offsetInSource, v);}
    
  
 
  /** @generated */
  final Feature casFeat_documentSize;
  /** @generated */
  final int     casFeatCode_documentSize;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getDocumentSize(int addr) {
        if (featOkTst && casFeat_documentSize == null)
      jcas.throwFeatMissing("documentSize", "org.apache.uima.examples.SourceDocumentInformation");
    return ll_cas.ll_getIntValue(addr, casFeatCode_documentSize);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocumentSize(int addr, int v) {
        if (featOkTst && casFeat_documentSize == null)
      jcas.throwFeatMissing("documentSize", "org.apache.uima.examples.SourceDocumentInformation");
    ll_cas.ll_setIntValue(addr, casFeatCode_documentSize, v);}
    
  
 
  /** @generated */
  final Feature casFeat_lastSegment;
  /** @generated */
  final int     casFeatCode_lastSegment;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getLastSegment(int addr) {
        if (featOkTst && casFeat_lastSegment == null)
      jcas.throwFeatMissing("lastSegment", "org.apache.uima.examples.SourceDocumentInformation");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_lastSegment);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLastSegment(int addr, boolean v) {
        if (featOkTst && casFeat_lastSegment == null)
      jcas.throwFeatMissing("lastSegment", "org.apache.uima.examples.SourceDocumentInformation");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_lastSegment, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public SourceDocumentInformation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_uri = jcas.getRequiredFeatureDE(casType, "uri", "uima.cas.String", featOkTst);
    casFeatCode_uri  = (null == casFeat_uri) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_uri).getCode();

 
    casFeat_offsetInSource = jcas.getRequiredFeatureDE(casType, "offsetInSource", "uima.cas.Integer", featOkTst);
    casFeatCode_offsetInSource  = (null == casFeat_offsetInSource) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_offsetInSource).getCode();

 
    casFeat_documentSize = jcas.getRequiredFeatureDE(casType, "documentSize", "uima.cas.Integer", featOkTst);
    casFeatCode_documentSize  = (null == casFeat_documentSize) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentSize).getCode();

 
    casFeat_lastSegment = jcas.getRequiredFeatureDE(casType, "lastSegment", "uima.cas.Boolean", featOkTst);
    casFeatCode_lastSegment  = (null == casFeat_lastSegment) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lastSegment).getCode();

  }
}



    