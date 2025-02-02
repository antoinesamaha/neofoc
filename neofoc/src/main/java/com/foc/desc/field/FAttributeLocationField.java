/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.*;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.gui.*;
import com.foc.gui.fieldPathChooser.*;
import com.foc.gui.table.cellControler.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FAttributeLocationField extends FStringField {
  //FIeldPAth to get to the Desc
  private FFieldPath descPropertyFieldPath = null;
  
  public FAttributeLocationField(String name, String title, int id, boolean key, FFieldPath descPropertyFieldPath) {
    super(name, title, id, key, 30);
    this.descPropertyFieldPath = descPropertyFieldPath;
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FAttributeLocationProperty(masterObj, getID(), (FFieldPath) defaultValue);
  }
  
  public FProperty newProperty(FocObject masterObj){
    return newProperty(masterObj, null);
  }
  
  public FFieldPath getDescPropertyFieldPath(){
    return descPropertyFieldPath;
  }
  
  // -----------------------
  // Gui components
  // -----------------------
  
  public Component getGuiComponent(FProperty prop){
    FPanel panel = new FPanel();
    Component textField = (Component) super.getGuiComponent(prop);
    textField.setEnabled(false);
    
    panel.add(textField, 0, 0);
    
    FGButton edit = new FGButton(Globals.getIcons().getEditIcon());
    StaticComponent.setComponentToolTipText(edit, "Edit formula");
    edit.addActionListener(new EditListener((FAttributeLocationProperty) prop));
    panel.add(edit, 1, 0);

    return panel;
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
  	//FPanel guiComp = (FPanel) getGuiComponent(prop);
    return new AttributeLocationCellControler();
  }
  
  @SuppressWarnings("serial")
	public class EditListener extends AbstractAction{
    FAttributeLocationProperty pathProp = null;    
    
    public EditListener(FAttributeLocationProperty prop){
      pathProp = prop;
    }
    
    public void actionPerformed(ActionEvent e){
      FieldPathChooser pathChooser = new FieldPathChooser(pathProp);
      FPanel panel = pathChooser.newSelectionPanel();
      Globals.getDisplayManager().changePanel(panel);
    }
  }
}
