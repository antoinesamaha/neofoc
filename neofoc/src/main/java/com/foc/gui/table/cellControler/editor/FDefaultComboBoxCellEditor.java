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
public class FDefaultComboBoxCellEditor extends DefaultCellEditor /*implements ActionListener, ItemListener*/{
  private FGAbstractComboBox comboBox        = null;
  private boolean            dontStopEditing = false;
  
	public FDefaultComboBoxCellEditor(FGAbstractComboBox comboBox) {
    super(comboBox);
    this.comboBox = comboBox;
    comboBox.setTableCellEditor(this);
    super.setClickCountToStart(DisplayManager.NBR_OF_CLICKS);
  }
  
  public void dispose(){
    comboBox = null;
  }
  
  public FGAbstractComboBox getComboBox(){
  	return comboBox;
  }

  public void setComboBoxProperty(JTable jTable, int row, int column){    
    if(comboBox != null && jTable != null){
      FTable table = (FTable)jTable;
      FAbstractTableModel model = (FAbstractTableModel) table.getModel();
      FTableView view = model.getTableView();
      FProperty  prop = (FProperty) model.getFProperty(row, view.getVisibleColumnIndex(column));
      comboBox.setProperty(prop);
    }
  }
  
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    Component comp = null;
    if(!isSelected){
      stopCellEditing();
      return null;
    }
    setComboBoxProperty(table, row, column);
    FTable t = (FTable)table;
    if (t.requestToEditCell()){
    	dontStopEditing = true;
      comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);
      dontStopEditing = false;
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
  	boolean b = false;
  	if(!dontStopEditing){
	    b = super.stopCellEditing();
	    comboBox.setProperty(null);
  	}
    return b;
  }

  public boolean isDontStopEditing() {
		return dontStopEditing;
	}

	public void setDontStopEditing(boolean dontStopEditing) {
		this.dontStopEditing = dontStopEditing;
	}
}
