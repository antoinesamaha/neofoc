/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;
import java.sql.Types;

import javax.swing.JTextField;

import com.fab.model.table.FieldDefinition;
import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.FocObject;
import com.foc.gui.FGTimeField;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.gui.table.cellControler.TextCellControler;
import com.foc.property.FProperty;
import com.foc.property.FTime;

/**
 * @author 01Barmaja
 */
public class FTimeField extends FDateField {

  public FTimeField(String name, String title, int id, boolean key) {
    super(name, title, id, key);
  }
  
  @Override
	public boolean isTimeRelevant() {
		return true;
	}

  @Override
  public boolean isDateRelevant(){
    return false;
  }

  @Override
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_TIME;
  }
  
  public int getSqlType() {
  	if(getProvider() == DBManager.PROVIDER_ORACLE){
  		return super.getSqlType();
  	}else{
  		return Types.TIME;
  	}
  }

  public String getCreationString(String name) {
  	if(getProvider() == DBManager.PROVIDER_ORACLE){
  		return super.getCreationString(name);
  	}else if(getProvider() == DBManager.PROVIDER_H2
  			|| getProvider() == DBManager.PROVIDER_POSTGRES){
  		return " \"" + name + "\" TIME";
  	}else{
  		return " " + name + " TIME";
  	}  	
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
  	FProperty prop = new FTime(masterObj, getID(), (java.sql.Time)defaultValue);
  	if(isAllowNullProperties() && defaultValue == null) prop.setValueNull(true);
  	return prop;
  }
  
  public FProperty newProperty(FocObject masterObj){
    return newProperty(masterObj, null);
  }
  
  /*
  public FField duplicate() {
    return new FTimeField(getName(), getTitle(), getID(), false, 0);
  }*/
  
  public Component getGuiComponent(FProperty prop){
  	return getGuiComponent_Static(prop);
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
    JTextField guiComp = (JTextField) getGuiComponent(prop);
    return new TextCellControler(guiComp);
  }

  public static Component getGuiComponent_Static(FProperty prop){
    FGTimeField timeField = new FGTimeField();
    timeField.setColumns(Double.valueOf(10 * Globals.CHAR_SIZE_FACTOR).intValue());
    if(prop != null) timeField.setProperty(prop);
    return timeField;
  }

  public static AbstractCellControler getTableCellEditor_ForTime(FProperty prop){
    JTextField guiComp = (JTextField) getGuiComponent_Static(prop);
    return new TextCellControler(guiComp);
  }
  
  /*
  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc pointerDesc){
    
  }
  */ 
}
