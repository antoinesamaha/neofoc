/*
 * Created on 15 fevr. 2004
 */
package com.foc.gui.table;

import java.util.*;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import com.foc.*;
import com.foc.admin.FocGroup;
import com.foc.admin.GrpViewRights;
import com.foc.admin.GrpViewRightsDesc;
import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.gui.table.cellControler.DrawingCellControler;
import com.foc.gui.table.cellControler.GanttChartActivityCellControler;
import com.foc.gui.table.cellControler.GanttChartCellControler;
import com.foc.gui.table.cellControler.TreeCellControler;
import com.foc.gui.table.cellControler.renderer.drawingCellRenderer.FDrawingScale;
import com.foc.gui.table.cellControler.renderer.gantChartActivityRenderer.FGanttChartActivityColumnHeaderRenderer;
import com.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;
import com.foc.gui.table.view.ColumnsConfig;
import com.foc.gui.table.view.UserView;
import com.foc.gui.table.view.UserViewDesc;
import com.foc.gui.table.view.ViewConfig;
import com.foc.gui.table.view.ViewFocCache;
import com.foc.gui.table.view.ViewFocList;
import com.foc.gui.table.view.ViewSelectionPanel;
import com.foc.gui.treeTable.FGTreeInTable;
import com.foc.list.FocList;
import com.foc.list.filter.FocListFilter;
import com.foc.property.FObject;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

/**
 * @author 01Barmaja
 */
public class FTableView implements Cloneable {
  private ArrayList<Integer>                    visibleColumnsIndexes    = null;
  private ArrayList<FTableColumn>               columns                  = null;
  private ArrayList<FTableColumnRepresentation> columnRepresentation     = null;
  private FTable                                table                    = null;
  private double                                columnWidthFactor        = 1;
  private int                                   lineNumber               = 10;
  private FocListFilter                         filter                   = null;
  private int                                   detailPanelViewID        = FocObject.DEFAULT_VIEW_ID;
  private int                                   detailPanelViewIDForNewItem = -1;
  private boolean                               editAfterInsertion       = false;
  private String                                viewKey                  = null;
  private String                                viewContext              = null;
  private UserView                              userView                 = null;
  private GrpViewRights                         viewRights               = null;
  private ViewFocList 													listOfViews              = null;
  private boolean                               popupLoadingMessage      = false;
  
  private int columnResizingMode = COLUMN_WIDTH_FACTOR_MODE;
  public static final int COLUMN_WIDTH_FACTOR_MODE = 1;
  public static final int COLUMN_AUTO_RESIZE_MODE  = 2;
  private static final String GANTT_COLUMN_TITLE   = "GANTT";
  private static final String DRAWING_COLUMN_TITLE = "DRAWING";
  
  private boolean owner = true;
  private int columnsToFreeze = 0;
  private FColumnGroupHeaderConstructor columnGroupHeaderConstructor = null;
  
  private boolean disableAutoAdjustViewportPosition = false;
  
  public FTableView() {
    columns              = new ArrayList<FTableColumn>();
    columnRepresentation = new ArrayList<FTableColumnRepresentation>();
    if(ConfigInfo.isShowStatusColumn()){
      addStatusColumn();
    }
    columnGroupHeaderConstructor = new FColumnGroupHeaderConstructor(this);
  }
  
  public void dispose(){
    if(columns != null){
      for(int i = 0; i < getColumnCount(); i++){
        FTableColumn tableCol = getColumnAt(i);
        if(tableCol != null){
          tableCol.dispose();
        }
      }
      columns.clear();
      columns = null;
      
      columnRepresentation.clear();
      columnRepresentation = null;
    }
    
    if(visibleColumnsIndexes != null){
      visibleColumnsIndexes.clear();
      visibleColumnsIndexes = null;
    }
    
    if(table != null){
      table.dispose();
      table = null;
    }
    
    if(filter != null && owner){
      filter.dispose();
      filter = null;
    }
    
    if(columnGroupHeaderConstructor != null){
      columnGroupHeaderConstructor.dispose();
      columnGroupHeaderConstructor = null;
    }
    
    if(listOfViews != null){
    	//Because this is cashed in the ViewFocListMap object
    	//listOfViews.dispose();
    	listOfViews = null;
    }
    
    if(userView != null){
    	//Because this is cashed in the ViewFocListMap object
      //userView.dispose();
      userView = null;
    }
  }
  
