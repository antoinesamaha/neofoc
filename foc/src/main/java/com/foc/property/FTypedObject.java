/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import com.fab.model.table.UserDefinedObject;
import com.foc.desc.*;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FTypedObjectField;

/**
 * @author 01Barmaja
 */
public class FTypedObject extends FObject{
	
	private FocDesc    focDesc = null ;
	
  public FTypedObject(FocObject fatherObj, int fieldID, FocTypedObject typedObject){
    super(fatherObj, fieldID, null);
    if(typedObject != null){
      setObject(typedObject);
    }
  }

  public FTypedObject(FocObject fatherObj, int fieldID) {
    super(fatherObj, fieldID, null);
  }
  
  public FocDesc getFocDesc(){
    if(focDesc == null){
      FTypedObjectField fld = (FTypedObjectField) getFocField();
      if(fld != null){
      	FFieldPath fieldPath = fld.getTableNameFieldPath();
      	FocObject actualFocObject = getFocObject();
      	if(fieldPath != null && actualFocObject != null){
      		FDescPropertyStringBased descStringBased = (FDescPropertyStringBased) fieldPath.getPropertyFromObject(actualFocObject);
      		focDesc = descStringBased != null ? descStringBased.getSelectedFocDesc() : null;
      	}
      }
    }
    return focDesc;
  }
  
  public void setObject(Object paramSet){
  	super.setObject(paramSet);
  }
  
  public void createNewFocObject(){
  	newObject();
  }

  @Override
  protected void setObject_Encapsulation(FocObject obj){
  	super.setObject_Encapsulation(obj);
  }

}
