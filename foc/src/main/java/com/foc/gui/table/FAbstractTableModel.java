/*
 * Created on 16 fevr. 2004
 */
package com.foc.gui.table;

import java.awt.*;
import java.text.Format;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.*;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FIntField;
import com.foc.dragNDrop.FocTransferable;
import com.foc.event.FocEvent;
import com.foc.event.FocListener;
import com.foc.gui.table.view.ColumnsConfig;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.FocListListener;
import com.foc.list.filter.FocListFilter;
import com.foc.property.*;
 
/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public abstract class FAbstractTableModel extends AbstractTableModel implements Cloneable {

//public abstract int getRowCount();
	public abstract FocObject      getRowFocObject(int i);
	public abstract void           afterTableConstruction(FTable table);
	public abstract Color          getDefaultBackgroundColor(Color bg, Component comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);
	public abstract FocList        getFocList();
	public abstract FocListElement getRowListElement(int row);
	
  protected FTableView      tableView          = null;
  private   FIntField       lineNumberField    = null; 
  private   FInt            lineNumberProperty = null;
	private   FocListListener tableCellModificationListener = null;
  protected boolean         waitingForInvoke   = false;
  protected boolean         suspendGuiRefresh  = false;
  
  public FAbstractTableModel(){
		super();
		tableView = new FTableView();
	}

  public void dispose(){
    if(tableView != null){
      tableView.dispose();
      tableView = null;
    }
    if(lineNumberProperty != null){
      lineNumberProperty.dispose();
      lineNumberProperty = null;
    }
    if(lineNumberField != null){
      lineNumberField.dispose();
      lineNumberField = null;
    }
    disposeTableCellModificationListener();
  }
  
  public Object clone() throws CloneNotSupportedException {
    Object obj = super.clone();
    FAbstractTableModel abstractTableModel = (FAbstractTableModel)obj;
    FTableView tableView = abstractTableModel.getTableView();
    tableView = (FTableView)tableView.clone();
    abstractTableModel.setTableView(tableView);
    //abstractTableModel.tableCellModificationListener = (FocListListener)abstractTableModel.tableCellModificationListener.clone();
  
    return obj;
  }
  
	public boolean isSuspendGuiRefresh() {
		return suspendGuiRefresh;
	}

	public void setSuspendGuiRefresh(boolean suspendGuiRefresh) {
		boolean refreshNow = this.suspendGuiRefresh != suspendGuiRefresh && !suspendGuiRefresh;
		this.suspendGuiRefresh = suspendGuiRefresh;
		if(refreshNow){
			refreshGui();
		}
	}

  public void refreshGui() {
  	if(!isSuspendGuiRefresh()){
  		FTableView tableView = getTableView();
  		FTable     table     = tableView != null ? tableView.getTable() : null;
  		int        row       = table != null ? table.getSelectedRow() : -1;
  		int        column    = table != null ? table.getSelectedColumn() : -1;
  		fireTableDataChanged();
			if(row >= 0 && row < table.getRowCount()){
				table.setRowSelectionInterval(row, row);
			}
			if(column >= 0 && column < table.getColumnCount()){
				table.setColumnSelectionInterval(column, column);
			}
  	}
  }

	public void setRowHeight(int rowHeight) {
	}
  
  public void setTableView(FTableView tableView) {
    this.tableView = tableView;
  }
  
  public FTableView getTableView() {
    return tableView;
  }
  
  public FocListFilter getFilter(){
    FocListFilter listFilter = null;
    FTableView tableView = getTableView();
    if(tableView != null){
      listFilter = tableView.getFilter();
    }
    return listFilter;
  }
  
  protected FInt getLineNumberProperty(int row){
    if(lineNumberField == null){
      lineNumberField = new FIntField(FField.LINE_NUMBER_FIELD_LBL, "Line number", FField.LINE_NUMBER_FIELD_ID, false, 6);
    }
    if(lineNumberProperty == null){
      lineNumberProperty = new FInt(null, FField.LINE_NUMBER_FIELD_ID, -1);
      lineNumberProperty.setFocField(lineNumberField);
      lineNumberProperty.setBackground(Globals.getDisplayManager().getColumnTitleBackground());
    }
    lineNumberProperty.setInteger(row+1);
    return lineNumberProperty;
  }

  public FProperty getSpecialFProperty(FTableColumn tc, FocObject rowObject, int row, int col){
  	FProperty objectProperty = null;
    if (tc.getID() == FField.LINE_NUMBER_FIELD_ID){
      objectProperty = getLineNumberProperty(row);
    }else if (tc.getID() == FField.SELECTION_FIELD_ID) {
      FocListElement listElmt = getRowListElement(row);
      if (listElmt != null) objectProperty = listElmt.getSelectedProperty();
    }
    return objectProperty; 
  }
  
  public FProperty getFProperty(int row, int col) {
    FProperty objectProperty = null;

    if(tableView != null){
      FocObject rowObject = getRowFocObject(row);
      if(rowObject != null){
	    	//B-COL_REORDER-NOT      	
	      FTableColumn tc = (FTableColumn) tableView.getColumnAt(col);
	      //FTableColumn tc = (FTableColumn) tableView.getVisibleColumnAt(col);
	    	//E-COL_REORDER        
	      if(tc != null){
	      	objectProperty = getSpecialFProperty(tc, rowObject, row, col);
	      	if(objectProperty == null){
	          // In this case we should go through the field path
	          // Until we get to the property
	          FocObject  curObj    = rowObject;
	          FFieldPath fieldPath = tc.getFieldPath();
	          if(fieldPath != null){
	          	objectProperty = fieldPath.getPropertyFromObject(rowObject);
	          }
	      	}
	      }
	    }
    }
    return objectProperty;
  }
	  
  // AbstractTableModel
  // ------------------
  public int getColumnCount() {
    //System.out.println("get colomn count");
    return (tableView != null) ? tableView.getColumnCount() : 0;
  }

  public String getColumnName(int column) {
    String name = "";
    if (tableView != null) {
      FTableColumn tableColumn = tableView.getColumnAt(column);
      if (tableColumn != null) {
        name = tableColumn.getTitle();
      }
    }
    return name;
  }

  @SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
  	Object o = getValueAt(0, c);
    return o == null ? Object.class : o.getClass();
  }
	
  public boolean isCellEditable(int row, int col) {
    boolean editable = false;
    if (tableView != null) {
      FTableColumn tableColumn = tableView.getColumnAt(col);
      if (tableColumn != null) {
        editable = tableColumn.getEditable();
        if(editable){
          FProperty prop = getFProperty(row, col);
          if(prop != null){
            editable = !prop.isValueLocked();
            if(editable){
            	FocObject rowObject = getRowFocObject(row);
            	
  	          FFieldPath fieldPath = tableColumn.getFieldPath();
  	          if(fieldPath != null){
  	          	if(fieldPath.size() == 1){
  	          		editable = !rowObject.isPropertyLocked(fieldPath.get(0));
  	          	}
  	          }
  	          
  	        	if(prop.isInherited()){
  	        		editable = false;
  	        	}
            }
          }
        }
      }
    }
    return editable;
  }
  
  public Color getCellColor(int row, int col) {
    Color color = null;
    FProperty prop = getFProperty(row, col);    
    if(prop != null){    
      color = prop.getBackground();
    }
    
    if(color == null){
      FTableColumn  tableColumn = getTableView().getColumnAt(col);
      ColumnsConfig colConfig   = tableColumn != null ? tableColumn.getColumnConfig() : null;
      if(colConfig != null){
      	color = colConfig.getBackground();
      }
    }

    return color;
  }
	
  public Object getValueAt(int row, int col) {
    Object    obj       = null;
    FProperty prop      = getFProperty(row, col);
    FTableColumn tc = (FTableColumn) tableView.getColumnAt(col);
    if(prop != null){
      FocObject rowObject = prop.getFocObject();
      Format    format    = (rowObject != null && prop.getFocField() != null) ? rowObject.getFormatForFieldID(prop.getFocField().getID()) : null;
      if(format == null){//Here we are giving priority to the precision coming from the focObject but this is not logical.
      	format = tc.getFormat();
      }
    	obj = prop.getTableDisplayObject(format);

    	if(prop.isInherited()){
    		FProperty inheritedProp = null;
    		try{
    			inheritedProp = prop.getFocField().getInheritedPropertyGetter().getInheritedProperty(prop.getFocObject(), prop);
    		}catch(Exception e){
    			inheritedProp = prop.getFocField().getInheritedPropertyGetter().getInheritedProperty(prop.getFocObject(), prop);    			
    			Globals.logException(e);
    		}
	  		if(inheritedProp != null){
	  			obj = inheritedProp.getTableDisplayObject(format);
	  		}
    	}
    }
    return obj;
  }

  public void setValueAt(Object aValue, int row, int column) {
    FProperty prop = getFProperty(row, column);
    FTableColumn tc = (FTableColumn) tableView.getColumnAt(column);

    if (prop != null && tc != null) {
      prop.setTableDisplayObject(aValue, tc.getFormat());
    }
  }
  
  public void disposeTableCellModificationListener(){
    if(tableCellModificationListener != null){
      tableCellModificationListener.dispose();
      tableCellModificationListener = null;
    }
  }
  
  public void plugListListenerToCellPropertiesModifications(){
    FTableView view = getTableView();
    if (view != null) {
      FocListener focListener = new FocListener(){
        public void focActionPerformed(FocEvent evt) {
          //fireTableRowsUpdated(0, getRowCount());
        	//refreshGui();          
          if(!waitingForInvoke){
          	waitingForInvoke = true;
	          SwingUtilities.invokeLater(new Runnable(){
	            public void run() {
	            	waitingForInvoke = false;
	            	fireTableRowsUpdated(0, getRowCount());
	            	refreshGui();
	              if(tableView != null && tableView.getTable() != null){
	                if(tableView.getColumnResizingMode() == FTableView.COLUMN_AUTO_RESIZE_MODE){
	                	tableView.getTable().autoResizeColumns();
	                }
	              }
	            }
	          });
          }
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
  }
  
  public FocListListener getTableCellModificationListener(){
  	return tableCellModificationListener;
  }
  
  // --------------------------
  // Drag implementation
  // --------------------------
  public void fillSpecificDragInfo(FocTransferable focTransferable){
  	if(focTransferable != null){
	  	int selectedRow = focTransferable.getTableSourceRow();
	  	FocObject sourceFocObject = getRowFocObject(selectedRow);
	  	focTransferable.setSourceFocObject(sourceFocObject);
  	}
  }
}