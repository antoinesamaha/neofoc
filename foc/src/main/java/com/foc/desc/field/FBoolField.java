/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.*;

import com.fab.model.table.FieldDefinition;
import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.FocObject;
import com.foc.gui.*;
import com.foc.gui.table.cellControler.*;
import com.foc.list.filter.BooleanCondition;
import com.foc.list.filter.FilterCondition;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FBoolField extends FIntField {

	public static final int GUI_TYPE_CHECKBOX      = 0;
	public static final int GUI_TYPE_TOGGLE_BUTTON = 1;
	
	private int guiType = GUI_TYPE_CHECKBOX;
	
  public FBoolField(String name, String title, int id, boolean key) {
    super(name, title, id, key, 1);
  }
  
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_BOOLEAN;
  }
  
  public String getCreationString(String name) {
  	/*
    if(Globals.getDBManager().getProvider()== Globals.getDBManager().PROVIDER_ORACLE){
      return " " + name + " NUMBER" ;
    }else{
  	 */
  	String creation = null; 
  	if(getProvider() == Globals.getDBManager().PROVIDER_MSSQL){
  		creation = " [" + name + "] BIT";
  	}else if(   getProvider()== DBManager.PROVIDER_ORACLE
  			     || getProvider()== DBManager.PROVIDER_H2
  			     || getProvider()== DBManager.PROVIDER_POSTGRES){
  		creation = " \"" + name + "\" INT";
  	}else{
  		creation = " " + name + " INT"; 
  	}
  	
  	return creation;
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
  	FBoolean prop = new FBoolean(masterObj, getID(), (defaultValue != null) ? ((Boolean)defaultValue).booleanValue() : false);
  	if(isAllowNullProperties() && defaultValue == null) {
  		prop.setValueNull(true);
  	}
  	return prop;
  }

  public FProperty newProperty_ToImplement(FocObject masterObj){
    FProperty prop = newProperty(masterObj, null);
    if (isAllowNullProperties()) prop.setValueNull(true);
    return prop;
  }
  
/*
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
  */
  /*
  public FField duplicate() {
    return new FBoolField(getName(), getTitle(), getID(), false, 0);
  }
  */
  
  public Component getGuiComponent(FProperty prop){
  	Component comp = null;
  	if(getGuiType() == GUI_TYPE_CHECKBOX){
	    FGCheckBox box = new FGCheckBox();
	    box.setText(getTitle());
	    if(getToolTipText() != null){
	    	StaticComponent.setComponentToolTipText(box, getToolTipText());
	    }
	    if(prop != null) box.setProperty(prop);
	    comp = box;
  	}else{
	    FGToggleButton box = new FGToggleButton();
	    box.setText(getTitle());
	    if(getToolTipText() != null){
	    	StaticComponent.setComponentToolTipText(box, getToolTipText());
	    }
	    if(prop != null) box.setProperty(prop);
	    comp = box;
  	}
    return comp;
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
    /*
    JTextField guiComp = (JTextField) getGuiComponent(prop);
    return new TextCellEditor(guiComp);
    */
    CheckBoxCellControler checkBoxCellConstroler = new CheckBoxCellControler(); 
    return checkBoxCellConstroler;
  }  
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		BooleanCondition condition = null;
		if(fieldPath != null && conditionPrefix != null){
			condition = new BooleanCondition(fieldPath, conditionPrefix);
		}
		return condition;
	}

	public int getGuiType() {
		return guiType;
	}

	public void setGuiType(int guiType) {
		this.guiType = guiType;
	}
	
	public Class vaadin_getClass(){
		return Boolean.class;
	}
}
