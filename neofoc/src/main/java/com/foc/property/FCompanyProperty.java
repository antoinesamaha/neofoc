/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import com.foc.Globals;
import com.foc.desc.*;
import com.foc.list.FocList;

/**
 * @author 01Barmaja
 */
public class FCompanyProperty extends FObject {
  
	/*
	public FCompanyProperty(FocObject fatherObj, int fieldID, FocObject focObj, int displayField) {
    super(fatherObj, fieldID, focObj, displayField);
  }

  public FCompanyProperty(FocObject fatherObj, int fieldID, FocObject focObj, int displayField, FocList localSourceList) {
    super(fatherObj, fieldID, focObj, displayField, localSourceList);
  }
  */

  public FCompanyProperty(FocObject fatherObj, int fieldID, FocObject focObj) {
    super(fatherObj, fieldID, focObj);
  }
  
  public void setCompanyToDefault(){
  	FocObject obj = getFocObject();
  	if(obj != null){
  		obj.setCompany(Globals.getApp().getCurrentCompany());
  	}
  }
  
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