/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;
import java.sql.Types;

import com.fab.model.table.FieldDefinition;
import com.foc.ConfigInfo;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.FGDateChooser;
import com.foc.gui.StaticComponent;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.gui.table.cellControler.DateCellControler;
import com.foc.list.filter.DateCondition;
import com.foc.list.filter.FilterCondition;
import com.foc.property.FDate;
import com.foc.property.FProperty;

/**
 * @author Tony
 */
public class FDateField extends FField {

	public static String RTL = "\u200F";
	public static String LTRE = "\u202A";
	public static String DF = "\u202C";
	public static String SEPARATOR = LTRE + " " + DF;

	private boolean monthLevelOnly = false;
	private long    zeroValueTime  = 0;   
	
  public FDateField(String name, String title, int id, boolean key) {
    super(name, title, id, key, 0, 0);
  }

  public int getSqlType() {
    return Types.DATE;
  }

  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_DATE;
  }
  
  public String getCreationString(String name) {
  	if(    getProvider() == DBManager.PROVIDER_ORACLE
  			|| getProvider() == DBManager.PROVIDER_H2
  			|| getProvider() == DBManager.PROVIDER_POSTGRES
  			){
  		return " \"" + name + "\" DATE";
  	}else{
  		return " " + name + " DATE";
  	}
  }
  
  public boolean isTimeRelevant(){
    return false;
  }

  public boolean isDateRelevant(){
    return true;
  }
  
  public boolean isMonthRelevantOnly(){
    return monthLevelOnly;
  }
  
  public void setMonthRelevantOnly(boolean monthLevelOnly){
  	this.monthLevelOnly = monthLevelOnly;
  }

  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    FProperty prop = new FDate(masterObj, getID(), (java.sql.Date)defaultValue);
    if(isAllowNullProperties() && defaultValue == null) prop.setValueNull(true);
    return prop;
  }

  public FProperty newProperty_ToImplement(FocObject masterObj){
    FProperty prop = new FDate(masterObj, getID(), new java.sql.Date(getZeroValueTime()));
    if (isAllowNullProperties()) prop.setValueNull(true);
    return prop;    
  }

  public Component getGuiComponent(FProperty prop){
    FGDateChooser dateField = new FGDateChooser();
    if(getToolTipText() != null){
    	StaticComponent.setComponentToolTipText(dateField, getToolTipText());
    }
    if(prop != null){
      dateField.setProperty(prop);
    }
    return dateField;
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
    return new DateCellControler();
  }  
   
  public int getFieldDisplaySize() {
    return 18;
  }
  
  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc pointerDesc){
    
  }
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		DateCondition condition = null;
		if(fieldPath != null && conditionPrefix != null){
			condition = new DateCondition(fieldPath, conditionPrefix);
		}
		return condition;
	}

	public long getZeroValueTime() {
		return zeroValueTime;
	}

	public void setZeroValueTime(long zeroValueTime) {
		this.zeroValueTime = zeroValueTime;
	}

  public String newDateFormat() {
  	String fmt = "";
		if(isMonthRelevantOnly()){
			if(ConfigInfo.isArabic()) {
				fmt = "MMM"+SEPARATOR+"yyyy";
			} else {
				fmt = "MMM yyyy";
			}
		}else{
			if(isDateRelevant()){
				if(ConfigInfo.isArabic()) {
					fmt = RTL+"dd"+SEPARATOR+"MMM"+SEPARATOR+"yyyy";
				} else {
					fmt = "dd MMM yyyy";
				}
			}
	  	if(isTimeRelevant()){
	  		if(ConfigInfo.isArabic()) {
	  			if(!fmt.isEmpty()){
	  				fmt = " " + fmt;
	  			}
		  		fmt = "HH:mm" + fmt;
	  		} else {
	  			if(!fmt.isEmpty()){
	  				fmt += " ";
	  			}
		  		fmt += "HH:mm";
	  		}
	  	}
		}
		return fmt;
  }

//  We keep the Vaadin class String to turn arround the format table bug that doesn't take into account our format
//  public Class vaadin_getClass(){
//    return java.util.Date.class;
//  }
}
