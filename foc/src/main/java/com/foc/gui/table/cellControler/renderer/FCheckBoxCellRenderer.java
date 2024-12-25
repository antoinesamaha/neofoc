/*
 * Created on 24-Mar-2005
 */
package com.foc.gui.table.cellControler.renderer;

import java.awt.*;

import javax.swing.*;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.gui.*;
import com.foc.gui.table.FTable;

/**
 * @author 01Barmaja
 */
public class FCheckBoxCellRenderer extends FDefaultCellRenderer{
  
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
    FTable    ft  = (FTable) table;
    FocObject obj = ft.getTableModel().getRowFocObject(row);
    
    //Boolean boolValue = (value instanceof Boolean ) ? ((Boolean) value) : null; 
    Boolean boolValue = ((Boolean) value); 
    
    if( boolValue != null ){
      checkBox.setSelected(boolValue.booleanValue());    
    }
    setCellShape(checkBox, table, value, isSelected, hasFocus, row, column);
    if(Globals.getDisplayManager().getLookAndFeel() == DisplayManager.LOOK_AND_FEEL_NIMBUS){
    	//Other wize we get a white component. What we need is to have the color of the tree or the line...
    	checkBox.setOpaque(true);
    	checkBox.setHorizontalAlignment(SwingConstants.CENTER);
    }
    //setToolTipTextAccordingToField(checkBox);    
    return checkBox;
  }
  
  public void dispose(){
    
  }
}
