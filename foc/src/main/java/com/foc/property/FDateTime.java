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

import java.util.*;

import com.foc.*;
import com.foc.business.calendar.FCalendar;
import com.foc.db.DBManager;
import com.foc.desc.*;
import com.foc.desc.field.FDateTimeField;
import com.foc.desc.field.FField;

import java.text.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FDateTime extends FDate {

  private static SimpleDateFormat dateTimeFormat    = null;
  private static SimpleDateFormat sqlDateTimeFormat = null;
  private boolean timeRelevant = false;
  
  public FDateTime(FocObject focObj, int fieldID, java.sql.Date date) {
    super(focObj, fieldID, date);
    getDateTimeFormat();
    if (sqlDateTimeFormat == null) {
      if (getProvider() == DBManager.PROVIDER_ORACLE){
        sqlDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      }else{
        sqlDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      }
    }
    FDateTimeField field = (FDateTimeField) getFocField();
    if(field != null){
    	timeRelevant = field.isTimeRelevant();
    }
  }

  public boolean isTimeRelevant(){
  	return timeRelevant;
  }
  
  public void setTimeRelevant(boolean timeRelevant){
  	this.timeRelevant = timeRelevant;
  }

  public SimpleDateFormat getThisFormat(){
  	if(isTimeRelevant()){
  		return getDateTimeFormat();
  	}else{
  		return FDate.getDateFormat();
  	}
  }
  
  public static SimpleDateFormat getDateTimeFormat(){
    if(dateTimeFormat == null) dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
  	return dateTimeFormat;
  }

  public static String convertDateToSQLString_Static(java.util.Date date){
    return date != null ? sqlDateTimeFormat.format(date) : sqlDateTimeFormat.format(new Date(0));
  }

  public String convertDateToSQLString(java.util.Date date){
    return convertDateToSQLString_Static(date);
  }

  public static String convertDateToDisplayString(java.util.Date date){   
    return date != null ? dateTimeFormat.format(date) : dateTimeFormat.format(new Date(0));
  }
  
  public boolean checkDate(){
    return !isEmpty();
  }
  
  public void setDate(java.sql.Date date) {
    if(date.compareTo(this.date) != 0){
    	setValueNull(false);
      this.date.setTime(date.getTime());
      notifyListeners();
    }
  }

  public String getString() {
    String str = "";
    if(checkDate()){
    	Date d = date != null ? date : new Date(0);
    	str = getThisFormat().format(d);
    }
    return str;
  }

  public String getSqlString() {
  	if (isValueNull()) {
  		return "NULL";
  	} else {
  		return getSqlString_Static(getProvider(), date);
  	}
  }

  public static String getSqlString_Static(int provider, Date date) {
    if (provider == DBManager.PROVIDER_ORACLE){
      return "TO_DATE (" + "'" + convertDateToSQLString_Static(date) + "'" + " , "+ "'YYYY-MM-DD HH24:MI:SS')";
    }else if (provider == DBManager.PROVIDER_POSTGRES){
        return "'" + convertDateToSQLString_Static(date) + "'";      
    }else if (provider == DBManager.PROVIDER_MSSQL){
    	return "CAST(N\'"+convertDateToSQLString_Static(date)+".000\' AS DateTime)";
    }else if (provider == DBManager.PROVIDER_H2){
    	return "\'" + convertDateToSQLString_Static(date) + "\'";
    }else{
      return "\"" + convertDateToSQLString_Static(date) + "\"";
    }
  }
  
  public void setSqlStringInternal(String str) {
    try {
    	if (str == null) {
    		if (isAllowNullProperties()) {
    			setDate(new java.sql.Date(getZeroReference()), false);
    			setValueNull(true);
    		}
    	} else {    	
	      if (str != null && !str.equals("0000-00-00 00:00:00") && str != ""){
	        date = java.sql.Date.valueOf(str.substring(0, 10));
	        int hours = Integer.valueOf(str.substring(11, 13));
	        int mins  = Integer.valueOf(str.substring(14, 16));
	        int sec   = Integer.valueOf(str.substring(17, 19));
	        date.setTime(date.getTime()+((hours*60+mins)*60+sec)*1000);
	        if (isAllowNullProperties()) setValueNull(false);
	      }
    	}
    } catch (Exception e) {
      Globals.logException(e);
    }
  }

  public void setString(String str) {
    try {
      if (str != null) {
        if(str.trim().compareTo("") == 0){
          setDate(new java.sql.Date(0));
        }else if(str.trim().equals(FField.NO_RIGHTS_STRING)){
        	
        }else{
          java.util.Date utilDate = null;
          boolean tryWithoutTime = false;
          try{
          	utilDate = dateTimeFormat.parse(str);
          }catch(ParseException e){
          	tryWithoutTime = true;
          }
          tryWithoutTime = tryWithoutTime || FCalendar.isDateZero(new java.sql.Date(utilDate.getTime()));
          if(tryWithoutTime){
          	utilDate = FDate.getDateFormat().parse(str);
          }
          if (utilDate != null) setDate(new java.sql.Date(utilDate.getTime()));
        } 
      }
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
    
  public int compareTo(FProperty prop) {
    Date otherDate = null;
    int diff = 0;
    
    if(prop != null){
      otherDate = ((FDateTime)prop).getDate();      
      if(otherDate != null){
        diff = date.compareTo(otherDate);
      }
    }
    
    return diff;
  }
  
  public void setEmptyValue(){
  	setDate(new java.sql.Date(0));
  }
  
  @Override
  public void copy(FProperty sourceProp){
  	if(sourceProp instanceof FDateTime) {
  		long time = ((FDateTime) sourceProp).getTime();
  		setDate(new java.sql.Date(time));
  	}
  }
  
  @Override
  public String getModificationLogString() {
  	String before = FDate.isEmpty(backupDate, getZeroReference()) ? "" : convertDateToDisplayString(backupDate);
  	String after = FDate.isEmpty(getDate(), getZeroReference()) ? "" : convertDateToDisplayString(getDate());
  	return before + " -> " + after;
  }
}
