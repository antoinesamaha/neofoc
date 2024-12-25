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
public class InheritanceCellEditor extends DefaultCellEditor /*implements ActionListener, ItemListener*/{
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3257009869092434481L;
  private FGCheckBox checkBox = null;

  public InheritanceCellEditor(FGCheckBox checkBox) {
    super(checkBox);
    this.checkBox = new FGCheckBox();
    super.setClickCountToStart(2);
  }

  public void dispose(){
    checkBox = null;
  }
  
  public void setCheckBoxProperty(JTable jTable, int row, int column){    
    if(checkBox != null && jTable != null){
      FTable table = (FTable)jTable;
      FAbstractTableModel model = (FAbstractTableModel) table.getModel();
      FTableView view = model.getTableView();
      FProperty prop = (FProperty) model.getFProperty(row, view.getVisibleColumnIndex(column));
      checkBox.setProperty(prop);
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
    if(t.requestToEditCell()){
      comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
    return comp;
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
    checkBox.setProperty(null);
    return b;
  }
}
