/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import com.foc.desc.*;
import com.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class FInLineObject extends FObject{
  
  private void init(){
    FocObject focObj = (FocObject) getObject();
    if(focObj == null){
      newObject();
      focObj = (FocObject) getObject();
      if(focObj != null){
        //This is very important in all inline objects to specify that master objects are the father subjects
        focObj.setFatherSubject(focObj.getMasterObject());
        
        if(focObj.getMasterObject() != null){
          FProperty refProperty = focObj.getMasterObject().getFocProperty(FField.REF_FIELD_ID); 
          if(refProperty != null){
            focObj.putFocPropertyWithSpecifiedIndex(refProperty, getFocDesc().getPropertyArrayLength() - 1);
          }
        }
      }
    }
  }
  
  /*
  public FInLineObject(FocObject fatherObj, int fieldID, FocObject focObj, int displayField) {
    super(fatherObj, fieldID, focObj, displayField);
    init();
  }

  public FInLineObject(FocObject fatherObj, int fieldID, FocObject focObj, int displayField, FocList localSourceList) {
    super(fatherObj, fieldID, focObj, displayField, localSourceList);
    init();
  }
  */

  public FInLineObject(FocObject fatherObj, int fieldID, FocObject focObj) {
    super(fatherObj, fieldID, focObj);
    init();
  }
  
  public FInLineObject(FocObject fatherObj, int fieldID) {
    super(fatherObj, fieldID, null);
    init();
  }
  
  public void copy(FProperty sourceProp){
    FInLineObject sourceObjProp = (FInLineObject)sourceProp;
    
    FocObject sourceObj = (FocObject) sourceObjProp.getObject();
    FocObject targetObj = (FocObject) getObject();
  
    sourceObj.duplicate(targetObj, getFocObject(), false);
  }
  
}
