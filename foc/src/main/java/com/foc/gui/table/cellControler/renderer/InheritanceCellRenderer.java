/*
 * Created on 24-Mar-2005
 */
package com.foc.gui.table.cellControler.renderer;

import java.awt.*;

import javax.swing.*;

import com.foc.desc.FocObject;
import com.foc.gui.*;
import com.foc.gui.table.FTable;

/**
 * @author 01Barmaja
 */
public class InheritanceCellRenderer extends FDefaultCellRenderer{
  
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3257004362944295737L;

  private FGCheckBox checkBox = null;
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if(checkBox == null){
      checkBox = new FGCheckBox();
    }
    //Globals
    FTable ft = (FTable) table;
    FocObject obj = ft.getTableModel().getRowFocObject(row);
    
    //Boolean boolValue = (value instanceof Boolean ) ? ((Boolean) value) : null; 
    Boolean boolValue = ((Boolean) value); 
    
    if( boolValue != null ){
      checkBox.setSelected(boolValue.booleanValue());    
    }
    setCellShape(checkBox, table, value, isSelected, hasFocus, row, column);
    //setToolTipTextAccordingToField(checkBox);    
    return checkBox;
  }
  
  public void dispose(){
    
  }
}