  private void setColumns(ArrayList<FTableColumn> columns){
    this.columns = columns;
  }

  private void setColumnRepresentation(ArrayList<FTableColumnRepresentation> columns){
    this.columnRepresentation = columns;
  }

  public Object clone(){
    Object obj = null;
    try {
      obj = super.clone();
      FTableView ftableView = (FTableView)obj;
      
      ArrayList<FTableColumn> clonedColumns = new ArrayList<FTableColumn>();
      for( int i = 0; i < ftableView.getColumnCount(); i++ ){
        clonedColumns.add((FTableColumn)ftableView.getColumnAt(i).clone());
      }
      ftableView.setColumns(clonedColumns);

      ArrayList<FTableColumnRepresentation> clonedColumnsRepresentation = new ArrayList<FTableColumnRepresentation>();
      for( int i = 0; i < ftableView.getColumnRepresentationCount(); i++ ){
      	clonedColumnsRepresentation.add((FTableColumnRepresentation)ftableView.getColumnRepresentationAt(i).clone());
      }
      ftableView.setColumnRepresentation(clonedColumnsRepresentation);
      
      ftableView.columnGroupHeaderConstructor = (FColumnGroupHeaderConstructor)ftableView.columnGroupHeaderConstructor.clone();
      ftableView.columnGroupHeaderConstructor.setTableView(ftableView);
      ftableView.columnGroupHeaderConstructor.cloneColumnGroupFatherArray();  
      
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return obj;
  }

  public int getDetailPanelViewID(){
  	return detailPanelViewID;
  }

  public void setDetailPanelViewID(int detailPanelViewID){
  	this.detailPanelViewID = detailPanelViewID;
  }
  
  public FTableColumn addColumn(FTableColumn tableCol) {
  	if(getColumnById(tableCol.getID()) != null){
  		FTableColumn prevCol = getColumnById(tableCol.getID());
  		Globals.getDisplayManager().popupMessage("You have 2 columns with same ID : "+prevCol.getTitle()+" and "+tableCol.getTitle());
  	}
    columns.add(tableCol);
    return tableCol;
  }

  public FTableColumn insertColumnBefore(FTableColumn tableCol, int index) {
  	addColumn(tableCol);
  	for(int i=columns.size()-1; i>index; i--){
  		columns.set(i, columns.get(i-1));
  	}
    columns.set(index, tableCol);
    return tableCol;
  }

  public FTableColumn addColumn(FocDesc focDesc, int id, FFieldPath fieldPath, String title, int size, boolean editable) {
    FTableColumn column = new FTableColumn(focDesc, fieldPath, id, title, size, editable);
    addColumn(column);
    return column;
  }

  public FTableColumn addColumn(FocDesc focDesc, int id, FFieldPath fieldPath, boolean editable) {
    FTableColumn column = new FTableColumn(focDesc, fieldPath, id, null, 0, editable);
    addColumn(column);
    return column;
  }

  public FTableColumn addColumn(FocDesc focDesc, int id, FField focField) {
    FTableColumn tableCol = addColumn(focDesc, id, FFieldPath.newFieldPath(focField.getID()), focField.getTitle(), focField.getSize(), false);
    return tableCol;
  }

  public FTableColumn addColumn(FocDesc focDesc, FField focField) {
    FTableColumn tableCol = addColumn(focDesc, focField.getID(), FFieldPath.newFieldPath(focField.getID()), focField.getTitle(), focField.getSize(), false);
    return tableCol;
  }
  
  public FTableColumn addColumn(FField field,int id, String title, int size, boolean editable) {
    FTableColumn column = new FTableColumn(field, id, title, size, editable);
    addColumn(column);
    return column;
  }
  
  public FTableColumn addColumn(FocDesc focDesc, int id, String title, int size, boolean editable) {
  	FTableColumn column = new FTableColumn(focDesc, id, title, size, editable);
    addColumn(column);
    return column;
  }  	

  public FTableColumn addColumn(FocDesc focDesc, int id, int size, boolean editable) {
  	FTableColumn column = new FTableColumn(focDesc, id, null, size, editable);
    addColumn(column);
    return column;
  }
  
  public FTableColumn addColumn(FocDesc focDesc, int id, String title, boolean editable) {
  	FTableColumn column = new FTableColumn(focDesc, id, title, 0, editable);
    addColumn(column);
    return column;
  }  	

  public FTableColumn addColumn(FocDesc focDesc, int id, int colId, String title, boolean editable) {
  	FTableColumn column = new FTableColumn(focDesc, id, colId, title, 0, editable);
    addColumn(column);
    return column;
  }  	

  public FTableColumn addColumn(FocDesc focDesc, int id, boolean editable) {
  	FTableColumn column = new FTableColumn(focDesc, id, null, 0, editable);
    addColumn(column);
    return column;
  }  	

  public FTableColumn addColumn(FocDesc focDesc, FFieldPath fieldPath, int id, boolean editable) {
  	FTableColumn column = new FTableColumn(focDesc, fieldPath, id, null, 0, editable);
    addColumn(column);
    return column;
  }  	

  public FTableColumn addColumnRepresentation(FTableColumnRepresentation tableColRep) {
    columnRepresentation.add(tableColRep);
    return tableColRep;
  }
  
  public FTableColumn addSelectionColumn(){
    return addColumn(null/*theFocDesc*/, FField.SELECTION_FIELD_ID, FFieldPath.newFieldPath(FField.SELECTION_FIELD_ID), "", 3, true);
  }

  public void addStatusColumn(){
    addColumn(null/*theFocDesc*/, FField.STATUS_FIELD_ID, null, "", 3, false);
  }

  public FTableColumn addLineNumberColumn(){
    FTableColumn column = new FTableColumn(FField.getLineNumberField(),FField.LINE_NUMBER_FIELD_ID,"",5,false);
    insertColumnBefore(column, 0);
    return column;
  }

  public void addTreeColumn(FGTreeInTable tree){
  	FTableColumn fCol = addColumn(null/*theFocDesc*/, FField.TREE_FIELD_ID, null, "", 30, false);
  	fCol.setShowConfigurable(false);
  	fCol.setCellEditor(new TreeCellControler(table));
  }
  
  public FTableColumn addGanttChartColumn(BasicGanttScale gantScale){
    return addGanttChartColumn(new GanttChartCellControler(gantScale));
  }

  public FTableColumn addGanttChartActivityColumn(BasicGanttScale gantScale){
    return addGanttChartActivityColumn(gantScale, false);
  }
  
  public FTableColumn addGanttChartActivityColumn(BasicGanttScale gantScale, boolean showExpandCollapseHeaderButton){
    GanttChartActivityCellControler ganttChartActivityCellControler = new GanttChartActivityCellControler(gantScale);
    FGanttChartActivityColumnHeaderRenderer columnHeaderRenderer = (FGanttChartActivityColumnHeaderRenderer)ganttChartActivityCellControler.getColumnHeaderRenderer();
    columnHeaderRenderer.setShowExpandCollapseHeaderButton(showExpandCollapseHeaderButton);
    return addGanttChartColumn(ganttChartActivityCellControler);
  }
  
  public FTableColumn addGanttChartColumn(AbstractCellControler controler){
    FTableColumn fCol = addGanttChartColumn();
    fCol.setCellEditor(controler);
    return fCol;
  }
  
  private FTableColumn addGanttChartColumn(){
    return addColumn(null/*theFocDesc*/, FField.FLD_ID_GANTT_CHART, null, GANTT_COLUMN_TITLE, 30, false);
  }
  
  public FTableColumn addDrawingColumn(FDrawingScale drawingScale){
    FTableColumn fCol = addColumn(null, FField.FLD_ID_DRAWING, null, DRAWING_COLUMN_TITLE, 30, false);
    DrawingCellControler drawingCellController = new DrawingCellControler(drawingScale);
    fCol.setCellEditor(drawingCellController);
    return fCol;
  }
  
  public FTableColumn getColumnAt(int col) {
    FTableColumn tableColumn = null;
    if (columns != null && col >= 0) {
      tableColumn = (FTableColumn) columns.get(col);
    }
    return tableColumn;
  }

  public int getVisibleColumnIndex(int i) {
    int col = i;
    if (visibleColumnsIndexes != null && visibleColumnsIndexes.size() > 0 && i >= 0) {
      Integer intObj = (Integer) visibleColumnsIndexes.get(i);
      col = intObj.intValue();
    }
    return col;
  }

  public FTableColumn getVisibleColumnAt(int i) {
    return getColumnAt(getVisibleColumnIndex(i));
  }

  public int getColumnCount() {
    int count = (columns != null) ? columns.size() : 0;
    return count;
  }
  
  public int getColumnRepresentationCount(){
    int count = (columnRepresentation != null) ? columnRepresentation.size() : 0;
    return count;
  }
  
  public FTableColumnRepresentation getColumnRepresentationAt(int col) {
    FTableColumnRepresentation tableColumn = null;
    if(columnRepresentation != null && col >= 0){
      tableColumn = (FTableColumnRepresentation) columnRepresentation.get(col);
    }
    return tableColumn;
  }
  
  public int getColumnRepresentationIndexForId(int id) {
    int index = -1;
    for(int i = 0; i < getColumnRepresentationCount(); i++){
      FTableColumn tableCol = getColumnRepresentationAt(i);
      if (tableCol != null) {
        if (tableCol.getID() == id) {
          index = i;
          break;
        }
      }
    }
    return index;
  }

  public FTableColumnRepresentation getColumnRepresentationById(int id) {
    int index = getColumnRepresentationIndexForId(id);
    return getColumnRepresentationAt(index);
  }

  public int getColumnIndexForId(int id) {
    int index = -1;
    for (int i = 0; i < getColumnCount(); i++) {
      FTableColumn tableCol = getColumnAt(i);
      if (tableCol != null) {
        if (tableCol.getID() == id) {
          index = i;
          break;
        }
      }
    }
    return index;
  }

  public FTableColumn getColumnById(int id) {
    int index = getColumnIndexForId(id);
    return getColumnAt(index);
  }
  
  public boolean containsFieldPath(FFieldPath aFieldPath){
    boolean contains = false;
    for (int i = 0; i < getColumnCount() && !contains; i++){
      FTableColumn col = getColumnAt(i);
      FFieldPath fieldPath = col.getFieldPath();
      if(fieldPath.isEqualTo(aFieldPath)){
        contains = true;
      }
    }
    return contains;
  }
  
  public int getTotalWidth(){
    int totalWidth = 0;
    for(int i=0; i<getColumnCount(); i++){
      FTableColumn tc = getColumnAt(i);
      if(tc != null){
        totalWidth += tc.getPreferredWidth();//tc.getSize();
      }
    }
    return totalWidth ;//* Globals.CHAR_WIDTH;
  }
  
  public FTable getTable(){
    return table;
  }
  
  public void setTable(FTable table){
    this.table = table;
  }
  
  private void initViewConfig(){
	  if(isColumnConfigPersistent()){
	  	if(listOfViews == null){
	  		listOfViews = ViewFocCache.getInstance().get(this.getViewKey(), true);
	  		//listOfViews = new ViewFocList(this.getViewContext());
	  	}
	  	if(listOfViews != null) listOfViews.setTableView(this);
	  	
	  	if(viewRights == null){
	  		FocGroup group = Globals.getApp().getGroup();
	  		FocList  list  = group.getViewRightsList();
	  		list.loadIfNotLoadedFromDB();
	  		for(int i=0; i<list.size() && viewRights == null; i++){
	  			GrpViewRights rights = (GrpViewRights) list.getFocObject(i);
	  			if(rights != null && viewKey != null && viewContext != null){
	  				if(rights.getViewKey().equals(viewKey) && rights.getViewContext().equals(viewContext)){
	  					viewRights = rights;
	  				}
	  			}
	  		}
	  	}
	  	
	  	if(userView == null){
	  		/*
	  		UserView usrViewTemp = new UserView(new FocConstructor(UserViewDesc.getInstance(), null));
	  		usrViewTemp.setViewKey(viewKey);
	  		usrViewTemp.setViewContext(viewContext);
	  		usrViewTemp.setUser(Globals.getApp().getUser());
	    	SQLFilter filter = new SQLFilter(usrViewTemp, SQLFilter.FILTER_ON_SELECTED);
	    	filter.addSelectedField(UserViewDesc.FLD_VIEW_KEY);
	    	filter.addSelectedField(UserViewDesc.FLD_VIEW_CONTEXT);
	    	filter.addSelectedField(UserViewDesc.FLD_USER);
	    	FocList userViewList = new FocList(new FocLinkSimple(UserViewDesc.getInstance()), filter);
	    	userViewList.loadIfNotLoadedFromDB();
	    	userView = (UserView) userViewList.getAnyItem();
	    	if(userView == null){
	    		userView = (UserView) userViewList.newEmptyItem();
	    		userView.setUser(Globals.getApp().getUser());
	    		userView.setViewKey(viewKey);
	    		userView.setViewContext(viewContext);
	    	}
	      */
	  		userView = ViewFocCache.getInstance().getUserView(viewKey, viewContext);
	    	userView.setViewFocList(listOfViews);
	    	userView.getFocProperty(UserViewDesc.FLD_VIEW).addListener(new FPropertyListener(){
					public void dispose(){}

					public void propertyModified(FProperty property){
						applyViewConfig();
						if(userView != null){
							//We need the invoke later because at that point the property is modified but the FocObject
							//userView is not modified yet and the validate will not cause it to save
							//Bug was that we do not save the choice of users when they select a view
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									userView.validate(true);
								}
							});
						}
					}
	    	});
	  	}
	  	
