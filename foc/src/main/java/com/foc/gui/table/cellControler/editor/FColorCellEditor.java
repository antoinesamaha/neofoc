/*
 * Created on 15 fevr. 2004
 */
package com.foc.gui.table.cellControler.editor;

import java.awt.Component;
import javax.swing.*;

import com.foc.gui.*;
import com.foc.gui.table.*;
import com.foc.property.*;

import java.awt.event.*;
import java.util.EventObject;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FColorCellEditor extends DefaultCellEditor /*implements ActionListener, ItemListener*/{
  private FGColorChooser colorChooser = null;

  public FColorCellEditor() {
    super(new JTextField());
    this.colorChooser = new FGColorChooser();
    super.setClickCountToStart(DisplayManager.NBR_OF_CLICKS);
  }

  public void dispose(){
    colorChooser = null;
  }
  
  public void setCheckBoxProperty(JTable jTable, int row, int column){    
    if(colorChooser != null && jTable != null){
      FTable table = (FTable)jTable;
      FAbstractTableModel model = (FAbstractTableModel) table.getModel();
      FTableView view = model.getTableView();
      FProperty prop = (FProperty) model.getFProperty(row, view.getVisibleColumnIndex(column));
      colorChooser.setProperty((FColorProperty) prop);
    }
  }
  
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    Component comp = null;
    if(!isSelected){
      stopCellEditing();
      return null;
    }
    setCheckBoxProperty(table, row, column);
    FTable t = (FTable)table;
    //if(t.requestToEditCell()){
    //  comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);
    //}
    return colorChooser;
  }

  public boolean shouldSelectCell(EventObject anEvent) {
    boolean toti = false;
    if(anEvent.getClass() == KeyEvent.class){
      KeyEvent ke = (KeyEvent)anEvent;
      if(ke.getKeyCode() != KeyEvent.VK_INSERT && ke.getKeyCode() != KeyEvent.VK_DELETE){
        toti = super.shouldSelectCell(anEvent);
      }
    }
    return toti;
  }
  
  public boolean stopCellEditing() {
    boolean b = super.stopCellEditing();
    colorChooser.setProperty(null);
    return b;
  }
 
  @Override
  public Object getCellEditorValue(){
  	return colorChooser.getColor();
  }
}
