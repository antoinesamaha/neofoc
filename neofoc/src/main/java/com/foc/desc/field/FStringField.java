/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;
import javax.swing.*;

import com.fab.model.table.FieldDefinition;
import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.*;
import com.foc.gui.*;
import com.foc.gui.table.cellControler.*;
import com.foc.list.filter.FilterCondition;
import com.foc.list.filter.StringCondition;
import com.foc.property.*;

import java.sql.Types;

/**
 * @author 01Barmaja
 */
public class FStringField extends FField {

  public static final int NAME_LEN = FEMailField.LEN_EMAIL;
  public static final int DESC_LEN = 50;
  
  private boolean capital    = false;
  private boolean multiline  = false;
  private int     columns    = 0;  
  private boolean compress   = false;
  
  public FStringField(String name, String title, int id, boolean key, int size) {
    super(name, title, id, key, size, 0);
  }

  public static int SqlType() {
    return Types.VARCHAR;
  }

  public int getSqlType() {
    return SqlType();
  }
  
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_CHAR_FIELD;
  }

  public boolean isClob() {
  	boolean clob = false;
  	if(			getProvider() == DBManager.PROVIDER_ORACLE) {
  		clob = getSize() > 4000;
  	} else if(getProvider() == DBManager.PROVIDER_MSSQL) {
  		clob = getSize() > 4000;
  	}
  	return clob;
  }
  
  public String getCreationString(String name) {
    if (getProvider()== DBManager.PROVIDER_ORACLE){
    	if(isClob()) {
    		return " \"" + name + "\" CLOB ";
    		//return " (\"" + name + "\" CLOB) LOB(\""+name+"\") STORE AS SECUREFILE ";
    	} else {
    		return " \"" + name + "\" VARCHAR2" + "(" + getSize() + ") ";
    	}
    }else if (getProvider()== DBManager.PROVIDER_POSTGRES){
      	if(isClob()) {
      		return " \"" + name + "\" TEXT ";
      	} else {
      		return " \"" + name + "\" VARCHAR" + "(" + getSize() + ") ";
      	}    	
    }else if (getProvider()== DBManager.PROVIDER_MSSQL){
    	if(isClob()) { 
    		return " [" + name + "] [ntext] ";
    	} else {
    		return " [" + name + "] [nvarchar]" + "(" + getSize() + ") ";
    	}
    }else if (getProvider()== DBManager.PROVIDER_H2){
    	return " \"" + name + "\" VARCHAR" + "(" + getSize() + ") DEFAULT ''";
    }else{
      return " " + name + " VARCHAR" + "(" + getSize() + ") CHARACTER SET utf8 COLLATE utf8_general_ci ";
    }
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    FProperty prop = new FString(masterObj, getID(), (String)defaultValue);
  	if(isAllowNullProperties() && defaultValue == null) {
  		prop.setValueNull(true);
  	}
  	return prop;
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj){
  	String defaultValue = isAllowNullProperties() ? null : "";
    return newProperty(masterObj, defaultValue);
  }

  public Component getGuiComponent(FProperty prop){
  	Component comp = null;
  	if(isMultiline()){
      FGTextArea textArea = newTextArea();
      if(prop != null) textArea.setProperty(prop);
      FGTextAreaPanel textAreaPanel = new FGTextAreaPanel(textArea, getTitle());    
      comp = textAreaPanel;
  	}else{
	    FGTextField textField = new FGTextField();
	    textField.setColumns(Double.valueOf((columns > 0 ? columns : this.getSize()) * Globals.CHAR_SIZE_FACTOR).intValue());
	    textField.setColumnsLimit(this.getSize());
	    textField.setCapital(capital);
	    if(getToolTipText() != null){
	    	StaticComponent.setComponentToolTipText(textField, getToolTipText());
	    }
	    if(prop != null) textField.setProperty(prop);
	    comp = textField;
  	}
    return comp;
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
  	if(isMultiline()){
      FGTextArea textAreaEditor   = newTextArea();
      FGTextArea textAreaRenderer = newTextArea();
      return new BlobStringCellControler(textAreaEditor, textAreaRenderer); 
  	}else{
      JTextField guiComp = (JTextField) getGuiComponent(prop);
      return new TextCellControler(guiComp);
  	}
  }

  private FGTextArea newTextArea(){
    FGTextArea textArea = new FGTextArea();
    int cols = columns;
    if(cols == 0) cols = getSize() / getDecimals();
    textArea.setColumns(cols);
    textArea.setRows(getDecimals());
    textArea.setColumnsLimit(getSize());
    textArea.setCapital(isCapital());
    return textArea;
  }
  
  public boolean isCapital() {
    return capital;
  }
  
  public void setCapital(boolean capital) {
    this.capital = capital;
  } 
  
  public int compareSQLDeclaration(FField field){
  	if(!this.isClob()) {
  		return this.getSize() - field.getSize();
  	} else {
  		return 0;
  	}
  }
  
  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc pointerDesc){
    
  }
  
	@Override
	protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		StringCondition condition = null;
		if(fieldPath != null && conditionPrefix != null){
			condition = new StringCondition(fieldPath, conditionPrefix);
		}
		return condition;
	}

	public boolean isMultiline() {
		return multiline;
	}

	public void setMultiline(boolean multiline) {
		this.multiline = multiline;
	}
	
	public void setMultiline(int nbrRows) {
		setMultiline(true);
		this.decimals = nbrRows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}
  
  /*private boolean displayEmptyValues = true;
  public boolean isDisplayEmptyValues() {
    return displayEmptyValues;
  }
  
  public void setDisplayEmptyValues(boolean displayEmptyValues) {
    this.displayEmptyValues = displayEmptyValues;
  }*/
}
