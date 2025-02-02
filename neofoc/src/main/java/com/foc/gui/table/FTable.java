/*
 * Created on 15 fevr. 2004
 */
package com.foc.gui.table;

import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import java.awt.print.Printable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.text.*;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.desc.*;
import com.foc.desc.field.FField;
import com.foc.dragNDrop.FocDefaultDropTargetListener;
import com.foc.dragNDrop.FocDragGestureListener;
import com.foc.dragNDrop.FocDragable;
import com.foc.dragNDrop.FocDropTargetListener;
import com.foc.dragNDrop.FocDropable;
import com.foc.dragNDrop.FocTransferable;
import com.foc.dragNDrop.FocTransferableObjectCompleter;
import com.foc.gui.*;
import com.foc.gui.lock.TableLockInputVerifier;
import com.foc.gui.table.cellControler.*;
import com.foc.gui.table.cellControler.renderer.FColumnHeaderRenderer;
import com.foc.gui.table.print.FTablePrintable;
import com.foc.gui.treeTable.FTreeTableModel;
import com.foc.list.*;
import com.foc.print.FTablePrintParameters;
import com.foc.property.FProperty;
import com.foc.property.FString;

import javax.swing.table.*;
import javax.swing.event.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FTable extends JTable implements FocusListener, ListSelectionListener, FocDragable, FocDropable {

  private FixedColumnScrollPane                     scrollpane                = null;
  private ArrayList<ListSelectionListener>          selectionListeners        = null;
  private ArrayList<FTableCellStartEditingListener> cellStartEditingListeners = null;
  private boolean                                   invokeLaterForViewpositionAdjustmentAlreadyCalled = false;

  //private ArrayList<FTableMouseListener>            mouseListeners            = null;
  
  public static int MAX_WIDTH = 5000;
  //public static int MAX_WIDTH = 600;

  private int maxWidth = MAX_WIDTH;

  private int maxColWidth = 0;
  
  private FPopupMenu popupMenu                 = null;
  private int        lastRowSelection          = -1;
  private boolean    disableSelectionListeners = false;
  
  private boolean dropable = true;
  private boolean fixed    = false;
  private FocTransferableObjectCompleter transferableObjectCompleter = null;
  private DragGestureRecognizer  dragGestureRecognizer = null;
  private FocDragGestureListener dragGesturListener    = null;
  private FocDropTargetListener  dropTargetListener    = null;
  private FTablePrintParameters  printParameters       = null;

  private boolean   shouldRecomputePreferredScrollableViewPortSize = false;
  private Dimension dim                                            = null;
  private Dimension fixedScrollableDimension                       = null;

  private boolean   showFormulaCellTag = true;
  private ITableOverPaint tableOverPainter = null;
  protected int newSelectedViewportHeight = 0;
  
  private void initDrag(){
    disposeDrag();
    DragSource dragSource = DragSource.getDefaultDragSource();
    dragGesturListener    = FocDragGestureListener.newFocdragGestureListener();
    dragGestureRecognizer = dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, dragGesturListener);
  }
  
  private void initDrop(DropTargetListener dropTargetListener){
    disposeDrop();
    this.dropable = true;
    this.dropTargetListener = (FocDropTargetListener) dropTargetListener;
    new DropTarget(this, DnDConstants.ACTION_COPY, dropTargetListener, true);
  }
  
  public FTable(FAbstractTableModel tableModel, boolean dropable) {
    this(tableModel, dropable, false);
  }
  
  public boolean getShowHorizontalLines() {
  	if(getTableModel() instanceof FTreeTableModel){
  		return true;
  	}else{
	    return false;
  	}
	}
  
	public boolean getShowVerticalLines() {
	  return true;
	}

  public FTable(FAbstractTableModel tableModel, boolean dropable, boolean fixed ) {
    super(tableModel);

    if(Globals.getDisplayManager().getLookAndFeel() == DisplayManager.LOOK_AND_FEEL_NIMBUS){
    	setSelectionBackground(Globals.getDisplayManager().getBarmajaOrangeDark());
    }
    
    //Globals.logString(" Table UI class : "+getUIClassID());
    
    //These had no effect so we are using an overwrite of getshowhorizontalgrid...
    //setShowHorizontalLines(false);
    //setShowVerticalLines(true);
    //setShowGrid(false);
    
    //tableColumnModel = new FTableColumnModel();
    //setColumnModel(tableColumnModel);
    
    this.fixed = fixed;
    //setBackground(Color.RED);
    getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.setRowSelectionAllowed(true);
    this.setColumnSelectionAllowed(false);
    setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    //setSurrendersFocusOnKeystroke(true);
        
    FTableView view = tableModel != null ? tableModel.getTableView() : null;
    view.setTable(this);
    //TableColumnModel colModel = this.getColumnModel();
    if (view != null ) {
      for (int i = 0; i < (view.getColumnCount()); i++) {
        FTableColumn fCol = view.getColumnAt(i);
        if (fCol != null) {
          TableColumn col = getColumnForId(fCol.getID());
          fCol.setPreviousOrderInView(i);
          fCol.setTableColumn(col);
          if (col != null) {
          	TableCellRenderer columnHeaderRenderer = null;
            AbstractCellControler cellEditor = fCol.getCellEditor();
            if(cellEditor != null){
              col.setCellRenderer(cellEditor.getRenderer());
              col.setCellEditor(cellEditor.getEditor());
              columnHeaderRenderer = cellEditor.getColumnHeaderRenderer();
            }
            Color headerRendererBackColor = fCol.getHeaderBackColor();
            if(columnHeaderRenderer == null){
            	columnHeaderRenderer = new FColumnHeaderRenderer((TableCellRenderer)col.getHeaderRenderer(), headerRendererBackColor);
            }
            //col.setHeaderRenderer(new FColumnHeaderRenderer((TableCellRenderer)col.getHeaderRenderer(),headerRendererBackColor));
            col.setHeaderRenderer(columnHeaderRenderer);
            //AUTOSIZE
            double sizeFactor = 0.76 * view.getColumnWidthFactor();
            /*if(fCol.isShow())*/ maxColWidth += fCol.getPreferredWidth() * sizeFactor;
            col.setPreferredWidth(Double.valueOf(fCol.getPreferredWidth() * sizeFactor).intValue());
            
            /*
             * final JTextField etf = new JTextField();
             * 
             * DefaultCellEditor editor = new DefaultCellEditor(etf) { public
             * Component getTableCellEditorComponent(JTable table, Object value,
             * boolean isSelected, int row, int col) {
             * etf.setText((String)value); requestFocus(); etf.selectAll();
             * return etf; } }; col.setCellEditor(editor);
             */
          }
        }
      }
    }
    /*
     * int vColIndex = 1; int margin = 2; packColumn(this, vColIndex, margin);
     * packColumns(this, 2);
     */
    
    getTableHeader().setReorderingAllowed(false);
    initDrag();
    setDropable(dropable);
    
    if(getFocList() != null){
    	setName(getFocList().getFocDesc().getStorageName()+".TABLE");
    }
    if(ConfigInfo.isUnitDevMode()){
    	StaticComponent.setComponentToolTipText(this, getName());
    }
    
    if(!fixed){
      tableModel.afterTableConstruction(this);    
    }
    
    //attachMouseListener();    
    
    computeMaxColWidth();
  }
  
  public void dispose(){
  	printParameters = null;
  	
    if(scrollpane != null){
      scrollpane.dispose();
      scrollpane = null;
    }
    
    if(selectionListeners != null){
      selectionListeners.clear();
      selectionListeners = null;
    }

    /*
    if(mouseListeners != null){
    	mouseListeners.clear();
    	mouseListeners = null;
    }
    */
    
    if(cellStartEditingListeners != null){
      for(int i=0; i<cellStartEditingListeners.size(); i++){
        FTableCellStartEditingListener listener = (FTableCellStartEditingListener) cellStartEditingListeners.get(i);
        listener.dispose();
        listener = null;
      }
      cellStartEditingListeners.clear();
      cellStartEditingListeners = null;
    }
    
    ListSelectionModel listSelectionModel = getSelectionModel();
    if(listSelectionModel != null){
      ListSelectionListener[] listSelectionListeners = ((DefaultListSelectionModel)listSelectionModel).getListSelectionListeners();
      for(int i = 0; i < listSelectionListeners.length; i++){
        ListSelectionListener listener = listSelectionListeners[i];
        listSelectionModel.removeListSelectionListener(listener);
      }
    }
    //this.selectionModel = null;
    
    disposeDrop();
    disposeDrag();
    if(popupMenu != null){
    	popupMenu.dispose();
    	popupMenu = null;
    }
  }

  private void disposeDrag(){
    if(dragGesturListener != null){
      dragGesturListener = null;
    }
    if(dragGestureRecognizer != null){
      dragGestureRecognizer = null;
    }
  }
  
  private void disposeDrop(){
    dropTargetListener = null;
  }
  
  @Override
  public void setAutoResizeMode(int mode) {
  	super.setAutoResizeMode(mode);
  	if(scrollpane != null && scrollpane.getFixedTable() != null && scrollpane.getFixedTable() != this){
  		scrollpane.getFixedTable().setAutoResizeMode(mode);
  	}
  }
  
  public void computeMaxColWidth(){
  	maxColWidth = 0;
  	FTableView view = getTableModel() != null ? getTableModel().getTableView() : null;
	  for (int i = 0; i < view.getColumnCount(); i++) {
	    FTableColumn fCol = view.getColumnAt(i);
	    if (fCol != null) {
	      TableColumn col = getColumnForId(fCol.getID());
	      if (col != null) {
	        double sizeFactor = 0.76 * view.getColumnWidthFactor();
	        if(fCol.isVisible()){
	        	maxColWidth += fCol.getPreferredWidth() * sizeFactor;
	        }
	        col.setPreferredWidth(Double.valueOf(fCol.getPreferredWidth() * sizeFactor).intValue());
	      }
	    }
	  }
	  //tableColumnModel.setTotalColumnWidth(maxColWidth);
	  //Globals.logString("Total Width="+maxColWidth);
  }  
  
  
  /**
   * Workaround for BasicTableUI anomaly. Make sure the UI never tries to 
   * resize the editor. The UI currently uses different techniques to 
   * paint the renderers and editors; overriding setBounds() below 
   * is not the right thing to do for an editor. Returning -1 for the 
   * editing row in this case, ensures the editor is never painted. 
   */
  
  public int getEditingRow() {
    /*(getColumnClass(editingColumn) == TreeTableModel.class) ? -1 :
    editingRow;*/  
    FTableView view = getTableModel() != null ? getTableModel().getTableView() : null;
    FTableColumn tc = view.getColumnAt(editingColumn);
    return (tc != null && tc.getID() == FField.TREE_FIELD_ID) ? -1 : editingRow;
  }
  
  public FTable(FAbstractTableModel tableModel) {
    this(tableModel, false);
  }
  
