/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;
import java.sql.Types;
import java.text.NumberFormat;

import javax.swing.JTextField;

import com.fab.model.table.FieldDefinition;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.FGNumField;
import com.foc.gui.StaticComponent;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.gui.table.cellControler.TextCellControler;
import com.foc.list.filter.FilterCondition;
import com.foc.property.FInt;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FIntField extends FField {
  private NumberFormat format = null;
  private boolean isGroupingUsed = true;
  private boolean displayZeroValues = true;
  
  public FIntField(String name, String title, int id, boolean key, int size) {
    this(name, title, id, key, size, true);
  }
  
  public FIntField(String name, String title, int id, boolean key, int size, boolean isGroupingUsed) {
    super(name, title, id, key, size, 0);
    this.isGroupingUsed = isGroupingUsed;
  }
  
  public static int SqlType() {
    return Types.INTEGER;
  }

  public int getSqlType() {
    return SqlType();
  }
  
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_INT;
  }

  public String getCreationString(String name) {
    if(getProvider()== DBManager.PROVIDER_ORACLE){
      return " \"" + name + "\" NUMBER("+ getSize()+")";
    }else if(getProvider()== DBManager.PROVIDER_MSSQL){
    	return " [" + name + "] INT" ;
    }else if(getProvider()== DBManager.PROVIDER_H2
    		  || getProvider()== DBManager.PROVIDER_POSTGRES){
    	return " \"" + name + "\" INT" ;
    }else{
      return " " + name + " INT" ;//+ "(" + getSize() + ")";
    }
  }

  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
  	FInt prop = new FInt(masterObj, getID(), (defaultValue != null) ? ((Integer)defaultValue).intValue() : 0);
  	if(isAllowNullProperties() && defaultValue == null) {
  		prop.setValueNull(true);
  	}
  	return prop;
  }

  public FProperty newProperty_ToImplement(FocObject masterObj){
    FProperty prop = new FInt(masterObj, getID(), 0);
    if (isAllowNullProperties()) prop.setValueNull(true);
    return prop;
  }

  /*
  public FField duplicate() {
    return new FIntField(getName(), getTitle(), getID(), false, getSize(), 0);
  }  
  */
  
  public int getFieldDisplaySize(){ 
    return 1 + getSize() + getSize() / 3;
  }
  
  public boolean isGroupingUsed(){
  	return this.isGroupingUsed;
  }
  
  public void setGroupingUsed(boolean groupingUsed){
  	this.isGroupingUsed = groupingUsed;
  }

  public NumberFormat getFormat(){
    if(format == null){
      format = FGNumField.newNumberFormat(this.getSize(), this.getDecimals(), this.isGroupingUsed);
    }
    return format;
  }
  
  public Component getGuiComponent(FProperty prop){
    NumberFormat format = getFormat();
    FGNumField numField = new FGNumField(format, getFieldDisplaySize());
    if(getToolTipText() != null){
    	StaticComponent.setComponentToolTipText(numField, getToolTipText());
    }
    if(prop != null) numField.setProperty(prop);
    return numField;
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
    JTextField guiComp = (JTextField) getGuiComponent(prop);
    TextCellControler textCellControler = new TextCellControler(guiComp);
    textCellControler.setFormat(getFormat());
    return textCellControler;
  }
  
  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc sourceDesc){
    
  }
  
  public boolean isDisplayZeroValues() {
    return displayZeroValues;
  }
  
  public void setDisplayZeroValues(boolean displayZeroValues) {
    this.displayZeroValues = displayZeroValues;
  }
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
  	return null;
  }

	public Class vaadin_getClass(){
		return Integer.class;
	}
}
