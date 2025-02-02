/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
// Instance
// Static

/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import java.sql.Time;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.FocObject;

/**
 * @author 01Barmaja
 */
public class FTime extends FProperty {
  private java.sql.Time time;
  private java.sql.Time backupTime = null;
  private static SimpleDateFormat timeFormat      = null;
  private static SimpleDateFormat timeFormatShort = null;
  private static SimpleDateFormat sqlTimeFormat   = null;
  private static java.sql.Time    ZERO_TIME       = null;

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // Instance
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public FTime(FocObject focObj, int fieldID, java.sql.Time time) {
    super(focObj, fieldID);
    /*if (timeFormat == null) {
      timeFormat = new SimpleDateFormat("HH:mm");
    }
    if (sqlTimeFormat == null) {
      sqlTimeFormat = new SimpleDateFormat("dd/mon/yy HH:mm:ss");
    }*/
    this.time = getZeroTime_Copy();
    if(time != null){
      setTime(time); 
    }
  }
  
  public static String convertTimeToSQLString(int provider, java.util.Date date){
    String str = null;
    if (sqlTimeFormat == null) {
      if (provider == DBManager.PROVIDER_ORACLE){
        str = "01-JAN-1970 ";
        sqlTimeFormat = new SimpleDateFormat("dd-MMM-yyyy H:m:ss");
      }else{
        str = "1970-01-01 ";
        sqlTimeFormat = new SimpleDateFormat("yyyy-MM-dd H:m:ss");
      }
    }
    str = date != null ? sqlTimeFormat.format(date) : sqlTimeFormat.format(getZeroTime());
    
  	if(provider == DBManager.PROVIDER_MSSQL){
  		str = "CAST(N'"+str+"' AS Time)";
  	}
    
    return str;
  }
  
  public String convertTimeToSQLString(java.util.Date date){
  	return convertTimeToSQLString(getProvider(), date);
  }
  
  public static SimpleDateFormat getTimeFormat(){
    if (timeFormat == null) {
      timeFormat = new SimpleDateFormat("HH:mm");
    }
    return timeFormat;
  }

  private static SimpleDateFormat getTimeFormatShort(){
    if (timeFormatShort == null) {
      timeFormatShort = new SimpleDateFormat("HHmm");
    }
    return timeFormatShort;
  }

  public static String convertTimeToDisplayString(java.util.Date date){   
    return date != null ? getTimeFormat().format(date) : getTimeFormat().format(getZeroTime());
  }

  public static java.util.Date convertStringToTime(String str){   
  	java.util.Date date = null;
  	
  	try{
	    if(str != null && str.trim().compareTo("") != 0){
	    	str = str.replace(".", ":");
    		date = getTimeFormat().parse(str);
	    }else{
	    	date = getZeroTime_Copy();
	    }
  	}catch(Exception e){
  		try{
				date = getTimeFormatShort().parse(str);
			}catch (ParseException e1){
				Globals.logExceptionWithoutPopup(e);
				date = getZeroTime_Copy();
			}
  	}
    
    return date;
  }

  public static Time getZeroTime_Copy(){
  	return (Time) getZeroTime().clone();
  }
  
  public static Time getZeroTime(){
  	if(ZERO_TIME == null){
  		Calendar cal = Calendar.getInstance();
  		cal.clear();
  		cal.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
  		ZERO_TIME = new Time(cal.getTime().getTime());
  	}
  	return ZERO_TIME;
  }
  
  public boolean checkTime(){
    /*
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.YEAR) > 1970;
    */
    return true;//!isEmpty();
  }
  
  public static boolean isEmpty(java.sql.Time time){
    return time.getTime() == getZeroTime().getTime();
  }
  
  public boolean isEmpty(){
    //System.out.println("time = "+date.getTime());
    //return date.getTime() < Globals.DAY_TIME;
    return FTime.isEmpty(time);
  }
  
  public java.sql.Time getTime() {
    return (java.sql.Time) time.clone();
  }

  public void setTime(java.sql.Time time) {
    if(time != null && this.time != null && !time.toString().equals(this.time.toString()) && time.getTime() != this.time.getTime()){
    	setValueNull(false);
      this.time.setTime(time.getTime());
      notifyListeners();
    }
  }

  public String getString() {
    String str = "";
    if(checkTime()){
      str = convertTimeToDisplayString(time);
    }
    return str;
  }

