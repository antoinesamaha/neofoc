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

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.foc.Globals;
import com.foc.business.calendar.FCalendar;
import com.foc.db.DBManager;
import com.foc.desc.FocObject;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FField;
import com.foc.util.Utils;
//import com.vaadin.data.util.converter.Converter;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FDate extends FProperty {
  protected java.sql.Date date;
  protected java.sql.Date backupDate = null;
  private static SimpleDateFormat dateFormat = null;
  private static SimpleDateFormat dateFormat_MonthOnly = null;
  private static SimpleDateFormat sqlDateFormat = null;
  private static SimpleDateFormat sortDateFormat = null;
  private static SimpleDateFormat sortDateMonthlyFormat = null;
  private static SimpleDateFormat sortDateMonthlyYearly = null;

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // Instance
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public FDate(FocObject focObj, int fieldID, java.sql.Date date) {
    super(focObj, fieldID);
    this.date = new java.sql.Date(getZeroReference());
    getDateFormat();
    if(date != null){
      setDate(date); 
    }
  }
  
  public static SimpleDateFormat getDateFormat_ForSortingYearly(){
  	if(sortDateMonthlyYearly == null){
  		sortDateMonthlyYearly = new SimpleDateFormat("yyyy-MM");
  	}
  	return sortDateMonthlyYearly;
  }

  public static SimpleDateFormat getDateFormat_ForSortingMonthly(){
  	if(sortDateMonthlyFormat == null){
  		sortDateMonthlyFormat = new SimpleDateFormat("yyyy-MM");
  	}
  	return sortDateMonthlyFormat;
  }
  
  public static SimpleDateFormat getDateFormat_ForSorting(){
    if (sortDateFormat == null) {
    	sortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }
    return sortDateFormat;
  }
  
  public static SimpleDateFormat getDateFormat(){
    if (dateFormat == null) {
      dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }
    return dateFormat;
  }
  
  public static SimpleDateFormat getDateFormat_MonthOnly(){
    if (dateFormat_MonthOnly == null) {
      dateFormat_MonthOnly = new SimpleDateFormat("MMM yyyy");
    }
    return dateFormat_MonthOnly;
  }
  
  public String convertDateToSQLString(java.util.Date date){
    if (sqlDateFormat == null) {
      if (getProvider() == DBManager.PROVIDER_ORACLE){
        sqlDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
      }else{
        sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      }
    }
    return date != null ? sqlDateFormat.format(date) : sqlDateFormat.format(new Date(getZeroReference()));
  }

  public static String convertDateToDisplayString(java.util.Date date){
    /*if (dateFormat == null) {
      dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }*/
    return date != null ? dateFormat.format(date) : dateFormat.format(new Date(0));
  }

  public static String convertDateToDisplayString_MonthRelevantOnly(java.util.Date date){
  	return date != null ? getDateFormat_MonthOnly().format(date) : getDateFormat_MonthOnly().format(new Date(0));
  }
  
  public static Date convertDisplayStringToDate(String dateStr) {
  	Date date = null;
		try{
			date = dateFormat.parse(dateStr);
		}catch (ParseException e){
			Globals.logException(e);
		}
    return date;
  }

  public boolean checkDate(){
    return !isEmpty();
  }
  
  public static boolean isEmpty(Date date){
    return isEmpty(date, 0);
  }
  
  public static boolean isEmpty(Date date, long zeroRef){
    return date == null || (date.getTime() < (zeroRef + Globals.DAY_TIME) && date.getTime() > (zeroRef - Globals.DAY_TIME));
  }
  
  public long getZeroReference(){ 
    FDateField fld = getDateField(); 
  	long zeroRef = fld != null ? fld.getZeroValueTime() : 0;
  	return zeroRef;
	}
  	
  public boolean isEmpty(){
    return FDate.isEmpty(date, getZeroReference());
  }
  
  public java.sql.Date getDate() {
    return (java.sql.Date) date.clone();
  }

  public void setDate(java.sql.Date date) {
  	setDate(date, false);
  }
  	
  public void setDate(java.sql.Date date, boolean userEditingEvent) {
    
  	boolean equalAtMidnight = 
  					(isEmpty(date, getZeroReference()) && isEmpty(this.date, getZeroReference()))
  			|| 	FCalendar.getTimeAtMidnight(this.date) == FCalendar.getTimeAtMidnight(date);
    
    if(date.compareTo(this.date) != 0 && !equalAtMidnight){
    	if(getFocField() == null || getFocField().isPropertyModificationAllowed(this, date, true)){
        this.date.setTime(date.getTime());
        setValueNull(false);
    		notifyListeners(userEditingEvent);
    	}
    }
  }

  public FDateField getDateField(){
  	FField fld = getFocField();
  	FDateField dFld = null;
  	if(fld != null && fld instanceof FDateField){
  		dFld = (FDateField) fld;
  	}
  	return dFld;
  }
  
  public String getString() {
    String str = "";
    if(checkDate()){
    	FDateField dFld = getDateField();
    	if(dFld != null && dFld.isMonthRelevantOnly()){
    		str = convertDateToDisplayString_MonthRelevantOnly(date);
    	}else{
    		str = convertDateToDisplayString(date);
    	}
    }
    return str;
  }

  public String getSqlString() {
  	if (isValueNull()) {
  		return "NULL";
  	} else {
	    if (getProvider() == DBManager.PROVIDER_ORACLE){
	      return "TO_DATE (" + "'" + convertDateToSQLString(date) + "'" + " , "+ "'DD-MON-YYYY')";
	    }else if (getProvider() == DBManager.PROVIDER_POSTGRES){
	    	return "'" + convertDateToSQLString(date) + "'";
	    }else if (getProvider() == DBManager.PROVIDER_MSSQL){
	    	return "CAST(N\'"+convertDateToSQLString(date)+"\' AS Date)";
	    }else if (getProvider() == DBManager.PROVIDER_H2){
	    	return "\'" + convertDateToSQLString(date) + "\'";
	    }else{
	      return "\"" + convertDateToSQLString(date) + "\"";
	    }
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
	      if (str != null && !str.equals("0000-00-00") && str != ""){
	      	int idx = str.indexOf(" ");
	      	String datteStr = null;
	      	
	      	if(idx > 0){
	      		datteStr = str.substring(0, idx).trim();
	      	}else{
	      		datteStr = str.substring(0, 10).trim();
	      	}
	      	
	      	int zerosToAdd = 4 - datteStr.indexOf("-");
	      	while(zerosToAdd > 0){
	      		datteStr = "0"+datteStr;
	      		zerosToAdd--;
	      	}
	      	
	        date = java.sql.Date.valueOf(datteStr);
	        setValueNull(false);
	      }
    	}
    } catch (Exception e) {
    	Globals.logString("!!!! Trying to parse date: "+str);
      Globals.logException(e);
    }
  }

  public void setString(String str) {
  	setString(str, false);
  }
  	
  public void setString(String str, boolean userEditingEvent) {
    try {
      if (str != null) {
        if(str.trim().compareTo("") == 0){
          setDate(new java.sql.Date(getZeroReference()), userEditingEvent);
        }else if(str.trim().equals(FField.NO_RIGHTS_STRING)){
          
        }else{
        	FDateField dFld = getDateField();
        	java.util.Date utilDate = null;
        	if(dFld != null && dFld.isMonthRelevantOnly()){
        		utilDate = dateFormat_MonthOnly.parse(str);
        	}else{
        		utilDate = dateFormat.parse(str);
        	}
          if (utilDate != null) setDate(new java.sql.Date(utilDate.getTime()), userEditingEvent);
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
    setObject(obj, true);
  }
  
  public Object getObject(){
    return getString();
  }

  public void setObject(Object obj){
  	setObject(obj, false);
  }
  	
  public void setObject(Object obj, boolean userEditingEvent){
    setString(obj.toString(), userEditingEvent);
  }
  
  public int compareTo(FProperty prop) {
    Date otherDate = null;
    int diff = 0;
    
    if(prop != null){
      otherDate = ((FDate)prop).getDate();      
      if(otherDate != null){
        int nbDays = (int) (date.getTime() / FCalendar.MILLISECONDS_IN_DAY);
        int otherNbDays = (int) (otherDate.getTime() / FCalendar.MILLISECONDS_IN_DAY);
        diff = nbDays - otherNbDays; 
      }
    }
    
    return diff;
  }
 
  public Long getTime(){
  	return date != null ? date.getTime() : getZeroReference();
  }
  
  public void backup() {
    if(backupDate == null){
      backupDate = new java.sql.Date(date.getTime());
    }else{
      backupDate.setTime(date.getTime());
    }
  }
  
  public void restore() {
    setDate(backupDate != null ? backupDate : date);
  }  
  
  public String getModificationLogString() {
  	String before = FDate.isEmpty(backupDate, getZeroReference()) ? "" : convertDateToDisplayString(backupDate);
  	String after = FDate.isEmpty(getDate(), getZeroReference()) ? "" : convertDateToDisplayString(getDate());
  	return before + " -> " + after;
  }
  
  public void setEmptyValue(){
  	setDate(new java.sql.Date(getZeroReference()));
  }
  
  //-------------------------------
  // VAADIN Property implementation
  //-------------------------------
  
  @Override
  public Object getValue() {
    return isEmpty() ? null : getDate();
  }

//  @Override
  public void setValue(Object newValue) {
		if(newValue == null){
			if (isAllowNullProperties()) {
				boolean notifyListeners = !isValueNull();
				setValueNull_AndResetIntrinsicValue(false);
				if(notifyListeners) notifyListeners(false);
			} else {
				setDate(new java.sql.Date(getZeroReference()));
			}
		}else{
			setDate(new java.sql.Date(((java.util.Date)newValue).getTime()));
		}
  }

  @Override
	public Object vaadin_TableDisplayObject(Format format, String captionProperty){
		return isEmpty() ? "" : getString();
	}
  //-------------------------------
  
  @Override
  public void copy(FProperty sourceProp){
  	super.copy(sourceProp);
  }
  
  public void setValueNull_AndResetIntrinsicValue(boolean notifyListeners) {
  	date = new java.sql.Date(getZeroReference());
  	super.setValueNull_AndResetIntrinsicValue(notifyListeners);
  }
}
