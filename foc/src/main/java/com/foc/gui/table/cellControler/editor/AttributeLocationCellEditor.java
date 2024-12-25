/*
 * Created on 15 fevr. 2004
 */
package com.foc.gui.table.cellControler.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import com.foc.*;
import com.foc.gui.*;
import com.foc.gui.fieldPathChooser.FieldPathChooser;
import com.foc.gui.table.FTable;
import com.foc.gui.table.FTableModel;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.list.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class AttributeLocationCellEditor extends AbstractCellEditor implements TableCellEditor, FPropertyListener {

	private AbstractCellControler fatherCellControler = null;
	private FAttributeLocationProperty pathProp = null;
	private FPanel panel = null;
	private JComboBox dummy = new JComboBox();
		
  public AttributeLocationCellEditor(AbstractCellControler fatherCellControler) {
  	this.fatherCellControler = fatherCellControler;
  }

  public void dispose(){
  	panel = null;
  	pathProp = null;
  	dummy = null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable,
   *      java.lang.Object, boolean, int, int)
   */
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    Component comp = null;
    
    if(!isSelected){
      stopCellEditing();
      return null;
    }
    FTable t = (FTable)table;
    if(t.requestToEditCell()){
      comp = fatherCellControler != null ? fatherCellControler.getRenderer().getTableCellRendererComponent(table, value, isSelected, true, row, column) : dummy;
    }
    return comp;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.CellEditor#getCellEditorValue()
   */
  public Object getCellEditorValue() {
    Object obj = null; 
    if(pathProp != null){
      obj = pathProp.getTableDisplayObject(null);
    }else{
      obj = "";
    }
    return obj;
  }

  private void goBackIfNeeded() {
    if (panel == Globals.getDisplayManager().getCurrentPanel()) {
      Globals.getDisplayManager().goBack();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.CellEditor#cancelCellEditing()
   */
  public void cancelCellEditing() {
    goBackIfNeeded();    
    super.cancelCellEditing();    
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
   */
  public boolean shouldSelectCell(EventObject anEvent) {
    boolean ret = true;
    try {
      Object source = anEvent.getSource();

      if (anEvent.getClass() == MouseEvent.class) {
        Object sourceObj = anEvent.getSource();
        if (sourceObj.getClass() == FTable.class) {
          FTable table = (FTable) sourceObj;
          MouseEvent mouseEvent = (MouseEvent) anEvent;
          Point point = mouseEvent.getPoint();
          
          startCellEditing(table, table.rowAtPoint(point), table.columnAtPoint(point));
        }
      }
    } catch (Exception e) {
      Globals.logException(e);
    }

    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.CellEditor#stopCellEditing()
   */
  public boolean stopCellEditing() {
    goBackIfNeeded();
    return super.stopCellEditing();
  }

  /*
   * (non-Javadoc)
   * 
   * @see b01.foc.property.FPropertyListener#propertyModified(b01.foc.property.FProperty)
   */
  public void propertyModified(FProperty property) {
    stopCellEditing();
  }
  
  public void startCellEditing(FTable table, int row, int col) {
    try {
    	FTableModel model = (FTableModel) table.getModel();
      int modelCol = model.getTableView().getVisibleColumnIndex(col);
      pathProp = (FAttributeLocationProperty) model.getFProperty(row, modelCol);
      FieldPathChooser pathChooser = new FieldPathChooser(pathProp);
      panel = pathChooser.newSelectionPanel();
      Globals.getDisplayManager().popupDialog(panel, "Attribute path", true);
      stopCellEditing();
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
  
  public boolean isCellEditable(EventObject anEvent) {
    if (anEvent instanceof MouseEvent) { 
      return ((MouseEvent)anEvent).getClickCount() >= 2;
    }
    return true;
  }
}