  public String getSqlString() {
  	if (isValueNull()) {
  		return getNullSQLValue();
  	} else {
      if (getProvider() == DBManager.PROVIDER_ORACLE){
        return "TO_DATE (" + "'" + convertTimeToSQLString(time) + "'" + " , "+ "'DD-MON-YYYY HH24:MI:SS')";
      }else if(getProvider() == DBManager.PROVIDER_POSTGRES){
      	return "'" + convertTimeToSQLString(time) + "'";
      }else if(getProvider() == DBManager.PROVIDER_MSSQL){
      	return "CAST(N'"+time.toString()+"' AS Time)";
      }else if(getProvider() == DBManager.PROVIDER_H2){
      	return "\'" + time.toString() + "\'";
      }else{
        return "\"" + time.toString() + "\"";
      }
  	}
  }

  public void setSqlStringInternal(String str) {
    try {
    	if (str == null){
	  		if (isAllowNullProperties()) {
	  			time = getZeroTime_Copy();
	  			setValueNull(true);
	  		}
    	} else {
	      if (str != null && !str.equals("00:00:00")){
	      	if(getProvider() == DBManager.PROVIDER_ORACLE){
	      		try{
	      			if(str.length()-1 > 11 && 11+5 < str.length()){
		      			String timeStr = str.substring(11, 11+5);//(str.length() -3));
		      			if(!timeStr.equals("00:00")){
		      				time = java.sql.Time.valueOf(timeStr+":00");
		      			}
	      			}else{
	      				time = java.sql.Time.valueOf("00:00:00");
	      			}
	      		}catch(Exception e){
	      			Globals.logString("FTime.setSqlStringInternal() str="+str);
	      			Globals.logExceptionWithoutPopup(e);
	      		}
	      	}else if(getProvider() == DBManager.PROVIDER_MSSQL){
	      		int dotIndex = str.indexOf('.');
	      		if(dotIndex > 0){
	      			str = str.substring(0, dotIndex);
	      		}
	      		time = java.sql.Time.valueOf(str);
	      	}else{
	      		time = java.sql.Time.valueOf(str);
	      	}
	      	setValueNull(false);
	      }
    	}
    } catch (Exception e) {
    	Globals.logString(str);
      Globals.logException(e);
    }
  }

  public void setString(String str) {
    try {
      if (str != null) {
        if(str.trim().compareTo("") == 0){
          setTime(getZeroTime_Copy());
        }else{
        	java.util.Date utilDate =convertStringToTime(str);
          long timeInLong = utilDate.getTime();
          java.sql.Time newTime = new java.sql.Time(timeInLong);
          if (utilDate != null) setTime(newTime);
        }
      }
    } catch (Exception e) {
      Globals.logException(e);
    }
  }

  public Object getTableDisplayObject(Format format) {
    /*
    String str = "";
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    if(calendar.get(Calendar.YEAR) > 1970){
      str = getString();
    }
    */
    return getString();
  }
  
  public void setTableDisplayObject(Object obj, Format format) {
    setObject(obj);
  }
  
  public Object getObject(){
    return getString();
  }
  
  public void setObject(Object obj){
    setString((String) obj);
  }
  
  public int compareTo(FProperty prop) {
    java.sql.Time otherTime = null;
    int diff = 0;
    
    if(prop != null){
      otherTime = ((FTime)prop).getTime();
      diff = compareTo(otherTime);
    }
    
    return diff;
  }
  
  public int compareTo(java.sql.Time otherTime) {
    int diff = 1;

    if(otherTime != null){
	    Calendar cal1 = Calendar.getInstance();
	    Calendar cal2 = Calendar.getInstance();
	    cal1.setTime(time);
	    cal2.setTime(otherTime);
	
	    diff = cal1.compareTo(cal2);
    }
    
    return diff;
  }

  public void backup() {
    backupTime = time;
  }
  
  public void restore() {
    time = backupTime != null ? backupTime : time;
  }
  
  public void setEmptyValue(){
  	setTime(getZeroTime_Copy());
  }
  
  public void setValueNull_AndResetIntrinsicValue(boolean notifyListeners) {
  	time = getZeroTime_Copy();
  	super.setValueNull_AndResetIntrinsicValue(notifyListeners);
  }
  
  //-------------------------------
  // VAADIN Property implementation
  //-------------------------------
  
  @Override
  public Object getValue() {
    return isEmpty() ? null : getTime();
  }

//  @Override
  public void setValue(Object newValue) {
		if(newValue == null){
			setTime(new java.sql.Time(0));
		}else{
			setTime(new java.sql.Time(((java.util.Date)newValue).getTime()));
		}
  }

  @Override
	public Object vaadin_TableDisplayObject(Format format, String captionProperty){
		return getString();
	}
  //-------------------------------
}
