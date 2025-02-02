/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Color;
import java.awt.Component;

import com.foc.desc.*;
import com.foc.gui.*;
import com.foc.gui.table.cellControler.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FColorField extends FStringField {

	private Color nullColorDisplay = null;
	
  public FColorField(String name, String title, int id, boolean key, Color defaultColor) {
    super(name, title, id, key, 12);
    setNullColorDisplay(defaultColor);
  }

  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return new FColorProperty(masterObj, getID(), (Color)defaultValue);
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj){
    return newProperty(masterObj, null);
  }

  public Component getGuiComponent(FProperty prop){
    FGColorChooser colorField = new FGColorChooser((FColorProperty) prop);
    if(getToolTipText() != null){
    	StaticComponent.setComponentToolTipText(colorField, getToolTipText());
    }
    return colorField;
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
    return new ColorCellControler();
  }

	public Color getNullColorDisplay() {
		return nullColorDisplay;
	}

	public void setNullColorDisplay(Color defaultColor) {
		this.nullColorDisplay = defaultColor;
	}
}
