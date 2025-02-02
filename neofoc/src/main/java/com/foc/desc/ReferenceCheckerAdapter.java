/*
 * Created on Jun 12, 2005
 */
package com.foc.desc;

import com.foc.Globals;
import com.foc.db.SQLFilter;
import com.foc.desc.field.FObjectField;

/**
 * @author 01Barmaja
 */
public class ReferenceCheckerAdapter implements ReferenceChecker, Cloneable {
  private FocDesc   focDesc       = null;//FocDesc to search in for the reference
  private int       objRefField   = 0   ;//The field in which the reference might be stored
	private ReferenceCheckerFilter referenceCheckerFilter = null;
	private Object    voidObject    = null;

	public ReferenceCheckerAdapter(FocDesc focDescToSearchIn, int objRefField){
    this.focDesc     = focDescToSearchIn;
    this.objRefField = objRefField      ;
  }
  
	public void dispose(){
		focDesc    = null;
		dispose_Content();
		referenceCheckerFilter = null;
	}
	
  @Override
  public Object clone() throws CloneNotSupportedException {
  	ReferenceCheckerAdapter refCheck = null;
		try{
			refCheck = (ReferenceCheckerAdapter) super.clone();
			refCheck.afterClone();
		}catch (CloneNotSupportedException e){
			Globals.logException(e);
		}
		return refCheck;
  }
  
  protected void afterClone(){
  }
  
  /*
  public int getNumberOfReferences(FocObject obj, StringBuffer message){
  	return 
    int nbRef = 0;
    
    if(focDesc != null && !obj.isCreated()){
      FObjectField objField = (FObjectField) focDesc.getFieldByID(objRefField);
      if(!objField.isReferenceChecker_PutToZeroWhenReferenceDeleted()){      
	      FocLinkSimple focLinkSimple = new FocLinkSimple(focDesc);
	      FocList       focList       = new FocList(focLinkSimple);
	      SQLFilter     filter        = newFilterAdapted(obj);
	      
	      SQLSelect select = new SQLSelect(focList, focDesc, filter);
	      select.execute();
	      FocList loadedFocList = select.getFocList();
	
	      nbRef = loadedFocList.size();
	      if(nbRef > 0 && message != null){
	        message.append("\n"+focDesc.getStorageName()+", "+objField.getTitle()+", "+nbRef+"times");
	      }
      }
    }    
    return nbRef;
  }
  */
  
  public void deleteObject(FocObject focObjectToRedirectFrom){
  	Globals.getApp().getDataSource().focObject_Delete(focObjectToRedirectFrom, null);
  }
  
  public void redirectReferencesToNewFocObject(FocObject focObjectToRedirectFrom, FocObject focObjectToRedirectTo){
  	Globals.getApp().getDataSource().focObject_Redirect(focObjectToRedirectFrom, focObjectToRedirectTo);
  	/*
  	FocConstructor constr = new FocConstructor(focDesc, null);
  	FocObject newFocObject = constr.newItem();
  	FObject fObjProp = (FObject) newFocObject.getFocProperty(objRefField);
  	boolean isDesactivateListener = fObjProp.isDesactivateListeners();
  	fObjProp.setDesactivateListeners(true);
  	fObjProp.setObject(focObjectToRedirectTo);
  	fObjProp.setDesactivateListeners(isDesactivateListener);
  	SQLUpdate update = new SQLUpdate(focDesc, newFocObject, newFilterAdapted(focObjectToRedirectFrom));
  	update.addQueryField(objRefField);
  	try{
  		update.execute();
  	}catch(Exception e){
  		Globals.logException(e);
  	}
  	*/
  }

  @Override
  public FocDesc getFocDesc(){
  	return focDesc;
  }

  @Override
  public int getObjectFieldID(){
  	return objRefField;
  }
  
	@Override
	public boolean isPutToZeroWhenReferenceDeleted() {
		boolean putToZero = false;
		FObjectField objFld = focDesc != null ? (FObjectField) focDesc.getFieldByID(objRefField) : null;
		if(objFld != null){
			putToZero = objFld.isReferenceChecker_PutToZeroWhenReferenceDeleted();
		}
		return putToZero;
	}

	@Override
	public boolean isDeleteWhenReferenceDeleted() {
		boolean deleteWhenReferenceDeleted = false;
		FObjectField objFld = focDesc != null ? (FObjectField) focDesc.getFieldByID(objRefField) : null;
		if(objFld != null){
			deleteWhenReferenceDeleted = objFld.isReferenceChecker_DeleteWhenReferenceDeleted();
		}
		return deleteWhenReferenceDeleted;
	}

	public void setReferenceCheckerFilter(ReferenceCheckerFilter referenceCheckerFilter) {
		this.referenceCheckerFilter = referenceCheckerFilter;
	}

	@Override
	public boolean applyForThisObject(FocObject focObj) {
		boolean apply = true;
		if(referenceCheckerFilter != null){
			apply = referenceCheckerFilter.applyForThisObject(focObj);
		}
		return apply;
	}
	
  public Object getVoidObject() {
		return voidObject;
	}

	public void setVoidObject(Object voidObject) {
		this.voidObject = voidObject;
	}

	@Override
	public void dispose_Content() {
		if(voidObject != null && voidObject instanceof SQLFilter) {
			((SQLFilter)voidObject).dispose(); 
		}
		voidObject = null;		
	}
}
