/*
 * Created on Dec 2, 2005
 */
package com.foc.list;

import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.property.FProperty;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class ListSumOperation implements ListOperation{
  protected FFieldPath fieldPath = null;
  private   String fieldPathString = null;
  protected FProperty sumProp = null;
  protected double sum = 0;
  
  public ListSumOperation(FFieldPath fieldPath, FProperty sumProp){
    this.fieldPath = fieldPath; 
    this.sumProp = sumProp;
  }
  
  public ListSumOperation(String fieldPathString, FProperty sumProp){
    this.fieldPathString = fieldPathString; 
    this.sumProp = sumProp;
  }
  
  public void dispose(){
    if(fieldPath != null){
      fieldPath.dispose();
      fieldPath = null;
    }
    sumProp = null;
  }
  
  public void reset(){
    sum = 0;  
  }
  
  public void treatObject(FocObject obj){
    FProperty prop = fieldPath != null ? fieldPath.getPropertyFromObject(obj) : null;
    if(prop == null && !Utils.isStringEmpty(fieldPathString)){
    	prop = obj.getFocPropertyForPath(fieldPathString);
    }
    if(prop != null){
      sum += prop.getDouble();
    }
  }
  
  public void sendResult(){
    sumProp.setDouble(sum);
  }
}
