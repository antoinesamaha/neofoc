/*
 * Created on 17 Jan 2014
 */
package com.foc.property;

import com.foc.desc.FocObject;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FDepartmentProperty extends FObject {
  
	/*
	public FCompanyProperty(FocObject fatherObj, int fieldID, FocObject focObj, int displayField) {
    super(fatherObj, fieldID, focObj, displayField);
  }

  public FCompanyProperty(FocObject fatherObj, int fieldID, FocObject focObj, int displayField, FocList localSourceList) {
    super(fatherObj, fieldID, focObj, displayField, localSourceList);
  }
  */

  public FDepartmentProperty(FocObject fatherObj, int fieldID, FocObject focObj) {
    super(fatherObj, fieldID, focObj);
  }
  
//  public void setCompanyToDefault(){
//  	FocObject obj = getFocObject();
//  	if(obj != null){
//  		obj.setCompany(Globals.getApp().getCurrentCompany());
//  	}
//  }
  
  /*
  @Override
  public void notifyListeners(boolean userEditingEvent) {
    super.notifyListeners(userEditingEvent);
    FocObject focObject = getFocObject();
    FocList   list      = (FocList) focObject.getFatherSubject();
    list.
  }
  */
}