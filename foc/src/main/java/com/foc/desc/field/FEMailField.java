/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.fab.model.table.FieldDefinition;
import com.foc.desc.*;
import com.foc.gui.*;
import com.foc.gui.table.cellControler.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FEMailField extends FStringField {

	public static final int LEN_EMAIL = 254;
	
  public FEMailField(String name, String title, int id, boolean key) {
    super(name, title, id, key, LEN_EMAIL);
  }
  
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_EMAIL_FIELD;
  }

  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return new FEMailProperty(masterObj, getID(), (String) defaultValue);
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj){
    return newProperty(masterObj, "");
  }

  @Override
  public Component getGuiComponent(FProperty prop){
  	JComponent jTextComp = (JComponent) super.getGuiComponent(prop);
  	FGEMailComponent fgeMailComponent = new FGEMailComponent((FEMailProperty) prop, jTextComp);
    if(getToolTipText() != null){
    	StaticComponent.setComponentToolTipText(fgeMailComponent, getToolTipText());
    }
    return fgeMailComponent;
  }

  @Override
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
    JTextField guiComp = (JTextField) super.getGuiComponent(prop);
    return new TextCellControler(guiComp);
  }

  /*
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
    return new ColorCellControler();
  }
  */
}