/*  protected JTableHeader createDefaultTableHeader() {
    return new JTableHeader(columnModel) {

      @Override
      public void paint(Graphics g) {
        //super.paint(g);
                
        
        javax.swing.JFrame frame = new javax.swing.JFrame();        
        FPanel panel = new FPanel();
        JLabel label = new JLabel("ALLO!");
        panel.add(label, 0, 0);
        
        JPanel cp = frame.getContentPane(); 
        
        frame.pack();
        
        label.paint(g);
      }
      
      
//        public String getToolTipText(MouseEvent e) {
//            String tip = null;
//            java.awt.Point p = e.getPoint();
//            int index = columnModel.getColumnIndexAtX(p.x);
//            int realIndex = 
//                    columnModel.getColumn(index).getModelIndex();
//            return columnToolTips[realIndex];
//        }
    };
  }
*/  
  
  /*@Override
  public String getToolTipText(MouseEvent e) {
    //return super.getToolTipText(arg0);
    java.awt.Point p = e.getPoint();
    int index = columnModel.getColumnIndexAtX(p.x);
    return ((FAbstractTableModel)getModel()).getColumnName(index);
  }*/

  public FAbstractTableModel getTableModel(){
    return (FAbstractTableModel) getModel();
  }

  public FTableView getTableView(){
    FAbstractTableModel model = getTableModel();
    return model != null ? model.getTableView() : null;
  }
  
  private static TableLockInputVerifier tableLockInputVerifier = null;
  public static TableLockInputVerifier getTableLockInputVerifier(){
    if(tableLockInputVerifier == null){
      tableLockInputVerifier = new TableLockInputVerifier();
    }
    return tableLockInputVerifier;
  }
  
  public void reactToFocusChange(boolean focusLost){
    reactToRowLostFocus(focusLost);
  }
  
  public void reactToRowLostFocus(boolean focusLost){
    if(!isDisableSelectionListeners()){
      int newRowSelection = getSelectedRow();
     
      boolean rowRequestToLooseFocus = lastRowSelection != newRowSelection || focusLost; 
      boolean rowRequestTolooseFocusAccepted = false;
      if(rowRequestToLooseFocus){
        rowRequestTolooseFocusAccepted = !Globals.getDisplayManager().shouldLockFocus(true);
      }
        
      if(rowRequestToLooseFocus){
        if(!rowRequestTolooseFocusAccepted){
          //This mainly helps when another table is trying to get focus and to change selection. The selection cchange can efffect
          //the locking table this is why we reset the selecion
          setRowSelectionInterval(lastRowSelection, lastRowSelection);
          //requestFocus();
          requestFocusInWindow();
          /*SwingUtilities.invokeLater(new Runnable(){
            public void run(){
              requestFocus();
              requestFocusInWindow();
            }
          });*/
          
        }else{
          lastRowSelection = newRowSelection;
          FocList list = getFocList();
          if(list != null){
            FocObject selObj = newRowSelection >= 0 ? getElementAt(newRowSelection) : null;
            list.reactToNewSelection(getThis(), selObj, focusLost);
          }
          
          notifySelectionListeners(null);
        }
      }
    }
  }
  
  public FTable getThis(){
    return this;
  }
  
  /*
   * public void setEditableColor() { FTableModel tableModel =
   * (FTableModel)this.getModel(); FTableView view = tableModel != null ?
   * tableModel.getTableView() : null; if (view != null) { for (int i = 0; i <
   * view.getColumnCount(); i++) { FTableColumn fCol = view.getColumnAt(i); if
   * (fCol != null) { TableColumn col = getColumnForId(fCol.getID()); if (col !=
   * null) { if(!fCol.getEditable()){ TableCellRenderer cellRend =
   * col.getCellRenderer(); if(cellRend != null){ JComponent comp = (JComponent)
   * cellRend ; if(comp != null){ comp.setBackground(new Color(10,10,10)); } } } } } } } }
   */
  public TableColumn getColumnForId(int id) {
    TableColumn col = null;
    FAbstractTableModel model = (FAbstractTableModel) this.getModel();
    TableColumnModel colModel = this.getColumnModel();
    if (model != null && colModel != null) {
      FTableView view = model.getTableView();
      if (view != null) {
        int index = view.getColumnIndexForId(id);
        index = view.getVisibleColumnIndex(index);
        if(index >= 0 && index < colModel.getColumnCount()){
        	col = colModel.getColumn(index);
        }
      }
    }
    return col;
  }

  public FPopupMenu getPopupMenu(){
    if(popupMenu == null){
      popupMenu = new FPopupMenu();
      
      MouseListener mouseListener = new MouseAdapter(){
        public void mousePressed(MouseEvent e) {
          maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
          maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
          if (e.isPopupTrigger()) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
          }
        }
      };
      
      FixedColumnScrollPane fixedScrollPane = getScrollPane();
      addMouseListener(mouseListener);
      if(fixedScrollPane.getFixedTable() != null){
        fixedScrollPane.getFixedTable().addMouseListener(mouseListener);
      }
      
    }
    return popupMenu;
  }
  /*
   * public void packColumns(JTable table, int margin) { for (int c=0; c
   * <table.getColumnCount(); c++) { packColumn(table, c, 2); } }
   */

  // Sets the preferred width of the visible column specified by vColIndex. The
  // column
  // will be just wide enough to show the column head and the widest cell in the
  // column.
  // margin pixels are added to the left and right
  // (resulting in an additional width of 2*margin pixels).
  /*
   * public void packColumn(JTable table, int vColIndex, int margin) {
   * TableModel model = table.getModel(); DefaultTableColumnModel colModel =
   * (DefaultTableColumnModel)table.getColumnModel(); TableColumn col =
   * colModel.getColumn(vColIndex); int width = 0; // Get width of column header
   * TableCellRenderer renderer = col.getHeaderRenderer(); if (renderer == null) {
   * renderer = table.getTableHeader().getDefaultRenderer(); } Component comp =
   * renderer.getTableCellRendererComponent( table, col.getHeaderValue(), false,
   * false, 0, 0); width = comp.getPreferredSize().width; // Get maximum width
   * of column data for (int r=0; r <table.getRowCount(); r++) { renderer =
   * table.getCellRenderer(r, vColIndex); comp =
   * renderer.getTableCellRendererComponent( table, table.getValueAt(r,
   * vColIndex), false, false, r, vColIndex); width = Math.max(width,
   * comp.getPreferredSize().width); } // Add margin width += 2*margin; // Set
   * the width col.setPreferredWidth(width); }
   */
  
  public void setFixedScrollableDimension(Dimension fixedScrollableDimension){
  	this.fixedScrollableDimension = fixedScrollableDimension;
  }
  
  public Dimension getPreferredScrollableViewportSize() {
  	if(isShouldRecomputePreferredScrollableViewPortSize()){
  		dim = computePreferredScrollableViewportSize();
  		if(fixedScrollableDimension != null){
  			dim = fixedScrollableDimension;
  		}
  		setShouldRecomputePreferredScrollableViewPortSize(false);
  	}
  	return dim;
  	//return super.getPreferredScrollableViewportSize();
  }

  public void setShouldRecomputePreferredScrollableViewPortSize(boolean shouldRecomputePreferredScrollableViewPortSize){
  	this.shouldRecomputePreferredScrollableViewPortSize = shouldRecomputePreferredScrollableViewPortSize;
  }

  public boolean isShouldRecomputePreferredScrollableViewPortSize(){
  	return this.shouldRecomputePreferredScrollableViewPortSize || dim == null;
  }

  private Dimension computePreferredScrollableViewportSize() {
    Dimension dim = super.getPreferredScrollableViewportSize();

  	Dimension autoDim = null;
    if(getTableView().getColumnResizingMode() == FTableView.COLUMN_WIDTH_FACTOR_MODE){
    	autoDim = AutofitTableColumns.autoResizeTable(this, true, false);
      computeMaxColWidth();
    }else if(getTableView().getColumnResizingMode() == FTableView.COLUMN_AUTO_RESIZE_MODE){
    	autoDim = AutofitTableColumns.autoResizeTable(this, true, true);
      maxColWidth = autoDim.width;  
    }
    
    dim.width = maxColWidth;
    FTableView view = getTableView();
    if(!fixed){
			//if(Globals.getDisplayManager().getLookAndFeel() == DisplayManager.LOOK_AND_FEEL_METAL){
				setRowHeight(autoDim.height);
			//}
    }
    
    if(scrollpane != null && scrollpane.getFixedTable() != null){
      scrollpane.getFixedTable().getTableView().setColumnResizingMode(scrollpane.getScrollTable().getTableView().getColumnResizingMode());
			//if(Globals.getDisplayManager().getLookAndFeel() == DisplayManager.LOOK_AND_FEEL_METAL){
				scrollpane.getFixedTable().setRowHeight(scrollpane.getScrollTable().getRowHeight());
			//}
    }
    
    dim.height = getRowHeight() * view.getLineNumber();
    if (dim.width > maxWidth) {
      dim.width = maxWidth;
    }
    //Globals.logString("Begin REPAINT OF THE SCROLL PANE : "+getRowHeight()+" "+this.toString());
    scrollpane.repaint();

    return dim;
  }  

  public void autoResizeColumns(){
  	AutofitTableColumns.autoResizeTable(this, true, true);
  	FixedColumnScrollPane scroll = getScrollPane();
  	if(scroll != null){
  		FTable fixedTable = scroll.getFixedTable();
  		if(fixedTable != null && fixedTable != this){
  	    
  			scroll.refreshRowHeaderSize();
  			/*
  			//ICI-GUI
  			//AutofitTableColumns.autoResizeTable(fixedTable, true, true);
  			fixedTable.setShouldRecomputePreferredScrollableViewPortSize(true);
  	    scroll.getRowHeader().setPreferredSize(fixedTable.getPreferredScrollableViewportSize());
  	    
  			scroll.doLayout();
  			//scroll.validate();//ICI-GUI
  			scroll.repaint();//ICI-GUI
  			//scroll.splitTableAccordingToColmnsFreezed();
  			*/
  		}
  	}
  }
  
  public void setScrollpane(FixedColumnScrollPane scrollpane) {
    this.scrollpane = scrollpane;
  }
  
  public FixedColumnScrollPane getScrollPane() {
    if(scrollpane == null){
      scrollpane = new FixedColumnScrollPane(this);
      // Dimension size = new Dimension(500,500);
      // this.setPreferredSize(size);
      /*
       * this.setPreferredScrollableViewportSize(size);
       * this.setMinimumSize(size);
       */

      //scrollpane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    }
    return scrollpane;
  }

  public FocObject getElementAt(int i) {
    FocObject ret = null;
    FAbstractTableModel ftm = (FAbstractTableModel) getModel();
    if(ftm != null){
      ret = ftm.getRowFocObject(i);
    }
    return ret;
  }

  public TableColumn getTableColumn(int colID) {
    TableColumn         col        = null;
    TableColumnModel    colModel   = this.getColumnModel();
    FAbstractTableModel tableModel = (FAbstractTableModel) getModel();
    FTableView          tableView  = tableModel != null ? tableModel.getTableView() : null;

    if (tableView != null) {
      int sellAssIndex = tableView.getColumnIndexForId(colID);
      if (sellAssIndex >= 0) {
        if (colModel != null) {
          col = colModel.getColumn(sellAssIndex);
        }
      }
    }

    return col;
  }

  public void addSelectionListener(ListSelectionListener selectionListener) {
    if (selectionListeners == null) {
      selectionListeners = new ArrayList<ListSelectionListener>();
    }
    selectionListeners.add(selectionListener);
  }

  public void removeSelectionListener(ListSelectionListener selectionListener) {
    if (selectionListeners != null) {
      selectionListeners.remove(selectionListener);
    }
  }
  
  public void notifySelectionListeners(ListSelectionEvent e){
    if(selectionListeners != null){
      for (int i = 0; i < selectionListeners.size(); i++) {
        ListSelectionListener selectionListener = (ListSelectionListener) selectionListeners.get(i);
        if (selectionListener != null) {
          selectionListener.valueChanged(e);
        }
      }
    }
  }

  /*
  public void addFocMouseListener(FTableMouseListener selectionListener) {
    if (mouseListeners == null) {
    	mouseListeners = new ArrayList<FTableMouseListener>();
    }
    mouseListeners.add(selectionListener);
  }

  public void removeFocMouseListener(FTableMouseListener selectionListener) {
    if (mouseListeners != null) {
      mouseListeners.remove(selectionListener);
    }
  }
  
  public void notifyFocMouseListeners(MouseEvent e){
    if(mouseListeners != null){
      for (int i = 0; i < mouseListeners.size(); i++) {
        FTableMouseListener mouseListener = (FTableMouseListener) mouseListeners.get(i);
        if (mouseListener != null) {
        	mouseListener.valueChanged(e);
        }
      }
    }
  }
  */

  public void addCellStartEditingListener(FTableCellStartEditingListener listener){
    if(cellStartEditingListeners == null){
      cellStartEditingListeners = new ArrayList<FTableCellStartEditingListener>();
    }
    cellStartEditingListeners.add(listener);
  }
  
  public void removeCellStartEditingListener(FTableCellStartEditingListener listener){
    if(cellStartEditingListeners != null){
      cellStartEditingListeners.remove(listener);
    }
  }
  
  public boolean requestToEditCell(){
    FocObject currentFocObject = null;
    int row = getSelectedRow();
    int col = getSelectedColumn();
    FAbstractTableModel tableModle = getTableModel();
    FProperty currentProperty = null;
    boolean result = true;
    
    if(row >= 0){
      currentFocObject = getElementAt(row);
      if(col >0){
        currentProperty = tableModle.getFProperty(row, col);
      }
    }
    if(cellStartEditingListeners != null){
      for(int i = 0; i< cellStartEditingListeners.size() && result; i++){
        FTableCellStartEditingListener listener = (FTableCellStartEditingListener)cellStartEditingListeners.get(i);
        result = listener.requestToEditCell(currentFocObject,currentProperty);
      }
    }
    return result;
  }

  /*public FocList getFocList(){
    FTableModel model = (FTableModel) getModel();
    return model != null ? model.getFocList() : null;
  }*/
  
  public FocList getFocList(){
    FocList list = null;
    FAbstractTableModel model = (FAbstractTableModel) getModel();
    //if(model instanceof FTableModel){
    //  FTableModel fTableModel = (FTableModel)model;
    list = model.getFocList();
    return list;
  }
  
  public void setSelectedObject(FocObject selectedObject) {
  	if(getFocList() != null){
	    int row = getFocList().getRowForObject(selectedObject);
	    if(row >= 0 && row < getRowCount()){
	      setRowSelectionInterval(row, row);
	    }else{
	      Globals.logString("setSelectedObject : Could not find row for object");
	    }
  	}
  }

  /**
	 * Overridden to pass the new rowHeight to the tree.
	 */
	public void setRowHeight(int rowHeight) {
		super.setRowHeight(rowHeight);
		FAbstractTableModel model = (FAbstractTableModel) getModel();
		if(model != null){
			model.setRowHeight(rowHeight);
		}
	}

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
   */
  public void valueChanged(ListSelectionEvent e) {
    super.valueChanged(e);
    
    if(scrollpane != null && scrollpane.getViewport() != null){
      //Dimension d = getSize();
    	int itemCount   = getModel().getRowCount();
    	int totalHeight = itemCount * getRowHeight();
    	int selRow      = getSelectedRow();
    	
      if(selRow > 0 && totalHeight > scrollpane.getViewport().getHeight()){
        //double portion = (double)totalHeight * (double)selRow / (double)itemCount;
        int selectedHeight = selRow * getRowHeight();
        int selectedHead = selectedHeight;        
        
        if(totalHeight - selectedHeight < scrollpane.getViewport().getHeight()){
        	selectedHead = totalHeight - scrollpane.getViewport().getHeight();
        }
        
        Point currentHeadPoint = scrollpane.getViewport().getViewPosition();
        
        //If the height I want to set is out of the screen then do the setting otherwise no need 
       
        newSelectedViewportHeight = selectedHead;
        
        if(selectedHeight < currentHeadPoint.getY() || selectedHeight > currentHeadPoint.getY()+scrollpane.getViewport().getHeight()){ 
          //scrollpane.getViewport().setViewPosition( new Point(0, selectedHeight));
        	if(!isInvokeLaterForViewpositionAdjustmentAlreadyCalled()){
	        	SwingUtilities.invokeLater(new Runnable(){
							public void run() {
								if(scrollpane != null && scrollpane.getViewport() != null){
									Point viewPos = scrollpane.getViewport().getViewPosition();
				        	//HEADER_GRID
									boolean skip = getTableView() != null && getTableView().isDisableAutoAdjustViewportPosition(); 
									if(!skip){
										scrollpane.getViewport().setViewPosition( new Point(viewPos.x, newSelectedViewportHeight/*+ getRowHeight()*/));
									}
								}
								setInvokeLaterForViewpositionAdjustmentAlreadyCalled(false);
							}
	        	});
        	}
        }
      }
    }
  }
    
  public boolean isInvokeLaterForViewpositionAdjustmentAlreadyCalled() {
		return invokeLaterForViewpositionAdjustmentAlreadyCalled;
	}

	public void setInvokeLaterForViewpositionAdjustmentAlreadyCalled(boolean invokeLaterForViewpositionAdjustmentAlreadyCalled) {
		this.invokeLaterForViewpositionAdjustmentAlreadyCalled = invokeLaterForViewpositionAdjustmentAlreadyCalled;
	}

	public Point getRowLocation(int row){
  	Point absScrollPt  = scrollpane.getLocationOnScreen();
  	Point viewPortPos  = scrollpane.getViewport().getViewPosition();
  	int   headerHeight = getTableHeader().getHeight();
  	return new Point((int) (viewPortPos.getX()+absScrollPt.getX() + 5), (int) (row * getRowHeight() - viewPortPos.getY() + absScrollPt.getY() + headerHeight + 5));
  }
  
  public void editingCanceled(ChangeEvent e) {
    //requestFocusInWindow();    
    super.editingCanceled(e);
  }
  
  public void editingStopped(ChangeEvent e) {
    //requestFocusInWindow();    
    super.editingStopped(e);
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        invokeLaterFct();              
      }
    });
  }
  
  public void invokeLaterFct(){
    if(!hasFocus()){
      reactToFocusChange(true);
    }
  }
  
  /*
   * public boolean editCellAt(int row, int column, java.util.EventObject e) { //
   * Globals.logString("Calling editCellAt(E) " + row + " " + column);
   * 
   * requestFocus(); if(isEditing()) editingStopped(new ChangeEvent(this)); if(!
   * isRowSelected(row)) { setRowSelectionInterval(row, row); } if(!
   * isColumnSelected(column)) setColumnSelectionInterval(column, column);
   * boolean res = super.editCellAt(row, column, e); return res; }
   */

  // If I override this I will get the cell edited automatically each time I
  // move to the cell
  // I will not be able to move arround in the JTable
  /*
   * public void changeSelection(final int row, final int column, boolean
   * toggle, boolean extend){ super.changeSelection(row, column, toggle,
   * extend);
   * 
   * int selRow = getSelectedRow(); int selCol = getSelectedColumn(); FocObject
   * focObj = (FocObject) getElementAt(row); if (focObj != null) { FTableModel
   * tableModel = (FTableModel) getModel(); FTableView tableView =
   * tableModel.getTableView(); FTableColumn fColumn =
   * tableView.getColumnAt(selCol); if(fColumn != null){ CellEditorInterface
   * cellEditor = fColumn.getCellEditor(); if(cellEditor != null){
   * cellEditor.reactToChangeSelection(this, tableModel, fColumn, selRow,
   * selCol, toggle, extend); } }
   * 
   * //if(fColumn.isComboBoxRenderer()){ // if (editCellAt(selRow, selCol)){ //
   * getEditorComponent().requestFocusInWindow(); // } //} } }
   */

  public boolean editCellAt(int row, int column, EventObject e) {
    
    boolean result = false;
    boolean doProcess = true;
       
    if (doProcess) {
      result = super.editCellAt(row, column, e);
      final Component editor = getEditorComponent();

      if (editor != null){
      	if(editor instanceof JTextComponent) {
      		((JTextComponent) editor).selectAll();	
      	}else if(editor instanceof FGObjectComboBox) {
      		
      		if(e instanceof KeyEvent){
      			FGObjectComboBox comboBox = (FGObjectComboBox) editor;
      			/*
      			try{
							//((FGComboAutoCompletion)comboBox.getAutoCompletion()).insertString(0, String.valueOf(((KeyEvent)e).getKeyChar()), null);
      				((FGComboAutoCompletion)comboBox.getAutoCompletion()).insertString(0, String.valueOf(((KeyEvent)e).getKeyChar()), null);
						}catch (BadLocationException e1){
							Globals.logException(e1);
						}
						*/
      			//SwingUtilities.invokeLater(new TempInvoqueLater(comboBox, (KeyEvent)e));
      			//comboBox.dispatchEvent((KeyEvent)e);
      		}
/*      			
      			((FGComboAutoCompletion)comboBox.getAutoCompletion()).k
      		}
      		editor.
      		public void keyPressed(KeyEvent e)
      		*/ 
      		//SwingUtilities.invokeLater(new TempInvoqueLater(row, column, e));
      	}
      }
        
    }
    return result;
  }
  
  public class TempInvoqueLater implements Runnable{
  	private FGObjectComboBox comboBox = null;
  	private KeyEvent e = null;
  	
  	public TempInvoqueLater(FGObjectComboBox comboBox, KeyEvent e){
  		this.comboBox = comboBox;
  		this.e = e;
  	}
  	
		@Override
		public void run() {
			//FTable.this.dispatchEvent((KeyEvent)e);
			try{
				Thread.sleep(1000);
				Globals.getDisplayManager().getMainFrame().dispatchEvent(e);
			}catch (InterruptedException e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			/*
			try{
				//((FGComboAutoCompletion)comboBox.getAutoCompletion()).insertString(0, String.valueOf(((KeyEvent)e).getKeyChar()), null);
				((FGComboAutoCompletion)comboBox.getAutoCompletion()).insertString(0, String.valueOf(((KeyEvent)e).getKeyChar()), null);
			}catch (BadLocationException e1){
				Globals.logException(e1);
			}
			*/

		}
  }

  //We overwrite this method only to implement the reaction to a key typing in a JTable to find the first line that matches 
  protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {

    if(!isCellEditable(getSelectedRow(), getSelectedColumn()) && pressed && condition == 0 && e != null){
      if(e.getModifiers() == 0){
        e.getKeyChar();
        
        int foundRow = -1;
        int col = getSelectedColumn();
        int row = getSelectedRow();
        if(row >= 0 && row+1 < getRowCount()){
          for(int i=row+1; i<getTableModel().getRowCount() && foundRow < 0; i++){
            Object obj = getTableModel().getValueAt(i, col);
            if(obj instanceof String){
              String str = (String) obj;
              if(str.length() > 0 && Character.toUpperCase(e.getKeyChar()) == Character.toUpperCase(str.charAt(0))){
                foundRow = i;
              }            
            }
          }
          for(int i=0; i<row && foundRow < 0; i++){
            Object obj = getTableModel().getValueAt(i, col);
            if(obj instanceof String){
              String str = (String) obj;
              if(str.length() > 0 && Character.toUpperCase(e.getKeyChar()) == Character.toUpperCase(str.charAt(0))){
                foundRow = i;
              }            
            }
          }
        }
        
        if(foundRow >= 0){
          setRowSelectionInterval(foundRow, foundRow);
        }
      }
    }      
    return super.processKeyBinding(ks, e, condition, pressed);
  }
  
  /* (non-Javadoc)
   * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
   */
  public void focusGained(FocusEvent e) {
    //setColumnSelectionInterval(0, 0);
    //setRowSelectionInterval(0, 0);
  }

  /* (non-Javadoc)
   * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
   */
  public void focusLost(FocusEvent e) {//d'une cellule
  	//BAntoineS - Apparently there is no need any more for this condition. I have tried it on java 6 and 5
  	//I think this was put initially for starting the edit of Combo box cells or text cells.
  	//if(!isEditing() && !e.isTemporary()){
    reactToFocusChange(true);
    //}
  }

  public int getEditingColumn(){
  	return editingColumn;
  }

  //------------------------------------------------------------- 
  //-------------------------------------------------------------
  //Mouse events
  //-------------------------------------------------------------
  //-------------------------------------------------------------

  private int currentMouseRow = -1;
  
  public int getCurrentMouseRow(){
    return currentMouseRow;
  }

  public void setCurrentMouseRow(int row){
    if(this.currentMouseRow != row){
      FTableModel model = (FTableModel) getModel();
      model.fireTableRowsUpdated(0, model.getRowCount());
      this.currentMouseRow = row;
    }
  }
  
  public Point getCellCoordinatesForMouseCurrentPosition(){
    Point pt = getMousePosition();
    int col = -1;
    int row = -1;      
    
    for(int i=0; i<getColumnCount() && col < 0; i++){
      Rectangle rect = getCellRect(0, i, true);
      //Globals.logString("rect "+rect + "point " + pt);
      if(rect != null && pt != null){
        if(rect.x <= pt.x && rect.x+rect.width >= pt.x){
          col = i;
        }
      }
    }
    
    for(int i=0; i<getRowCount() && row < 0; i++){
      Rectangle rect = getCellRect(i, 0, true);
      if(rect != null && pt != null){
        if(rect.y <= pt.y && rect.y+rect.height >= pt.y){
          row = i;
        }
      }
    }
    return new Point(col, row);
  }
  
  //Dragable
  public void setTransferableObjectCompleter(FocTransferableObjectCompleter transferableObjectCompleter){
    this.transferableObjectCompleter = transferableObjectCompleter;
  }

  public void fillTransferableObject(FocTransferable focTransferable) {

    FTable table = (FTable)focTransferable.getSourceComponent();
    int selectedRow = table.getSelectedRow();
    FAbstractTableModel model = table.getTableModel();
    /*This is done by the modle
    FocObject sourceFocObject = model.getRowFocObject(selectedRow);
    focTransferable.setSourceFocObject(sourceFocObject);*/
    Point p = getCellCoordinatesForMouseCurrentPosition();
    focTransferable.setTableSourceColumn(p.x);
    //We could get the sourceRow form p also (p.Y) but we keep it like this.
    focTransferable.setTableSourceRow(selectedRow);
    //The call of model.fillSpecificDragInfo(focTransferable) shoul be after 
    //focTransferable.setTableSourceRow(selectedRow) because model.fillSpecificDragInfo(focTransferable) 
    //needs the selectedRow to fill the sourceFocObject.
    model.fillSpecificDragInfo(focTransferable);
    
    
    FocList sourceFocList = table.getFocList();
    focTransferable.setSourceFocList(sourceFocList);
    /*if(sourceFocList != null){
      focTransferable.setSourceFocObject(sourceFocList.getSelectedObject());
      FListPanel listPanel = sourceFocList.getAttachedListPanel();
      int initialPosition = listPanel.getTable().getSelectedRow();
      focTransferable.setFocObjectInitialPosition(initialPosition);
    }*/
    
  
    if(this.transferableObjectCompleter != null){
      this.transferableObjectCompleter.completeFillingTransferableObject(focTransferable);
    }
  }
  
  //Dropable
  
  public boolean isDropable(){
    return this.dropable;
  }
  
  public void setDropable(boolean dropable){
    this.dropable = dropable;
    if(dropable){
      FocDefaultDropTargetListener defaultDropTargetListener = FocDefaultDropTargetListener.getInstance();
      setDropable(defaultDropTargetListener);
    }
  }
  
  public void setDropable(DropTargetListener dropTargetListener){
    initDrop(dropTargetListener);
  	if(scrollpane != null && scrollpane.getFixedTable() != null && !isFixed() && this != scrollpane.getFixedTable()){
  		scrollpane.getFixedTable().setDropable(dropTargetListener);
  	}
  }
  
  public void fillDropInfo(FocTransferable focTransferable, DropTargetDropEvent dtde){
  	Point point = getCellCoordinatesForMouseCurrentPosition();
    focTransferable.setTableTargetRow(point.y);
    focTransferable.setTableTargetColumn(point.x);
    FAbstractTableModel tableModel = getTableModel();
    if(tableModel != null){
    	focTransferable.setTargetFocObject(tableModel.getRowFocObject(point.y));
    }
  }
  
  public boolean shouldExecuteDrop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
  	return true;
	}
  
  public boolean drop(FocTransferable focTransferable, DropTargetDropEvent dtde){
    boolean accepted = false;
    if(isDropable()){
      try{
        //Transferable transferable = dtde.getTransferable();
        //FocTransferable fTransferable = (FocTransferable) dtde.getTransferable();
      	
        /*Transferable transferable = dtde.getTransferable();
        FocTransferable focTransferable = (FocTransferable)transferable.getTransferData(FocTransferable.getFocDataFlavor());*/
        Point point = getCellCoordinatesForMouseCurrentPosition();
        /*focTransferable.setTableTargetRow(point.y);
        focTransferable.setTableTargetColumn(point.x);*/
        FocList targetList = getFocList();
        FocList sourceList = focTransferable.getSourceFocList();
        if(sourceList != null){
	        FocObject sourceFocObject = focTransferable.getSourceFocObject();
	        
	        if(targetList != null && targetList != sourceList){
	          if(sourceFocObject != null){
	            if(targetList.getFocDesc() == sourceFocObject.getThisFocDesc()){
	              targetList.add(sourceFocObject);
	              accepted = true;
	            }
	          }
	        }else{
	          int initialPosition = focTransferable.getTableSourceRow();
	          //Point point = getCellCoordinatesForMouseCurrentPosition();
	          int finalPosition  = point.y;
	          //int finalPosition  = focTransferable.getTableTargetRow();
	          if(initialPosition >= 0 && finalPosition >= 0 ){
	            sourceList.elementMoved(initialPosition, finalPosition);
	          }
	        }
        }
      }catch(Exception e){
        Globals.logException(e);
      }
    }
    return accepted;
  }

  public boolean isDisableSelectionListeners() {
    return disableSelectionListeners;
  }

  public void setDisableSelectionListeners(boolean disableSelectionListeners) {
    this.disableSelectionListeners = disableSelectionListeners;
  }

  public boolean isFixed() {
    return fixed;
  }

  /*
   * 888888888888888888888888888888888888888888888888888888888888888888888888888888888
   * 888888888888888888888888888888888888888888888888888888888888888888888888888888888
   * 888888888888888888888888888888888888888888888888888888888888888888888888888888888
   * 888888888888888888888888888888888888888888888888888888888888888888888888888888888
   *
   *  VERY IMPORTANT THIS CODE WORKS, IT ALLOWS TO COLOR THE ROW THE MOUSE 
   *  IS CURRENTLY ON 
   * 
   * 888888888888888888888888888888888888888888888888888888888888888888888888888888888
   * 888888888888888888888888888888888888888888888888888888888888888888888888888888888
   * 888888888888888888888888888888888888888888888888888888888888888888888888888888888
   * 888888888888888888888888888888888888888888888888888888888888888888888888888888888
   * 
   * 
   * 
   * 
   // FTable should implement MouseMotionListener
    * 
   public void attachMouseListener(){
    Globals.logString("Attach listener");
    addMouseMotionListener(this);
  }

  private synchronized void mousePositionEvent(MouseEvent e){
    Point p = e.getPoint();
    
    int row = rowAtPoint(p);
    if(currentMouseRow != row && row >= 0){
      FTableModel model = (FTableModel) getModel();
      //FocList list = model.getFocList();
      //FocObject oldObj = list.getFocObject(currentMouseRow);
      //FocObject newObj = list.getFocObject(row);      
      currentMouseRow = row;
      if(currentMouseRow >= 0){
        //model.fireTableRowsUpdated(Math.min(row, currentMouseRow), Math.min(row, currentMouseRow));
        model.fireTableRowsUpdated(0, model.getRowCount());
      }
    }    
  }

  public void mouseDragged(MouseEvent e) {
    mousePositionEvent(e);
  }

  public void mouseMoved(MouseEvent e) {
    mousePositionEvent(e);
  }
  */  
