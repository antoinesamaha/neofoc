/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import java.text.Format;

import com.foc.desc.FocObject;
import com.vaadin.data.util.converter.Converter;

/**
 * @author 01Barmaja
 */
public class FBoolean extends FProperty {
  private boolean bVal;
  private boolean backupBool = false;

  public FBoolean(FocObject focObj, int fieldID, boolean bVal) {
    super(focObj, fieldID);
    this.bVal = bVal;
  }

  public int hashCode() {
    return getInteger();
  }

  public int compareTo(FProperty prop) {
    return (prop != null) ? prop.getInteger() - getInteger() : 1;
  }

  public int getInteger() {
    return bVal ? 1 : 0;
  }

  public void setInteger(int iVal) {
    setBoolean(iVal != 0);
  }

  public String getString() {
    //return String.valueOf(bVal);
    return isValueNull() ? getNullSQLValue() : String.valueOf(getInteger());
  }

  public void setString(String str) {
    if (str == null || str.compareTo("") == 0) {
    	if (isAllowNullProperties()) {
    		setBoolean_WithoutListeners(false);
    		setValueNull_WithListener(true);
    	} else {
    		setBoolean(false);
    	}
    } else {
    	try{
    		setInteger(Integer.valueOf(str).intValue());
      //setBoolean(Boolean.getBoolean(str));
    	}catch(NumberFormatException e){
    		if(str.toUpperCase().equals("TRUE")){
    			setString("1");
    		}else if(str.toUpperCase().equals("FALSE")){
    			setString("0");
    		}
    	}
    }
  }

  public void backup() {
    backupBool = this.bVal;
  }

  public void restore() {
    this.bVal = backupBool;
  }

  public String getModificationLogString() {
  	return (backupBool ? "True" : "False") + " -> " + (bVal ? "True" : "False") ;
  }
  
  public double getDouble() {
    return (double) getInteger();
  }

  public void setDouble(double dVal) {
    setInteger((int) dVal);
  }
  
  public void setObject(Object obj) {
  	setObject(obj, false);
  }

  public void setObject(Object obj, boolean userEdit) {
    if (obj != null) {
    	setBoolean(((Boolean) obj).booleanValue());
    }
  }
  
  public Object getObject() {
    return new Boolean(getBoolean());
  }

  public void setBoolean(boolean b) {
    setBoolean(b, false);
  }

  public void setBoolean(boolean b, boolean userEditingEvent) {
  	boolean notifyListener = false;
  	if(isValueNull()) {
  		setValueNull(false);
  		notifyListener = true;
  	}
    if(bVal != b) {
    	bVal = b;
    	notifyListener = true;
    }
    if(notifyListener) notifyListeners(userEditingEvent);
  }

  public void setBoolean_WithoutListeners(boolean b) {
  	setValueNull(false);
    bVal = b;
  }

  public boolean getBoolean() {
    return bVal;
  }

  public boolean getBackupBoolean() {
    return backupBool;
  }

  public void setEmptyValue(){
  	setBoolean(false);
  }
  
	@Override
	public Object getValue() {
		return getObject();
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException, Converter.ConversionException {
		setObject(newValue, true);
	}
	
  @Override
  public Object vaadin_TableDisplayObject(Format format, String captionProperty){
  	return getObject();
  }
  
  @Override
  public Object getXMLValue(){
    boolean bool = getBoolean();
    
    if(bool) return "true";
    else return "false";
  }
  
  public void setValueNull_AndResetIntrinsicValue(boolean notifyListeners) {
  	bVal = false;
  	super.setValueNull_AndResetIntrinsicValue(notifyListeners);
  }

}
