/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;
import java.sql.Types;

import com.fab.model.table.FieldDefinition;
import com.foc.db.DBManager;
import com.foc.desc.FocObject;
import com.foc.gui.FGDateChooser;
import com.foc.gui.StaticComponent;
import com.foc.list.filter.DateTimeCondition;
import com.foc.list.filter.FilterCondition;
import com.foc.property.FDateTime;
import com.foc.property.FProperty;

/**
 * @author Tony
 */
public class FDateTimeField extends FDateField {
	private boolean timeRelevant = true;
	
  public FDateTimeField(String name, String title, int id, boolean key) {
    super(name, title, id, key);
  }

  public boolean isTimeRelevant(){
  	return timeRelevant;
  }
  
  @Override
  public boolean isDateRelevant() {
  	return true;
  }
  
  public void setTimeRelevant(boolean timeRelevant){
  	this.timeRelevant = timeRelevant;
  }

  public int getSqlType() {
    return Types.TIMESTAMP;
  }

  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_DATE_TIME;
  }

  public String getCreationString(String name) {
  	if(getProvider()== DBManager.PROVIDER_ORACLE){
  		return " \"" + name + "\" DATE";
  	}else if(getProvider()== DBManager.PROVIDER_POSTGRES) {
  		return " \"" + name + "\" TIMESTAMP";
  	}else{
  		return " " + name + " DATETIME";
  	}
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
  	FProperty prop = new FDateTime(masterObj, getID(), (java.sql.Date)defaultValue);
    if(isAllowNullProperties() && defaultValue == null) prop.setValueNull(true);
    return prop;
  }

  public FProperty newProperty(FocObject masterObj){
    FProperty prop = new FDateTime(masterObj, getID(), new java.sql.Date(0));
    if (isAllowNullProperties()) prop.setValueNull(true);
    return prop;    
  }

  public Component getGuiComponent(FProperty prop){
//  	FGDateField dateField = null;
//  	if(prop != null && ((FDateTime)prop).isTimeRelevant()){
//  		dateField = new FGDateTimeField();
//  		dateField.setColumns(Double.valueOf(17 * Globals.CHAR_SIZE_FACTOR).intValue());
//  	}else{
//      return super.getGuiComponent(prop);
//  	}
//    if(prop != null) dateField.setProperty(prop);
    FGDateChooser dateField = new FGDateChooser("dd/MM/yyyy hh:mm");
    if(getToolTipText() != null){
    	StaticComponent.setComponentToolTipText(dateField, getToolTipText());
    }
    if(prop != null){
      dateField.setProperty(prop);
    }
    return dateField;
  }
     
  public int getFieldDisplaySize() {
    return 17;
  }
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		DateTimeCondition condition = null;
		if(fieldPath != null && conditionPrefix != null){
			condition = new DateTimeCondition(fieldPath, conditionPrefix);
		}
		return condition;
	}
}
