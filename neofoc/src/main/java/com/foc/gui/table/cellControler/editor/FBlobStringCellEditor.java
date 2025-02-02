/*
 * Created on 15 fevr. 2004
 */
package com.foc.gui.table.cellControler.editor;

import java.awt.Component;
import javax.swing.*;

import com.foc.gui.FGTextArea;
import com.foc.gui.table.FAbstractTableModel;
import com.foc.gui.table.FTable;
import com.foc.gui.table.FTableView;
import com.foc.property.FProperty;

import java.awt.event.*;
import java.util.EventObject;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FBlobStringCellEditor extends DefaultCellEditor implements FocusListener /*implements ActionListener, ItemListener*/{
	private FGTextArea textAreaPanel = null;

  public FBlobStringCellEditor(FGTextArea textAreaPanel) {
    super(new JTextField());
    this.textAreaPanel = textAreaPanel;
    //textAreaPanel.addFocusListener(this);
    super.setClickCountToStart(2);
  }

  public void dispose(){
  	textAreaPanel = null;
  }
  
  public FGTextArea getTextArea(){
  	return textAreaPanel;
  }
  
  public void setCheckBoxProperty(JTable jTable, int row, int column){    
    if(textAreaPanel != null && jTable != null){
      FTable table = (FTable)jTable;
      FAbstractTableModel model = (FAbstractTableModel) table.getModel();
      FTableView view = model.getTableView();
      FProperty prop = (FProperty) model.getFProperty(row, view.getVisibleColumnIndex(column));
      textAreaPanel.setProperty(prop);
    }
  }
    
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    if(!isSelected){
      stopCellEditing();
      return null;
    }
    //if(t.requestToEditCell()){
    //  comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);
    //}
    /*
    if(textAreaPanel != null && value instanceof String){ 
    	textAreaPanel.setText((String)value);
    }
    */
    setCheckBoxProperty(table, row, column);
    return textAreaPanel;
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
  
  @Override
  public Object getCellEditorValue(){
  	return textAreaPanel != null ? textAreaPanel.getText() : "";
  }

  /*
  public boolean stopCellEditing() {
    boolean b = super.stopCellEditing();
    textAreaPanel.setProperty(null);
    return b;
  }
  */

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		//textAreaPanel.updateObjectPropertyValue();
	}
}
