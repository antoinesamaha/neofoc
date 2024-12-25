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
import com.foc.desc.field.FObjectField;
import com.foc.gui.*;
import com.foc.gui.table.FAbstractTableModel;
import com.foc.gui.table.FTable;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.list.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FTableObjectCellEditor extends AbstractCellEditor implements TableCellEditor, FPropertyListener {

  private AbstractCellControler fatherCellControler = null; 
  private FPanel                selPanel            = null;
  private FObject               selectionProperty   = null;
  private JComboBox             dummy               = new JComboBox();
  //private JPopupMenu            popupMenu           = null;
  private FDialog               popupMenu           = null;
  
  public FTableObjectCellEditor(AbstractCellControler fatherCellControler) {
    this.fatherCellControler = fatherCellControler;
  }

  public void dispose(){
    fatherCellControler = null;
    if(selPanel != null){
      selPanel.dispose();
      selPanel = null;
    }
    selectionProperty = null;
    dummy = null;
    if(popupMenu != null){
    	popupMenu.setVisible(false);
    	popupMenu = null;
    }
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
    if(selectionProperty != null){
      obj = selectionProperty.getTableDisplayObject(null);
    }else{
      obj = "";
    }
    return obj;
    /*
     * FocObject obj = (FocObject) selectionProperty.getObject(); return obj;
     */
  }

  private void goBackIfNeeded() {
    if(popupMenu != null){
    	popupMenu.setVisible(false);
    	popupMenu = null;
    }else if (selPanel == Globals.getDisplayManager().getCurrentPanel()) {
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
  	if(popupMenu == null){//Only if we are with the dialogBox
	    if(selectionProperty != null){
	      selectionProperty.removeListener(this);
	      
	      FocList selectionList = selectionProperty.getPropertySourceList();
	      if(selectionList != null){
	        selectionList.setSelectionProperty(null);
	      }
	    }
	    goBackIfNeeded();
  	}
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
    	
    	FAbstractTableModel model = (FAbstractTableModel)table.getModel();
      int modelCol = model.getTableView().getVisibleColumnIndex(col);
      selectionProperty = (FObject) model.getFProperty(row, modelCol);
      FObjectField objectField = selectionProperty != null ? (FObjectField)selectionProperty.getFocField() : null;
    	if(objectField != null && (objectField.getEditorType() == FObjectField.BROWSE_POPUP_EDITOR || objectField.getEditorType() == FObjectField.SELECTION_PANEL_EDITOR)){
	      selectionProperty.addListener(this);
	      FocList selectionList = selectionProperty.getPropertySourceList();
	
	      selectionList.setSelectionProperty(selectionProperty);
	      selectionList.setListRequestingTheSelection(model.getFocList());
	      selPanel = selectionList.getSelectionPanel(false);
	
	      if(objectField.getEditorType() == FObjectField.BROWSE_POPUP_EDITOR){
	      	//This is for POPUP Menu not dialog
	      	/*
	      	popupMenu = new JPopupMenu();
	      	
	        popupMenu.add(selPanel);
	        popupMenu.setLightWeightPopupEnabled(true);
	        Point point = table.getMousePosition() != null ? new Point(table.getMousePosition()) : new Point(0, 0);
	        point.x = point.x - selPanel.getPreferredSize().width;
	        point.y = point.y + selPanel.getPreferredSize().height;
	        popupMenu.setLocation(point);
	        popupMenu.setVisible(true);
	        */
	      	
	      	//This is for POPUP dialog
	        Point point = table.getMousePosition() != null ? new Point(table.getMousePosition()) : new Point(0, 0);
//	        point.x = point.x - selPanel.getPreferredSize().width;
//	        point.y = point.y + selPanel.getPreferredSize().height;
	        
	        //popupMenu = Globals.getDisplayManager().popupDialog(selPanel, "select", true, point.x, point.y, true, true);
	        popupMenu = Globals.getDisplayManager().popupDialog(selPanel, "select", true, 0, 0, true, true);
	        
//	        popupMenu.setLocation(point);
//	        popupMenu.setVisible(true);	        
	      }else{
	        Globals.getDisplayManager().getCurrentPanel().setCurrentDefaultFocusComponent(table);
	        int x = 0;
	        int y = 0;
	        Rectangle rect = table.getCellRect(row, col, true);
	        
	        if(selPanel.getMainPanelSising() != FPanel.FILL_BOTH && selPanel.getMainPanelSising() != FPanel.FILL_HORIZONTAL){
	        	x = Math.max(rect.x - selPanel.getPreferredSize().width, 0);
	        }
	        if(selPanel.getMainPanelSising() != FPanel.FILL_BOTH && selPanel.getMainPanelSising() != FPanel.FILL_VERTICAL){
	          y = Math.max(rect.y - selPanel.getPreferredSize().height, 0);
	        }
	      	Globals.getDisplayManager().popupDialog(selPanel, "Select", true, x, y, true, true);	
	      }
    	}      
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
  
  public boolean isCellEditable(EventObject anEvent) {
    if(anEvent instanceof MouseEvent){
      return ((MouseEvent)anEvent).getClickCount() >= 1;
    }
    return true;
  }
}
