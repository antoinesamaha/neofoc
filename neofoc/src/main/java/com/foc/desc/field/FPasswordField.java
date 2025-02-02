/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;
import javax.swing.*;

import com.fab.model.table.FieldDefinition;
import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.gui.*;
import com.foc.gui.table.cellControler.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FPasswordField extends FStringField {

	public FPasswordField(String name, String title, int id) {
		this(name, title, id, false, 22);
	}
	
  public FPasswordField(String name, String title, int id, boolean key, int size) {
    super(name, title, id, key, size);
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FPassword(masterObj, getID(), (String) defaultValue);
  }
  
  public Component getGuiComponent(FProperty prop){
    FGPasswordField textField = new FGPasswordField();
    textField.setColumns(Double.valueOf(this.getSize()* Globals.CHAR_SIZE_FACTOR).intValue());
    textField.setColumnsLimit(this.getSize());
    textField.setCapital(isCapital());
    if(prop != null) textField.setProperty(prop);
    return textField;
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
    JTextField guiComp = (JTextField) getGuiComponent(prop);
    return new TextCellControler(guiComp);
  }
  
  @Override
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_PASSWORD;
  }
}
