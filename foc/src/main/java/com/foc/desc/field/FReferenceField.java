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
import com.foc.desc.FocRef;
import com.foc.gui.FGNumField;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.gui.table.cellControler.TextCellControler;
import com.foc.list.filter.FilterCondition;
import com.foc.property.FProperty;
import com.foc.property.FReference;

/**
 * @author 01Barmaja
 */
public class FReferenceField extends FField {  
  private NumberFormat format  = null;
  public static final int LEN_REFERENCE = 11; 
  
  public FReferenceField(String name, String title) {
    super(name, title, FField.REF_FIELD_ID, false, LEN_REFERENCE, 0);
  }
  
  public void dispose(){
  	super.dispose();
  	format = null;
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

	public String getCreationString(int provider, String name) {
    String str = null;
    if(provider == DBManager.PROVIDER_MSSQL){
    	str = " [" + name + "] BIGINT" ;
    }else if(   provider == DBManager.PROVIDER_ORACLE
    		     || provider == DBManager.PROVIDER_POSTGRES
    		     || provider == DBManager.PROVIDER_H2){
    	str = " \"" + name + "\" INT" ;
    }else{
    	str = " " + name + " INT" ;
    }
		return str;
	}
	
	@Override
  public String getCreationString(String name) {
		return getCreationString(getProvider(), name);
  }

  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return new FReference(masterObj, getID(), (FocRef) defaultValue);
  }

  public FProperty newProperty_ToImplement(FocObject masterObj){
    return newProperty(masterObj, null);
  }
  
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
  
  @Override
	public Class vaadin_getClass(){
		return String.class;
	}
}
