/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import java.text.Format;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FNumField;
import com.foc.util.FocMath;
import com.vaadin.data.util.converter.Converter.ConversionException;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FDouble extends FProperty {
  private double dVal;
  private double dBackupVal;

  public FDouble(FocObject focObj, int fieldID, double dVal) {
    super(focObj, fieldID);
    this.dVal = dVal;
  }
  
  public void dispose(){
  	super.dispose();
  }

  public int compareTo(FProperty prop) {
    int comp = 1;
    if (prop != null) {
      if (prop.getDouble() > getDouble()) {
        comp = -1;
      } else if (prop.getDouble() < getDouble()) {
        comp = 1;
      } else {
        comp = 0;
      }
    }
    return comp;
  }

  public boolean isEmpty(){
    return dVal == getZeroValue();
  }
  
  public int getInteger() {
    return (int) dVal;
  }

  public void setInteger(int iVal) {
    setDouble((double) iVal);
  }

  public String getString() {
    /*
    FNumField numFld = (FNumField) getFocField();
    numFld.getFormat();
    */
  	if(Double.isNaN(dVal) || Double.isInfinite(dVal)){
  		return "0";
  	}else{
  		return String.valueOf(dVal);
  	}
  }

  public String getSqlString() {
  	if(isValueNull()) {
  		return "NULL";
  	} else if(Double.isNaN(dVal) || Double.isInfinite(dVal)){
  		return "0";
  	}else{
  		return getString();
  	}
  }

  public void setString(String str, boolean userEditingEvent) {
    if (str == null || str.trim().compareTo("") == 0) {
    	if (isAllowNullProperties()) {
    		setDouble_WithoutListeners(0);
    		setValueNull_WithListener(true);
    	} else {
    		setDouble_Internal(0, userEditingEvent);
    	}
    } else {
    	double d = 0;
	    if(str.compareTo("") != 0){
	    	try{
	    		str = str.replace(",","");
	        d = Double.parseDouble(str);
	    	}catch(Exception e){
	    		Globals.logString("parsing string :"+str+" Exception "+e.getMessage());
	    		d = dVal;
	    	}
	    }
	    setDouble_Internal(d, userEditingEvent);
    }
  }
  
  public void setString(String str) {
  	setString(str, false);
  }

  public double getBackup_Double(){
  	return dBackupVal;
  }
  
  public double getDouble() {
    return dVal;
  }

  public void setDouble_UserEditingEvent(double dVal) {
  	setDouble_Internal(dVal, true);
  }

  public void setDouble(double dVal) {
  	setDouble_Internal(dVal, false);
  }
  
  public void setDouble_Internal(double dVal, boolean userEditingEvent) {
    if(dVal != this.dVal || isValueNull()){
    	FNumField numField = (FNumField)getFocField();
    	double roundPrecision = numField != null ? numField.getRoundingPrecision() : 0;
    	if(roundPrecision > 0){
    		dVal = FocMath.round(dVal, 0.00000001);
    		dVal = FocMath.trunc(dVal, roundPrecision);
    	}
    	if(dVal != this.dVal || isValueNull()){
	    	if(getFocField() == null || getFocField().isPropertyModificationAllowed(this, dVal, true)){
	    		this.dVal = dVal;
	    		setValueNull(false);
	    		notifyListeners(userEditingEvent);
	    	}
    	}
      //setInherited(false);
    }
  }

  public void setDouble_WithoutListeners(double dVal) {
    this.dVal = dVal;
  }

  public void setObject(Object obj) {
  	setObject(obj, false);
  }
  
  private void setObject(Object obj, boolean userEditingEvent) {
    if(obj != null){
    	double dVal = 0;
    	try{
    		dVal = ((Double) obj).doubleValue();
    	}catch(Exception e){
    		dVal = Double.valueOf((String) obj);
    	}
    	setDouble_Internal(dVal, userEditingEvent);
    }
  }

  public Object getObject() {
    return Double.valueOf(getDouble());
  }
  
  public double getDouble_ForDisplay(){
  	return getDouble();
  }
  
  public Object getTableDisplayObject(Format format) {
    Object    displayObj = null;
    FNumField numFld     = (FNumField) getFocField();
    if(numFld != null){
	    if(numFld.isNoRights()){
	    	displayObj = FField.NO_RIGHTS_STRING;
	    }else{
		    double    realValue  = getDouble();
		    
		    if(realValue == getZeroValue() && !numFld.isDisplayZeroValues()){
		      displayObj = "";
		    }else{
		    	boolean addParenthesis = numFld.isUseParenthesisForNegativeValues() && realValue < 0;
		    	if(addParenthesis){
		    		realValue = -realValue;
		    	}
		      displayObj = format != null ? format.format(Double.valueOf(realValue)) : getString();
		      if(addParenthesis){
		      	displayObj = "("+displayObj+")";
		    	}
		    }
	    }
    }
    
    return displayObj;
  }

  public void setTableDisplayObject(Object obj, Format format) {
    try{
      if(obj == null || ((String)obj).trim().compareTo("") == 0){
        setDouble_UserEditingEvent(getZeroValue());
      }else{
        if(format != null){
          Number dbl = (Number) format.parseObject((String)obj);
          setDouble_UserEditingEvent(dbl.doubleValue());
        }else{
          setString((String)obj);
        }
      }
    }catch(Exception e){
      Globals.logException(e);
    }
  }
  
  public void backup() {
    dBackupVal = dVal;
  }

  public void restore() {
    dVal = dBackupVal ;
  }  
  
  public void setEmptyValue(){
  	setDouble(getZeroValue());
  }
  
  private double getZeroValue(){
  	FNumField numFld = (FNumField)getFocField();
  	return numFld != null ? numFld.getZeroValue() : 0;
  }
  
  @Override
  public Object getValue() {
//    String str = (String) getTableDisplayObject();
//    return str;
    return getObject();
  }

  @Override
  public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
//    setTableDisplayObject(newValue);
  	setObject(newValue, true);
  }

	public Object vaadin_TableDisplayObject(Format format, String captionProperty){
		Object obj = null;
		if(format != null){
			obj = getTableDisplayObject(format);
		}else{
			obj = getTableDisplayObject();
		}
//		Object obj = getObject();
		return obj;
	}

  public void setValueNull_AndResetIntrinsicValue(boolean notifyListeners) {
  	dVal = 0;
  	super.setValueNull_AndResetIntrinsicValue(notifyListeners);
  }
}
