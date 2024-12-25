/*
 * Created on Jun 27, 2005
 */
package com.foc.property;

import com.foc.*;
import com.foc.desc.*;
import com.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class FPropertyArray extends FProperty{

  private FProperty propertyArray[] = null;
  
  public FPropertyArray(FocObject focObj, int fieldID, FProperty firstProperty) {
    super(focObj, fieldID, true);
    /*
    initStateVariables();
    setFocField(focObj.getThisFocDesc().getFieldArrayByID(fieldID));
    attachToObject(focObj);
    */
    
    try{
      FFieldArray fieldArray = (FFieldArray)getFocField();
  
      int size = fieldArray.getSize();
      propertyArray = new FProperty[size];
      for(int i=0; i<size; i++){
        if(i == 0){
          propertyArray[i] = firstProperty;
        }else{
          propertyArray[i] = (FProperty) firstProperty.clone(getFocObject(), fieldArray.getFieldAt(i));
        }
      }
    }catch(Exception e){
      Globals.logException(e);
    }
  }

  public String getString() {
    String str = "";
    FFieldArray fieldArray = (FFieldArray)getFocField();
    if(fieldArray != null){
      FField currFld = fieldArray.getCurrentField();
      if(currFld != null){
        FProperty prop = getFocObject().getFocProperty(currFld.getID());
        str = prop.getString();
      }
    }
    
    return str;
  }
  
  public Object getObject() {
    return getString();
  }
  
}
