/*
 * Created on 16 fevr. 2004
 */
package com.foc.gui.table;

import java.awt.*;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.event.*;
import com.foc.gui.FListPanel;
import com.foc.list.*;
import com.foc.list.filter.FocListFilter;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FTableModel extends FAbstractTableModel implements FocListener {
  /**
   * Comment for <code>serialVersionUID</code>
   */
  //private static final Color COLOR_FOR_LINE_GROUPING_BY_3 = new Color(235, 235, 235);
  private static final Color COLOR_FOR_LINE_GROUPING_BY_3 = new Color(210, 210, 255);
  
  private FocList         focList = null;
  public void setFocList(FocList focList) {
		this.focList = focList;
	}

	private FString         fictitiousStatusProperty = null;
  private FListPanel      listPanel = null;
  private FocListListener tableCellModificationListener = null;
  
  public FTableModel(FocList focList, FListPanel listPanel) {
  	super();
    this.focList = focList;
    this.listPanel = listPanel;
    this.focList.addFocListener(this);    
  }
  
  public void dispose(){
  	super.dispose();
    disposeTableCellModificationListener();
    
    if(focList != null){
      focList.removeFocListener(this);
      focList = null;
    }
            
    fictitiousStatusProperty = null;
  }
    
  /*public FocListFilter getFilter(){
    FocListFilter listFilter = null;
    FTableView tableView = getTableView();
    if(tableView != null){
      listFilter = tableView.getFilter();
    }
    return listFilter;
  }*/

  private FString getFictitiousStatusProperty(int row){
    if(fictitiousStatusProperty == null){
      fictitiousStatusProperty = new FString(null, FField.STATUS_FIELD_ID, "");    
    }
    FocObject rowObject = getRowFocObject(row);
    if(rowObject != null){
      StringBuffer str = new StringBuffer();
      if(rowObject.isModified()){
        str.append("M");
      }
      if(rowObject.isCreated()){
        str.append("C");
      }
      fictitiousStatusProperty.setString(str.toString());
    }
    return fictitiousStatusProperty;
  }
  
  public FProperty getSpecialFProperty(FTableColumn tc, FocObject rowObject, int row, int col){
  	FProperty objectProperty = super.getSpecialFProperty(tc, rowObject, row, col);
  	if(objectProperty == null){
	    if (tc.getID() == FField.STATUS_FIELD_ID) {
	      objectProperty = getFictitiousStatusProperty(row);          
	    }
  	}
    return objectProperty;
  }

  public FocListElement getRowListElement(int row){
    FocListElement element = null;
    FocListFilter filter = getFilter();
    
    if(filter != null){
      element = filter.getListVisibleElementAt(row);
    }else{
      element = focList.getFocListElement(row);
    }
    
    return element;
  }
  
  public FocObject getRowFocObject(int i) {
    FocListElement element = getRowListElement(i);
    return element != null ? element.getFocObject() : null;
  }
  
  public int getRowCount() {
    //here
  	int count = 0;
    FocListFilter filter = getFilter();
    
    if(filter != null){
      count = filter.getListVisibleElementCount();
    }else if(focList != null){
      count = focList.size();
    }
    
    return count;
  }

  public int getColumnCount() {
    return super.getColumnCount();
  }

  public String getColumnName(int column) {
  	return super.getColumnName(column);
  }

	public Class getColumnClass(int c) {
  	return super.getColumnClass(c);
  }
  
  public boolean isCellEditable(int row, int col) {
  	return super.isCellEditable(row, col);
  }

  public Color getCellColor(int row, int col) {
  	return super.getCellColor(row, col);
  }
  
  public Object getValueAt(int row, int col) {
    return super.getValueAt(row, col);
  }

  public void setValueAt(Object aValue, int row, int column) {
  	super.setValueAt(aValue, row, column);
  	FTableColumn tc = (FTableColumn) tableView.getColumnAt(column);
    if (tc != null && tc.getID() == FField.SELECTION_FIELD_ID) {
      FProperty prop = getFProperty(row, column);
      if (prop != null) {
      	getFocList().setModified(true);
      }
    }
  }

  public Color getDefaultBackgroundColor(Color bg, Component comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
    //if(row % 6 >= 3)
  	if(row % 2 >= 1){
      bg = COLOR_FOR_LINE_GROUPING_BY_3;          
    }
    return bg;
  }
  
	@Override
	public void afterTableConstruction(final FTable table) {
    FocList focList = getFocList();
    
    plugListListenerToCellPropertiesModifications();
    table.requestFocus();
    
    //If the List is direct impact on database and     
    if(focList.isKeepNewLineFocusUntilValidation()){
      table.addFocusListener(table);
      table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
        public void valueChanged(ListSelectionEvent e) {
          table.reactToFocusChange(false);
        }
      });
    }
    
    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    table.setInputVerifier(FTable.getTableLockInputVerifier());
	}
	
  // ------------------
  /**
   * @return
   */
  public FocList getFocList() {
  	return focList;
  }

  public FListPanel getListPanel() {
    return listPanel;
  }
  
  // --------------------------
  // FocListener implementation
  // --------------------------
  public void focActionPerformed(FocEvent evt) {
    if (evt != null) {
      if (evt.getSourceType() == FocEvent.TYPE_LIST) {
        if (evt.getID() == FocEvent.ID_ITEM_ADD || evt.getID() == FocEvent.ID_ITEM_REMOVE || evt.getID() == FocEvent.ID_ITEM_MODIFY) {
          FocListFilter filter = getFilter();
          if(filter != null){
            filter.resetVisibleArray();
          }
          //BAS - USELESS-POPUP-FOR-MANDATORY-FIELDS 
          //When we are in directly editabe, direct impact on database we have a useless popup for mandatory fields.
          //This is due to the ITEM-ADD event that launches a refresh table data and swing lauches a clear selection\
          //Causing the listeners to react to a change line selection and generate a message since the line is still empty
          boolean isDisactivatedBackup = false;
          if(listPanel != null && listPanel.getTable() != null){
            isDisactivatedBackup = listPanel.getTable().isDisableSelectionListeners();
            listPanel.getTable().setDisableSelectionListeners(true);
          }
          //EAS
          this.fireTableDataChanged();
          //BAS - USELESS-POPUP-FOR-MANDATORY-FIELDS
          if(listPanel != null && listPanel.getTable() != null){
            listPanel.getTable().setShouldRecomputePreferredScrollableViewPortSize(true);
            listPanel.getTable().setDisableSelectionListeners(isDisactivatedBackup);
          }
          //EAS
        }
      }
    }
  }

  /*private void disposeTableCellModificationListener(){
    if(tableCellModificationListener != null){
      tableCellModificationListener.dispose();
      tableCellModificationListener = null;
    }
  }*/
  
  public void resetListListenerToCellPropertiesMoifications(){
    disposeTableCellModificationListener();
    plugListListenerToCellPropertiesModifications();
  }
  
  /*public void plugListListenerToCellPropertiesMoifications(){
    FTableView view = getTableView();
    if (view != null) {
      FocListener focListener = new FocListener(){
        public void focActionPerformed(FocEvent evt) {
          fireTableRowsUpdated(0, getRowCount());
        }

        public void dispose() {
          
        }
      };
      
      disposeTableCellModificationListener();
      tableCellModificationListener = new FocListListener(getFocList());
      tableCellModificationListener.addListener(focListener);
      
      for (int i = 0; i < view.getColumnCount(); i++) {
        FTableColumn fCol = view.getColumnAt(i);
        if (fCol != null) {
          tableCellModificationListener.addProperty(fCol.getFieldPath());
        }
      }
      
      tableCellModificationListener.startListening();
    }
  }*/
}