	  	if(viewRights != null && viewRights.getRight() == GrpViewRightsDesc.ALLOW_NOTHING && viewRights.getViewConfigRef() > 0){
	  		userView.setViewConfigRef(viewRights.getViewConfigRef());
	  	}
	  	
	  	//viewConfig = (ViewConfig) listOfViews.getAnyItem();
	  	applyViewConfig();
	  }
  }
  
  public void setColumnVisibilityAccordinglyToConfig(){
  	initViewConfig();
  	if(userView != null){
  		userView.setViewFocList(listOfViews);
  		applyViewConfig();
  	}
  }

  public void applyViewConfig(){
  	if(userView != null){
  		applyViewConfig(userView.getViewConfig());
  	}
  }
  
  public void applyViewConfig(ViewConfig viewConfig){
    if(viewConfig != null){
	  	FocList colList = viewConfig.getColumnsConfigList_Full(this);
		  for(int i = 0; i < colList.size(); i++){
		    ColumnsConfig col = (ColumnsConfig) colList.getFocObject(i);
		    FTableColumn tableCol = getColumnById(col.getColumnID());
		    if(tableCol != null){
		    	tableCol.setColumnConfig(col);
		    	tableCol.setShow(col.isShow());
		    }
		    FTableColumnRepresentation tableColRep = getColumnRepresentationById(col.getColumnID());
		    if(tableColRep != null){
		    	tableColRep.setColumnConfig(col);
		    	tableColRep.setShow(col.isShow());
		    }
		  }
		  
		  //Scan all TableColumns and set their Titles
		  for(int i=0; i<getColumnCount(); i++){
		  	FTableColumn tableCol = getColumnAt(i);
		  	if(tableCol != null){
		  		ColumnsConfig colConfig = tableCol.getColumnConfig();
		  		if(colConfig == null){
			  		FTableColumnRepresentation tableColRep = tableCol.getColumnRepresentation();
			  		if(tableColRep != null){
			  			colConfig = tableColRep.getColumnConfig();	
			  		}
		  		}
		  		if(colConfig != null && tableCol.getTableColumn() != null){
		  			tableCol.getTableColumn().setHeaderValue(colConfig.getColumnTitle());
		  			tableCol.adjustColumnNumberFormat(colConfig);
		  		}
		  	}
		  }
		  
		  adjustColumnVisibility();
		  Globals.getDisplayManager().pack();
    }
  }

	//B-COL_REORDER
  /*
  public void adjustColumnVisibility_OLD(){
    if(visibleColumnsIndexes == null){
      visibleColumnsIndexes = new ArrayList<Integer>();
      for(int i = 0; i < getColumnCount(); i++){
        visibleColumnsIndexes.add(Integer.valueOf(i));
      }
    }
  	
    int order = 0;
    for(int i = 0; i < getColumnCount(); i++){
      FTableColumn fCol = getColumnAt(i);
      if(fCol != null && fCol.isShow()){
        visibleColumnsIndexes.set(order, Integer.valueOf(i));
        fCol.setOrderInView(order++);
      }
    }
    
    for(int i = 0; i < getColumnCount(); i++){
      FTableColumn fCol = getColumnAt(i);
      if(fCol != null && fCol.isShow() != fCol.isVisible()){
        if(fCol.isShow()){
          getTable().getColumnModel().addColumn(fCol.getTableColumn());
          getTable().getColumnModel().moveColumn(getTable().getColumnModel().getColumnCount()-1, fCol.getOrderInView());
        }else{
          getTable().getColumnModel().removeColumn(fCol.getTableColumn());          
        }
        
        fCol.setVisible(fCol.isShow());
      }
    }

    if(getColumnResizingMode() == COLUMN_AUTO_RESIZE_MODE){
    	getTable().autoResizeColumns();
    }    
  }
  */
  
  public void adjustColumnVisibility(){
    if(visibleColumnsIndexes == null){
      visibleColumnsIndexes = new ArrayList<Integer>();
      for(int i = 0; i < getColumnCount(); i++){
        visibleColumnsIndexes.add(Integer.valueOf(i));
      }
    }
    
    Collections.sort(visibleColumnsIndexes, new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				int comp = 0;
				FTableColumn fCol1 = getColumnAt(o1);
				FTableColumn fCol2 = getColumnAt(o2);
				if(fCol1 != null && fCol2 != null){
					int show1 = fCol1.isShow() ? 1 : 0;
					int show2 = fCol2.isShow() ? 1 : 0;
					comp = - (show1 - show2);
					if(comp == 0){
						if(getTable().getTableHeader() instanceof FColumnGroupTableHeader){
							FColumnGroupTableHeader colHeader = (FColumnGroupTableHeader) getTable().getTableHeader();
							ArrayList<Integer> a1 = colHeader.getGroupsIndexesForColumn(fCol1);
							ArrayList<Integer> a2 = colHeader.getGroupsIndexesForColumn(fCol2);
							if(a1.size() > 0 && a2.size() > 0){
								int max = Math.min(a1.size(), a2.size());
								for(int i=0; i<max && comp == 0; i++){
									comp = a1.get(i) - a2.get(i);									
								}
							}
						}
						if(comp == 0){
							ColumnsConfig cc1 = fCol1.getColumnConfig();
							ColumnsConfig cc2 = fCol2.getColumnConfig();
							if(cc1 != null && cc2 != null){
								comp = cc1.getColumnOrder() - cc2.getColumnOrder();
							}
						}
					}
				}
				return comp;
			}
    });

    for(int i = 0; i < visibleColumnsIndexes.size(); i++){
    	int index = visibleColumnsIndexes.get(i);
    	FTableColumn fCol = getColumnAt(index);
    	if(fCol != null){
    		fCol.setOrderInView(i);
    		//Globals.logString("pos " + i + " Col model index "+index);
    	}
    }

    ArrayList<TableColumn> array = new ArrayList<TableColumn>();
    DefaultTableColumnModel colModel = (DefaultTableColumnModel) getTable().getColumnModel();
    for(int i = 0; i < colModel.getColumnCount(); i++){
    	TableColumn col = colModel.getColumn(i);
    	if(col != null){
    		array.add(col);
    	}
    }
    
    for(int i = 0; i < array.size(); i++){
    	TableColumn col = array.get(i);
    	if(col != null){
    		colModel.removeColumn(col);
    	}
    }
    
    for(int i = 0; i < visibleColumnsIndexes.size(); i++){
    	int index = visibleColumnsIndexes.get(i);
    	FTableColumn fCol = getColumnAt(index);
    	if(fCol != null && fCol.isShow()){
    		getTable().getColumnModel().addColumn(fCol.getTableColumn());
        //getTable().getColumnModel().moveColumn(getTable().getColumnModel().getColumnCount()-1, fCol.getOrderInView());    		
    	}
  		fCol.setVisible(fCol.isShow());    	
    }
    
    if(getColumnResizingMode() == COLUMN_AUTO_RESIZE_MODE){
    	getTable().autoResizeColumns();
    }
  }
	//E-COL_REORDER  
  
  public FTableColumn getFTableColumn(TableColumn tableColumn){
    FTableColumn fTableColumn = null;
    boolean found = false;
    for(int i = 0; i < getColumnCount() && !found; i++){
      fTableColumn = getColumnAt(i);
      if(fTableColumn.getTableColumn().equals(tableColumn)){
        found = true;
      }
    }
    return fTableColumn;
  }
  
  public double getColumnWidthFactor() {
    return columnWidthFactor;
  }
  
  public void setColumnWidthFactor(double columnWidthFactor) {
    this.columnWidthFactor = columnWidthFactor;
  }
  
  public int getLineNumber() {
    return lineNumber;
  }
  
  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }
  
  public FocListFilter getFilter() {
    return filter;
  }
  
  public void setFilter(FocListFilter filter) {
    setFilter(filter, true);
  }
  
  public void setFilter(FocListFilter filter, boolean owner){
    this.filter = filter;
    this.owner = owner;
  }
  
  public void popupColumnConfigurationPanel(FTable table){
  	if(viewRights != null && viewRights.getRight() == GrpViewRightsDesc.ALLOW_NOTHING){
  		Globals.getDisplayManager().popupMessage("Access right denied.");
  	}else{
  		boolean editable = viewRights == null || viewRights.getRight() == GrpViewRightsDesc.ALLOW_CREATION;
	  	FObject selectionProp = new FObject(null, 123, userView.getViewConfig());
	  	listOfViews.setSelectionProperty(selectionProp);
	
	  	ViewSelectionPanel browsePanel = new ViewSelectionPanel(listOfViews, editable);
	  	//ViewConfigGuiBrowsePanel browsePanel = new ViewConfigGuiBrowsePanel(listOfViews, FocObject.DEFAULT_VIEW_ID);
	  	Globals.getDisplayManager().popupDialog(browsePanel, "View Configuration", true);
	  	
	  	ViewConfig viewConfig = (ViewConfig) selectionProp.getObject();
	  	userView.setViewConfig(viewConfig);
	  	userView.validate(true);
	  	selectionProp.dispose();
	  	selectionProp = null;
	  	
	  	applyViewConfig();
  	}
  }

  public String getViewKey(){
    return viewKey;
  }

  public void setViewKey(String viewKey){
    this.viewKey = viewKey;
  }

  public String getViewContext(){
    return viewContext;
  }

  public void setViewContext(String viewKey){
    this.viewContext = viewKey;
  }

  public boolean isColumnConfigPersistent(){
    return getViewKey() != null && getViewKey().trim().compareTo("") != 0;
  }

  public int getDetailPanelViewIDForNewItem(){
    return detailPanelViewIDForNewItem;
  }

  public void setDetailPanelViewIDForNewItem(int detailPanelViewIDForNewItem){
    this.detailPanelViewIDForNewItem = detailPanelViewIDForNewItem;
  }

  public void setDetailPanelViewIDForNewItem(int detailPanelViewIDForNewItem, boolean editAfterInsertion){
    this.detailPanelViewIDForNewItem = detailPanelViewIDForNewItem;
    setEditAfterInsertion(editAfterInsertion);
  }

  public int getColumnResizingMode(){
    return columnResizingMode;
  }

  public void setColumnResizingMode(int columnResizingMode){
    this.columnResizingMode = columnResizingMode;
    if(getTable() != null){
    	getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }
  }

  public int getColumnsToFreeze() {
    return columnsToFreeze;
  }

  public void setColumnsToFreeze(int columnsToFreeze) {
    this.columnsToFreeze = columnsToFreeze;
  }
  
  public FColumnGroupHeaderConstructor getColumnGroupHeaderConstructor() {
    return columnGroupHeaderConstructor;
  }

	public boolean isEditAfterInsertion() {
		return editAfterInsertion;
	}

	public void setEditAfterInsertion(boolean editAfterInsertion) {
		this.editAfterInsertion = editAfterInsertion;
	}

	public UserView getUserView() {
  	initViewConfig();
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public boolean isPopupLoadingMessage() {
		return popupLoadingMessage;
	}

	public void setPopupLoadingMessage(boolean popupLoadingMessage) {
		this.popupLoadingMessage = popupLoadingMessage;
	}

	public GrpViewRights getGrpViewRights(){
		return viewRights;
	}

	public boolean isDisableAutoAdjustViewportPosition() {
		return disableAutoAdjustViewportPosition;
	}

	public void setDisableAutoAdjustViewportPosition(boolean disableAutoAdjustViewportPosition) {
		this.disableAutoAdjustViewportPosition = disableAutoAdjustViewportPosition;
	}
}
