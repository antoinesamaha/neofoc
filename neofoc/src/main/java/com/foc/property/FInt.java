/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import java.text.Format;

import com.foc.desc.FocObject;
import com.foc.desc.field.FIntField;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FInt extends FProperty {
  protected int iVal;
  private int backupInt = 0;

  public FInt(FocObject focObj, int fieldID, int iVal) {
    super(focObj, fieldID);
    this.iVal = iVal;
  }

  public int hashCode() {
    return iVal;
  }

  public int compareTo(FProperty prop) {
    return (prop != null) ? iVal - prop.getInteger() : 1;
  }

  public boolean isEmpty(){
    return iVal == 0;
  }

  public int getInteger() {
    return iVal;
  }

  public void setInteger(int iVal, boolean userEditingEvent, boolean controlersActive) {
    if(iVal != this.iVal || isValueNull()){
    	if(getFocField() == null || !controlersActive || getFocField().isPropertyModificationAllowed(this, iVal, true)){
	      this.iVal = iVal;
	      setValueNull(false);
	      notifyListeners(userEditingEvent);
    	}
    }
  }

  public void setInteger(int iVal, boolean userEditingEvent) {
  	setInteger(iVal, userEditingEvent, true);
  }
  
  public void setInteger(int iVal) {
  	setInteger(iVal, false);
  }

  public String getSqlString() {
  	if(isValueNull()) {
  		return getNullSQLValue();
  	} else {
  		return getString();
  	}
  }
  
  public String getString() {
 		return String.valueOf(iVal);
  }

  public void setString(String str, boolean userEditingEvent) {
  	if (str == null || str.compareTo("") == 0) {
	    if (isAllowNullProperties()) {
	    	boolean notifyListeners = !isValueNull();
	      setValueNull_AndResetIntrinsicValue(false);
	      if (notifyListeners) notifyListeners(userEditingEvent);
	    } else {
	    	setInteger(0, userEditingEvent);
	    }
    } else {
      setInteger(Integer.parseInt(str), userEditingEvent);
    }
  }

  public void setString(String str) {
  	setString(str, false); 
  }

  public void backup() {
    backupInt = this.iVal;
  }

  public void restore() {
    setInteger(backupInt);
  }
  
  public String getModificationLogString() {
  	return backupInt+" -> "+getInteger();
  }

  public double getDouble() {
    return (double) iVal;
  }

  public void setDouble(double dVal) {
    setInteger((int) dVal);
  }

  public void setObject(Object obj) {
	  if (obj != null) {

		  int iVal = 0;
		  try{
			  iVal = ((Integer) obj).intValue();
		  }catch(Exception e){
			  iVal = Integer.valueOf((String) obj);
		  }
		  setInteger(iVal);
	  }
  }

  public Object getObject() {
    return Integer.valueOf(getInteger());
  }

  public Object getTableDisplayObject(Format format) {
    Object displayObj = null;
    int realValue = getInteger();
    
    if(realValue == 0 && getFocObject() != null && getFocField() != null){
      FProperty fPropAncestorCustomized = getFocObject().getFirstAncestorCustomizedProperty(getFocField().getID());
      if(fPropAncestorCustomized != null){
        realValue = fPropAncestorCustomized.getInteger();
      }
    }
    
    if( realValue == 0 && getFocField() != null && !((FIntField)getFocField()).isDisplayZeroValues() ){
      displayObj = "";
    }else{
      displayObj = format != null ? format.format(Integer.valueOf(realValue)) : getString();
    }
    
    return displayObj.toString();
  }

  public void setTableDisplayObject(Object obj, Format format) {
    setString((String)obj);
  }
  
  public void setEmptyValue(){
  	setInteger(0);
  }

  public void setValueNull_AndResetIntrinsicValue(boolean notifyListeners) {
  	iVal = 0;
  	super.setValueNull_AndResetIntrinsicValue(notifyListeners);
  }
  
  //-------------------------------
  // VAADIN Property implementation
  //-------------------------------
  
  @Override
  public Object getValue() {
//    return getInteger();
	  return getObject();
  }

//  @Override
  public void setValue(Object newValue) {
//    setInteger(((Integer)newValue).intValue());
	  setObject(newValue);
  }

}
