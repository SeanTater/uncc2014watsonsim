
/* First created by JCasGen Wed Mar 19 16:41:59 EDT 2014 */
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

/** Containing a list of search results
 * Updated by JCasGen Mon Apr 28 12:46:51 EDT 2014
 * @generated */
public class searchResultList_Type extends TOP_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (searchResultList_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = searchResultList_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new searchResultList(addr, searchResultList_Type.this);
  			   searchResultList_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new searchResultList(addr, searchResultList_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = searchResultList.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uncc2014watsonsim.uima.types.searchResultList");
 
  /** @generated */
  final Feature casFeat_list;
  /** @generated */
  final int     casFeatCode_list;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getList(int addr) {
        if (featOkTst && casFeat_list == null)
      jcas.throwFeatMissing("list", "uncc2014watsonsim.uima.types.searchResultList");
    return ll_cas.ll_getRefValue(addr, casFeatCode_list);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setList(int addr, int v) {
        if (featOkTst && casFeat_list == null)
      jcas.throwFeatMissing("list", "uncc2014watsonsim.uima.types.searchResultList");
    ll_cas.ll_setRefValue(addr, casFeatCode_list, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public searchResultList_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_list = jcas.getRequiredFeatureDE(casType, "list", "uima.cas.FSList", featOkTst);
    casFeatCode_list  = (null == casFeat_list) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_list).getCode();

  }
}



    