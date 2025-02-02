/*
 * Created on 15 fevr. 2004
 */
package com.foc.gui.table;

import java.awt.Color;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.table.*;

import com.foc.*;
import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.gui.table.cellControler.*;
import com.foc.gui.table.view.ColumnsConfig;

/**
 * @author 01Barmaja
 */
public class FTableColumn implements Cloneable /* extends TableColumn */{
  //we need one of these parameters
  //1-
  private FocDesc    focDesc   = null;
  private FFieldPath fieldPath = null;
  //2-
  private FField     field     = null;
  //End
  
  private int 									     id                    ;
  private String 								     title 						     = ""   ;
  private boolean 							     editable							 = false;
  private int 									     size 								 = 0;
  private int 									     minimumSize 					 = 0;
  private int 									     redererSupplementSize = 0;
  private TableColumn 					     tableColumn 					 = null ;//This is the instance of the real tableColumn only available after construct of the table
  private AbstractCellControler      cellEditor 					 = null ;
  private int 									     orderInView 					 = -1   ;
  private int 									     previousOrderInView   = -1   ;
  private boolean 							     visible 							 = true ;//Tells if the column is actually set to visible or not
  private boolean 							     show 								 = true ;//Tells if the view would like to hide the column
  private boolean 							     showConfigurable 		 = true ;//Tells if the view can decide weather to show or not the column
  private Color 								     headerBackColor 			 = null ;//The column header backColor
  private boolean 							     allowAutoResizing 		 = true ;
  private FTableColumnRepresentation columnRepresentation  = null ;
  private ColumnsConfig              columnConfig          = null ;
  //B-COL_REORDER
  private int                        indexInTableView      = -1;
  //E-COL_REORDER
  private String                     explanation           = null;
  
  public void init(FocDesc focDesc, FFieldPath fieldPath, FField field, int id, String title, int size, boolean editable) {
    this.focDesc   = focDesc  ;
    this.title     = title    ;
    this.id        = id       ;
    this.editable  = editable ;
    this.fieldPath = fieldPath;
    this.size      = size     ;
    this.field     = field    ;
    
    this.allowAutoResizing = true;
    cellEditor             = null;
    headerBackColor        = Globals.getDisplayManager() != null ? Globals.getDisplayManager().getColumnTitleBackground() : null;
    
    FField fField = getField();
    if(fField != null){
      this.setCellEditor(fField.getTableCellEditor(null));
      
      if(this.size == 0){
        this.size = fField.getFieldDisplaySize();
      }else{
        this.allowAutoResizing = false;
      }
      if(this.title == null){
      	this.title = fField.getTitle();
      }
    }
  }

