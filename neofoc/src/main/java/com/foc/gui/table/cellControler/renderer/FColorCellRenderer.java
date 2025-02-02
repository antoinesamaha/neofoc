/*
 * Created on 06-MAI-2009
 */
package com.foc.gui.table.cellControler.renderer;

import java.awt.*;

import javax.swing.*;

import com.foc.Globals;
import com.foc.gui.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FColorCellRenderer extends FDefaultCellRenderer{
  
  private FGColorChooser checkBox = null;
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if(checkBox == null){
      checkBox = new FGColorChooser();
    }
    //Globals
    //FTable    ft  = (FTable) table;
    //FocObject obj = ft.getTableModel().getRowFocObject(row);
    
    //Boolean boolValue = (value instanceof Boolean ) ? ((Boolean) value) : null; 
    Color colorValue = ((Color) value); 
    
    checkBox.setColor(colorValue);    
    //setCellShape(checkBox, table, value, isSelected, hasFocus, row, column);
    //setToolTipTextAccordingToField(checkBox);
    if(Globals.getDisplayManager().getLookAndFeel() == DisplayManager.LOOK_AND_FEEL_NIMBUS){
    	//Otherwize we get a white uncolored component
    	checkBox.setOpaque(true);
    }
    return checkBox;
  }
  
  public void dispose(){
    
  }
}
