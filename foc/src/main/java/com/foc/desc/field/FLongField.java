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
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.gui.table.cellControler.TextCellControler;
import com.foc.list.filter.FilterCondition;
import com.foc.property.FLong;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FLongField extends FField {
  private NumberFormat format = null;
  
  public FLongField(String name, String title, int id, boolean key, int size) {
    super(name, title, id, key, size, 0);
  }

  @Override
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_LONG;
  }
  
  public static int SqlType() {
    return Types.BIGINT;
  }

  public int getSqlType() {
    return SqlType();
  }

  public String getCreationString(String name) {
  	if(getProvider() == DBManager.PROVIDER_ORACLE){
  		return " \"" + name + "\" NUMBER(38)";
//  		return " \"" + name + "\" LONG" ;//+ "(" + getSize() + ")";
  	}else if(getProvider() == DBManager.PROVIDER_POSTGRES){
  		return " \"" + name + "\" INT";
  	}else{
  		return " " + name + " LONG" ;//+ "(" + getSize() + ")";
  	}
  }

  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    FProperty prop = new FLong(masterObj, getID(), (defaultValue != null) ? ((Long)defaultValue).longValue() : 0);
  	if(isAllowNullProperties() && defaultValue == null) {
  		prop.setValueNull(true);
  	}
  	return prop;
  }

  public FProperty newProperty_ToImplement(FocObject masterObj){
    FProperty prop = new FLong(masterObj, getID(), 0);
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

  public NumberFormat getFormat(){
    if(format == null){
      format = FGNumField.newNumberFormat(this.getSize(), this.getDecimals());
    }
    return format;
  }
  
  public Component getGuiComponent(FProperty prop){
    NumberFormat format = getFormat();
    FGNumField numField = new FGNumField(format, getFieldDisplaySize());
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
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
  	return null;
  }
}