/*  public void tableChanged(TableModelEvent e) {
    System.out.println("dans table changed");
    isRepating = true;
    if (isRepating){
    super.tableChanged(e);
    }
  }*/
  
  @Override
  public void setPreferredSize(Dimension dim){
  	super.setPreferredSize(dim);
  }

  @Override
  public int convertColumnIndexToModel(int viewColumnIndex) {
  	//try{
  	//	return getTableView().convertColumnIndexToModel(viewColumnIndex);	
  	//}catch(Exception e){
  		return super.convertColumnIndexToModel(viewColumnIndex);
  	//}
	}

  @Override
	public int convertColumnIndexToView(int modelColumnIndex) {
  	//try{
  	//	return getTableView().convertColumnIndexToView(modelColumnIndex);	
  	//}catch(Exception e){
  		return super.convertColumnIndexToView(modelColumnIndex);
  	//}
	}  
  
  /*
  private Dimension preffDim = new Dimension(); 
  public Dimension getPreferredSize(){
  	if(dim == null){
  		return super.getPreferredSize();
  	}else{
	  	preffDim.width = dim.width;
	  	preffDim.height = dim.height;
	  	return preffDim;
  	}
  }
  */
  
  public void focPrint(PrintMode mode) throws Exception {
	  MessageFormat footerFormat = new MessageFormat("- {0} -");
	  print(mode, null, footerFormat);
  }
  
  public Printable getPrintable(PrintMode printMode, MessageFormat headerFormat, MessageFormat footerFormat) {
		return new FTablePrintable(this, printMode, headerFormat, footerFormat);
  }

  /*
	@Override
	public void print(Graphics g) {
		super.print(g);
		FPrintParameters fPrintParams = getPrintParameters();
		if(fPrintParams != null){
			//if(fPrintParams.isFitWidth()){
				g.scale(0.5, 0.5);
			//}
		}
	}
	*/
	
	public FTablePrintParameters getPrintParameters() {
		if(printParameters == null){
			printParameters = new FTablePrintParameters();
		}
		return printParameters;
	}

	public void setPrintParameters(FTablePrintParameters printParameters) {
		this.printParameters = printParameters;
	}
	
	public int getMaxColumnWidth(){
		return maxColWidth;
	}

	public boolean isShowFormulaCellTag() {
		return showFormulaCellTag;
	}

	public void setShowFormulaCellTag(boolean showFormulaCellTag) {
		this.showFormulaCellTag = showFormulaCellTag;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(getTableOverPainter() != null){
			getTableOverPainter().paint(this, g);
		}
		/*
		g.setColor(Color.RED);
		g.drawRect(0, 0, 200, 200);
		*/
	}

	public ITableOverPaint getTableOverPainter() {
		return tableOverPainter;
	}

	public void setTableOverPainter(ITableOverPaint tableOverPainter) {
		this.tableOverPainter = tableOverPainter;
	}
	
	//-----------------------------------------------------------------
	//-----------------------------------------------------------------
	// CSV EXPORT
	//-----------------------------------------------------------------
	//-----------------------------------------------------------------

	public void csv_Export(){
		FString       filePathProperty = new FString(null, 1, "");
		FGFileChooser fileChooser      = new FGFileChooser(filePathProperty, Globals.getDisplayManager().getMainFrame(), "Select an export CSV file", FGFileChooser.MODE_FILES_ONLY, "csv", "Comma Seperated Values");
		fileChooser.popupFileChooser();
		String        fileName         = filePathProperty.getString();
		if(fileName != null){
			if(!fileName.endsWith(".csv")) fileName += ".csv";
			File        file   = new File(fileName);
			PrintStream stream = null;
			try{
				file.createNewFile();
				stream = new PrintStream(file);
			}catch (IOException e){
				Globals.logException(e);
			}
			
			TableModel tableModel = getTableModel();
			for(int i=0; i<tableModel.getColumnCount(); i++){
				if(i > 0) stream.print(",");
				String str = tableModel.getColumnName(i);
				str = str.replace("|", " ");
				if(str.contains(",")) str = "\"" + str + "\"";  
				stream.print(str);
			}
			
			for(int r=0; r<tableModel.getRowCount(); r++){
				stream.println();
				for(int i=0; i<tableModel.getColumnCount(); i++){
					if(i > 0) stream.print(",");
					Object valueAt = tableModel.getValueAt(r, i);
					String str = valueAt != null ? valueAt.toString() : "";
					if(str.contains(",")) str = "\"" + str + "\"";  
					stream.print(str);
				}
			}
			
			if(stream != null){
				stream.flush();
				stream.close();
				stream = null;
			}
		}
	}
	
	//-----------------------------------------------------------------
}