  public Object clone(){
    Object obj = null;
    try {
      obj = super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return obj;
  }
  
  public void dispose(){
    focDesc = null;
    if(fieldPath != null){
      fieldPath.dispose();
      fieldPath = null;
    }
    field = null;
    title = null;
    
    tableColumn = null;//This is the instance of the real tableColumn only available after construct of the table
    
    if(cellEditor != null){
      cellEditor.dispose();
      cellEditor = null;
    }
  }
  
  public FTableColumn(FocDesc focDesc, FFieldPath fieldPath, FField field, int id, String title, int size, boolean editable) {
  	init(focDesc, fieldPath, field, id, title, size, editable);
  }
  
  public FTableColumn(FocDesc focDesc, FFieldPath fieldPath, int id, String title, int size, boolean editable) {
    init(focDesc, fieldPath, null, id, title, size, editable);
  }

  public FTableColumn(FocDesc focDesc, FFieldPath fieldPath, int id, String title, boolean editable) {
    init(focDesc, fieldPath, null, id, title, 0, editable);
  }
  
  public FTableColumn(FField field, int id, String title, int size, boolean editable) {
    init(null, null, field, id, title, size, editable);
  }

  public FTableColumn(FocDesc focDesc, int id, String title, int size, boolean editable) {
    init(focDesc, FFieldPath.newFieldPath(id), null, id, title, size, editable);
  }

  public FTableColumn(FocDesc focDesc, int id, int colId, String title, int size, boolean editable) {
    init(focDesc, FFieldPath.newFieldPath(id), null, colId, title, size, editable);
  }
  
  private FField getField(){
    FField retField = field;
    
    if(retField == null && focDesc!=null && fieldPath != null){
      retField = fieldPath.getFieldFromDesc(focDesc);
    }
    return retField;
  }
  
  public String getTitle() {
    return title;
  }

  public void setExplanation(String explanation) {
  	this.explanation = explanation;
  }
  
  public String getExplanation() {
  	String ret = explanation;
  	if(ret == null || ret.isEmpty()){
  		FField fld = getField();
  		ret = fld != null ? fld.getExplanation() : "";
  	}
  	return ret;
  }

  public int getID() {
    return id;
  }
  
  public void setId(int id){
    this.id = id;
  }

  public boolean getEditable() {
    return editable;
  }

  public void setEditable(boolean b) {
    editable = b;
  }

  public FFieldPath getFieldPath() {
    return fieldPath;
  }

  /**
   * @return Returns the preferredWidth.
   */
  public int getPreferredWidth() {  
    return (size + redererSupplementSize) * Globals.getDisplayManager().getCharWidth();
  }
  
  public void setPreferredWidth(int sizeInPixels){
    size = (sizeInPixels / Globals.getDisplayManager().getCharWidth()) - redererSupplementSize;
    if(size < minimumSize){
    	size = minimumSize;
    	sizeInPixels = getPreferredWidth();
    }
  	if( getTableColumn() != null ){
      getTableColumn().setPreferredWidth(sizeInPixels);  
    }
  }

  /**
   * @param size
   *        column size
   */
  public void setSize(int size) {
    this.size = size ;
    setAllowAutoResizing(false);
  }
  
  /**
   * @param title The title to set.
   */
  public void setTitle(String title) {
    this.title = title;
  }
  
  /**
   * @return Returns the tableColumn.
   */
  public TableColumn getTableColumn() {
    return tableColumn;
  }
  
  /**
   * @param tableColumn The tableColumn to set.
   */
  public void setTableColumn(TableColumn tableColumn) {
    this.tableColumn = tableColumn;
  }
  
  /**
   * @return Returns the cellEditor.
   */
  public AbstractCellControler getCellEditor() {
    return cellEditor;
  }
  
  /**
   * @param cellEditor The cellEditor to set.
   */
  public void setCellEditor(AbstractCellControler cellEditor) {
    this.cellEditor = cellEditor;
    redererSupplementSize = cellEditor != null ? cellEditor.getRendererSupplementSize() : 0; 
  }
  
  public int getSize() {
    return size;
  }
  
  public Format getFormat() {
 		return cellEditor != null ? cellEditor.getFormat() : null;
  }
  
  public void setFormat(Format format) {
    if(cellEditor != null) cellEditor.setFormat(format);
  }
  
  public boolean isShow() {
  	boolean ret = show;
  	if(getColumnRepresentation() != null){
  		ret = getColumnRepresentation().isShow();
  	}
    return ret;
  }
  
  public void setShow(boolean show) {
    this.show = show;
  }
  
	public boolean isShowConfigurable() {
		return showConfigurable;
	}

	public void setShowConfigurable(boolean showConfigurable) {
		this.showConfigurable = showConfigurable;
	}
	
  public boolean isVisible() {
    return visible;
  }
  
  public void setVisible(boolean visible) {
    this.visible = visible;
  }
  
  public void setHeaderBackColor(Color backColor){
  	this.headerBackColor = backColor;
  }
  
  public Color getHeaderBackColor(){
  	return this.headerBackColor;
  }
  
  public int getOrderInView() {
    return orderInView;
  }
  
  public void setOrderInView(int orderInView) {
    this.orderInView = orderInView;
  }

  public int getPreviousOrderInView() {
    return previousOrderInView;
  }
  
  public void setPreviousOrderInView(int orderInView) {
    this.previousOrderInView = orderInView;
  }
  
  public boolean isAllowAutoResizing() {
    return allowAutoResizing;
  }

  private void setAllowAutoResizing(boolean allowAutoResizing) {
    this.allowAutoResizing = allowAutoResizing;
  }

	public int getMinimumSize() {
		return minimumSize;
	}

	public void setMinimumSize(int minimumSize) {
		this.minimumSize = minimumSize;
	}

	public FTableColumnRepresentation getColumnRepresentation() {
		return columnRepresentation;
	}

	public void setColumnRepresentation(FTableColumnRepresentation columnRepresentation) {
		this.columnRepresentation = columnRepresentation;
	}
	
	public String getColumnTitleInViewConfiguration(FColumnGroupTableHeader tableHeader){
		String title = null;
    if(tableHeader == null){
    	title = getTitle();
    }else if(tableHeader != null){
    	title = "";
    	ArrayList<FColumnGroup> groupsList  = tableHeader.getGroupsForColumn(this);
    	for(int g=0; g<groupsList.size(); g++){
    		FColumnGroup group = groupsList.get(g);
    		title += group.getHeaderValue().toString();
    		title += " - ";
    	}
    	title += getTitle();
    }
    return title;
	}

	public ColumnsConfig getColumnConfig() {
		ColumnsConfig ret = columnConfig;
  	if(getColumnRepresentation() != null){
  		ret = getColumnRepresentation().getColumnConfig();
  	}
    return ret;
	}

	public void setColumnConfig(ColumnsConfig columnConfig) {
		this.columnConfig = columnConfig;
		adjustColumnNumberFormat(columnConfig);
	}

	public void adjustColumnNumberFormat(ColumnsConfig columnConfig){
  	if(columnConfig != null && getFormat() != null && getFormat() instanceof NumberFormat){
  		NumberFormat numFormat = (NumberFormat) getFormat();
    	if(columnConfig.getNumberOfDecimals() != -1){
    		numFormat.setMaximumFractionDigits(columnConfig.getNumberOfDecimals());
    		numFormat.setMinimumFractionDigits(columnConfig.getNumberOfDecimals());
    	}
    	numFormat.setGroupingUsed(columnConfig.isGroupingUsed());
  	}
	}
	
	//B-COL_REORDER
	public int getIndexInTableView() {
		return indexInTableView;
	}

	public void setIndexInTableView(int indexOrderInView) {
		this.indexInTableView = indexOrderInView;
	}
	//E-COL_REORDER
}
