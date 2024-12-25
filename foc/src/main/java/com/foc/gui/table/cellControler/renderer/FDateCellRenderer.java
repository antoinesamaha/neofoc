/*
 * Created on 24-Mar-2005
 */
package com.foc.gui.table.cellControler.renderer;

import java.awt.*;

import javax.swing.*;

import com.foc.gui.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FDateCellRenderer extends FDefaultCellRenderer{
  
	private FGDateField date = null;
	
  public void dispose(){
  	if(date != null){
	  	date.dispose();
	  	date = null;
  	}
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if(date == null) {
    	date = new FGDateField();
    	date.setBorder(null);
    }
    String strVal = (String) value;
//    if(strVal.isEmpty()){
//    	strVal = "01/01/1970";
//    }
    date.setText(strVal);
    setCellShape(date, table, value, isSelected, hasFocus, row, column);
    /*
    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if(comp != null){
      FTableModel model = (FTableModel) table.getModel();
      if(!model.isCellEditable(row, column)){
        comp.setEnabled(false);
      }else{
        comp.setEnabled(true);      
      }
    }
    return comp;
    */
    return date;
  }
}
