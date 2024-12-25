/*
 * Created on 24-Mar-2005
 */
package com.foc.gui.table.cellControler.renderer;

import java.awt.*;

import javax.swing.*;

import com.foc.gui.FGTextArea;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FBlobStringCellRenderer extends FDefaultCellRenderer{
  
  private FGTextArea textAreaPanel = null;
  
  public FBlobStringCellRenderer(FGTextArea textAreaPanel){
  	this.textAreaPanel = textAreaPanel;
  }
  
  public void dispose(){
  	textAreaPanel = null;
  }
  
  public FGTextArea getTextArea(){
  	return textAreaPanel;
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if(textAreaPanel != null && value instanceof String){
    	textAreaPanel.setText((String)value);
    }
    setCellShape(textAreaPanel, table, value, isSelected, hasFocus, row, column);
  	return textAreaPanel;
  }
}
