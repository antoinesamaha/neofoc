/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import java.text.Format;

import com.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class FLong extends FProperty {
  protected long lVal;
  private long backupLong = 0;

  public FLong(FocObject focObj, int fieldID, long lVal) {
    super(focObj, fieldID);
    this.lVal = lVal;
  }

  public int hashCode() {
    return (Long.valueOf(lVal).hashCode());
  }

  public int compareTo(FProperty prop) {
  	int comp = 1;
    if (prop != null) {
      if (prop.getLong() > getLong()) {
        comp = -1;
      } else if (prop.getLong() < getLong()) {
        comp = 1;
      } else {
        comp = 0;
      }
    }
    return comp;
  }

  public boolean isEmpty(){
    return lVal == 0;
  }

  public long getLong() {
    return lVal;
  }

  public void setLong(long lVal, boolean userEditingEvent) {
    if(lVal != this.lVal){
      this.lVal = lVal;
      setValueNull(false);
      notifyListeners(userEditingEvent);
    }
  }

  public void setLong(long lVal) {
  	setLong(lVal, false);
  }

  public String getSqlString() {
  	if(isValueNull()) {
  		return getNullSQLValue();
  	} else {
  		return getString();
  	}
  }
  
  public String getString() {
    return String.valueOf(lVal);
  }

  public void setString(String str, boolean userEditingEvent) {
    if (str == null || str.compareTo("") == 0) {
	    if (isAllowNullProperties()) {
	    	boolean notifyListeners = !isValueNull();
	      setValueNull_AndResetIntrinsicValue(false);
	      if (notifyListeners) notifyListeners(userEditingEvent);
	    } else {
	    	setLong(0, userEditingEvent);
	    }
    } else {
      setLong(Integer.parseInt(str), userEditingEvent);
    }
  }

  public void setString(String str) {
  	setString(str, false);
  }

  public void backup() {
   backupLong = this.lVal;
  }

  public void restore() {
    this.lVal = backupLong;
  }

  public double getDouble() {
    return (double) lVal;
  }

  public void setDouble(double dVal) {
    setLong((long) dVal);
  }

  public void setObject(Object obj) {
    if (obj != null) {
      setLong(((Long) obj).longValue());
    }
  }

  public Object getObject() {
    return Long.valueOf(getLong());
  }

  public Object getTableDisplayObject(Format format) {
    return getString();
  }

  public void setTableDisplayObject(Object obj, Format format) {
    setString((String)obj);
  }
  
  public void setEmptyValue(){
  	setLong(0);
  }
  
  public void setValueNull_AndResetIntrinsicValue(boolean notifyListeners) {
  	lVal = 0;
  	super.setValueNull_AndResetIntrinsicValue(notifyListeners);
  }
}
