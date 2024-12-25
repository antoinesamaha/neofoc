/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import java.text.Format;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.FocRef;
import com.foc.desc.field.FField;
import com.vaadin.data.util.converter.Converter;

/**
 * @author 01Barmaja
 */
public class FReference extends FProperty {
  
  private FocRef reference = null;
  private FocRef backupReference = null;  

  private void init(FocRef ref){
    reference = ref;
    if(reference == null){
      reference = new FocRef();
    }
  }
  
  public FReference(FocObject focObj, int fieldID, FocRef ref) {
    super(focObj, fieldID);
    init(ref);
  }

  public FReference(FocObject focObj) {
    super(focObj, FField.REF_FIELD_ID);
    init(null);
  }
  
  public void dispose(){
    if(reference != null){
      reference.dispose();
      reference = null;
    }
    
    if(backupReference != null){
      backupReference.dispose();
      backupReference = null;
    }
    super.dispose();
  }
  
  public int hashCode() {
    return (int) reference.getLong();
  }

  public int compareTo(FProperty prop) {
  	int  comp = 0;
  	long diff = (prop != null) ? getLong() - prop.getLong() : 1;
  	if(diff > 0) comp = 1;
  	if(diff < 0) comp = -1;
    return comp;
  }

  public void setReference(FocRef ref){
    if(reference.compareTo(ref) != 0){
      setReferenceWithoutNotification(ref);
      notifyListeners();
    }
  }

  public void setReferenceWithoutNotification(FocRef ref){
    if(reference != null){
      if(ref != null){
        reference.copy(ref);
      }else{
        reference.setLong(0);
      }
    }else{
      if(ref != null){
        reference = (FocRef) ref.clone();
      }else{
        reference = new FocRef(0);
      }
    }
  }

  public FocRef getReferenceClone(){
    return reference != null ? (FocRef)reference.clone() : null;
  }
 
  public int getInteger() {
    return reference != null ? (int) reference.getLong() : 0;
  }
  
  public long getLong() {
    return reference != null ? reference.getLong() : 0;
  }

//  public void setInteger(int iVal, boolean userEditingEvent, boolean withNotification) {
//  	setLong(iVal, userEditingEvent, withNotification);
//  }
  
  public void setLong(long iVal, boolean userEditingEvent, boolean withNotification) {
    if(iVal != reference.getLong()){
      reference.setLong(iVal);
      if(withNotification){
      	notifyListeners(userEditingEvent);
      }
    }
  }

//  public void setInteger(int iVal, boolean userEditingEvent) {
//  	setLong(iVal, userEditingEvent, true);
//  }
  public void setLong(long iVal, boolean userEditingEvent) {
  	setLong(iVal, userEditingEvent, true);
  }
  	
  public void setInteger(int iVal) {
  	setLong(iVal, false);
  }
  
  public void setLong(long iVal) {
  	setLong(iVal, false);
  }
  
  public void setLong_WithoutNotification(long iVal) {
  	setLong(iVal, false, false);
  }
  
  public void addListener(FPropertyListener propListener) {
    super.addListener(propListener);
  }

  public String getString() {
    return String.valueOf(getLong());
  }

  public void setString(String str, boolean userEditingEvent) {
    if (str == null || str.compareTo("") == 0) {
      setLong(0, userEditingEvent);
    } else {
      setLong(Long.parseLong(str), userEditingEvent);
    }
  }

  public void setString(String str) {
  	setString(str, false);
  }

  @Override
  public String getSqlString() {
  	String str = super.getSqlString();
  	long sqlRef = getLong();
  	if(sqlRef <= 0){
  		if(Globals.getDBManager().isProviderSupportNullValues()){
  			str = null;
  		}else if(str.equals("0") && ConfigInfo.isAdaptConstraints()){
  			str = "NULL";
  		} else {
  			str = String.valueOf(sqlRef);
  		}
  	}
  	return str;
  }
  
  public void backup() {
    backupReference = (FocRef) reference.clone();
  }

  public void restore() {
    setReference(backupReference);
  }

  public double getDouble() {
    return (double) getInteger();
  }

  public void setDouble(double dVal) {
    setInteger((int) dVal);
  }

  public void setObject(Object obj) {
    if (obj != null) {
      setReference((FocRef)obj);
    }
  }

  public Object getObject() {
    return (Object)reference;
  }

  public Object getTableDisplayObject(Format format) {
    return getString();
  }

  public void setTableDisplayObject(Object obj, Format format) {
    setString((String)obj);
  }
  
  // Vaadin Property
  // ---------------
  
	@Override
	public Object getValue() {
		return getString();
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException, Converter.ConversionException {
		setString((String) newValue);
	}
}
