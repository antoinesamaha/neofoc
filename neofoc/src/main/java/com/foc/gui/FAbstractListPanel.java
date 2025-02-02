/*
 * Created on 24 fevr. 2004
 */
package com.foc.gui;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.foc.*;
import com.foc.admin.GrpViewRightsDesc;
import com.foc.business.printing.gui.PrintingAction;
import com.foc.business.printing.gui.PrnPopupMenu;
import com.foc.desc.*;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.gui.actions.CopyAction;
import com.foc.gui.actions.CopyPasteContent;
import com.foc.gui.actions.FilterAction;
import com.foc.gui.actions.FindAction;
import com.foc.gui.actions.PasteAction;
import com.foc.gui.findObject.FindObjectGuiDetailsPanel_ForFooter;
import com.foc.gui.plugs.FocGuiPlugs;
import com.foc.gui.table.*;
import com.foc.gui.table.view.UserView;
import com.foc.gui.table.view.UserViewDesc;
import com.foc.list.FocList;
import com.foc.list.filter.FocListFilter;
import com.foc.property.FProperty;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public abstract class FAbstractListPanel extends FPanel implements Cloneable, MouseListener {
  
  public abstract FGButtonAction getNewAction();
  public abstract FGButtonAction getDeleteAction();
  public abstract FGButtonAction getEditAction();
  public abstract FGButtonAction getSelectAction();
  public abstract FGButtonAction getEditCellAction();
  public abstract FGButtonAction getDuplicateAction();
  public abstract FGButtonAction getFilterAction();
  public abstract FGButtonAction getColumnSelectorAction();
  //public abstract FGButtonAction getPrintAction();
  public abstract FGButtonAction getRedirectAction();
  public abstract FGButtonAction getMoveUpAction();
  public abstract FGButtonAction getMoveDownAction();
  public abstract void 					 editCurrentItem();
  public abstract void 					 editCurrentItem(FocObject focObject);

  private FGButtonAction exportAction    = null;
  private FGButtonAction printViewAction = null;
  
  public FPanel newDetailsPanel(FocObject focObject, int viewID){
    return null;
  }
  
  public final static String BUTTON_INSERT          = "INSERT";
  public final static String BUTTON_DELETE          = "DELETE";
  public final static String BUTTON_EDIT            = "EDIT";
  public final static String BUTTON_DUPLICATE       = "DUPLICATE";
  public final static String BUTTON_FILTER          = "FILTER";
  public final static String BUTTON_COLUMN_SELECTOR = "COL_SEL";
  public final static String BUTTON_PRINT           = "PRINT";
  
  private static final String POPUP_LABEL_DUPLICATE  = "Duplicate";
  private static final String POPUP_LABEL_EDIT       = "Edit";
  private static final String POPUP_LABEL_DELETE     = "Delete";
  private static final String POPUP_LABEL_ADD        = "Add";
  private static final String POPUP_LABEL_REDIRECT   = "Redirect";
  private static final String POPUP_LABEL_MOVE_DOWN  = "Move Down";
  private static final String POPUP_LABEL_MOVE_UP    = "Move Up";
  private static final String POPUP_LABEL_PRINT_LIST = "Print list ...";
  private static final String POPUP_LABEL_CSV_EXPORT = "CSV export ...";
  
  protected FAbstractTableModel fTableModel = null;
  protected FTable              fTable      = null;
  
  protected FButtonsPanel buttonsPanel = null;
  private   FPanel        totalsPanel  = null;

  private FGButton insert         = null;
  private FGButton edit           = null;
  private FGButton delete         = null;
  private FGButton select         = null;
  private FGButton cancel         = null;
  private FGButton duplicate      = null;
  private FGButton filter         = null;
  private FGButton columnSelector = null;
  private FGButton print          = null;
  
  //private FObjectPanel viewComboBox = null;
  private FGObjectComboBox viewComboBox = null;
  
  //private FGButton redirect = null;
  
  private FGButtonAction printAction = null;
  //private FGButtonAction duplicateAction = null;
  
  private boolean uniquePoopUp = true;
  private HashMap<FocObject, InternalFrame> objectsInternalFramesMap = null;
  
  private static final String ACTION_MAP_KEY_COPY   = "copy"; 
  private static final String ACTION_MAP_KEY_PASTE  = "paste";
  private static final String ACTION_MAP_KEY_FIND   = "find";
  private static final String ACTION_MAP_KEY_FILTER = "filter";
  
  private CopyPasteContent copyPasteContent = null;
  private CopyAction       copyAction       = null;
  private PasteAction      pasteAction      = null;
  private FindAction       findAction       = null;
  private FilterAction     filterAction     = null;
  private String           prnContextName   = null;
  
  private FindObjectGuiDetailsPanel_ForFooter filterExpressionPanel = null;
  
  public FAbstractListPanel() {
    setInsets(0, 0, 0, 0);
  }

  public FAbstractListPanel(String frameTitle, int frameSizing, int panelFill) {
  	super(frameTitle, frameSizing, panelFill);
    setInsets(0, 0, 0, 0);
  }
    
  public FAbstractListPanel(String panelTitle, int panelFill) {
  	super(panelTitle, panelFill);
    setInsets(0, 0, 0, 0);  	
  }
  
  public void dispose(){
  	if(filterExpressionPanel != null){
  		filterExpressionPanel.removeFind();
  		filterExpressionPanel = null;
  	}

    super.dispose();
        
    FTableView tableView = getTableView();
    if(tableView != null){
      FocListFilter listFilter = tableView.getFilter();
      if(listFilter != null){
        listFilter.setSelectionPanel(null);  
      }
    }
    
    if(fTableModel != null){
      fTableModel.dispose();
      fTableModel = null;
      fTable = null;
    }     
    
    if(buttonsPanel != null){
      buttonsPanel.dispose();
      buttonsPanel = null;
    }
    
    if(totalsPanel != null){
      totalsPanel.dispose();    
      totalsPanel = null;
    }
    
    insert = null;
    edit = null;
    delete = null;
    select = null;
    cancel = null;
    duplicate = null;
    filter = null;
    columnSelector = null;
    print = null;
    //expandAll = null;
    //collapseAll = null;
    //redirect = null;
    //duplicateAction = null;
    printAction = null;
    
    if(copyAction != null){
      copyAction.dispose();
      copyAction = null;
    }
    
    if(pasteAction != null){
      pasteAction.dispose();
      pasteAction = null;
    }
    
    if(copyPasteContent != null){
      copyPasteContent.dispose();
      copyPasteContent = null;
    }
    
    if(findAction != null){
      findAction.dispose();
      findAction = null;
    }

    if(filterAction != null){
      filterAction.dispose();
      filterAction = null;
    }
    
    viewComboBox = null;
    
    HashMap<FocObject, InternalFrame> map = getObjectsInternalFramesMap();
    if(map != null){
    	map.clear();
    	map = null;
    }
    /*
    private HashMap<FocObject, InternalFrame> objectsInternalFramesMap = null;
     */  
  }

  public Object clone() throws CloneNotSupportedException {
    FAbstractListPanel abstractListPanel = (FAbstractListPanel) super.clone();
    FAbstractTableModel abstractTableModel = abstractListPanel.getTableModel();
    abstractTableModel = (FAbstractTableModel)abstractTableModel.clone();
    abstractListPanel.setTableModel(abstractTableModel);
    return abstractListPanel;
  }
    
  public void popup(FocObject currElement, boolean modalDialog) {
    refreshList();
    
    //Globals.getDisplayManager().popupDialog(this, this.getFrameTitle(), true);
    //this.setLightWeight(true);
    if(Globals.getDisplayManager().getActiveDialog() != null){
    	Globals.getDisplayManager().popupDialog(this, "", true);
    }else{
    	Globals.getDisplayManager().changePanel(this);
    }
  }
  
  public InternalFrame popup(FocObject currElement) {
    refreshList();
    return Globals.getApp().getDisplayManager().newInternalFrame(this);//, this.getTitle(), true);
  }
  
  public void refreshList() {
    FocList focList = getFocList();
    if (focList != null) {
      //getTableModel().resetListListenerToCellPropertiesMoifications();
      //memory SHOULD BE loadIFNotLoaded otherwize we reload the clients each time
      focList.reloadFromDB();
    }
  }
  
  public boolean allowDeletingThisObject(FocObject focObj){
  	boolean allow = focObj != null ? focObj.workflow_IsAllowDeletion() : false;
  	if(!allow) Globals.getDisplayManager().popupMessage("You don't have the rights to delete this transaction.");
  	return allow;
  }
  
  public Boolean isUniquePoopUp(){
    return uniquePoopUp;
  }
  
  public void setUniquePoopUp(Boolean unique){
    uniquePoopUp = unique;
  }
  
  public HashMap<FocObject, InternalFrame> getObjectsInternalFramesMap(){
    if (objectsInternalFramesMap == null){
      objectsInternalFramesMap  = new HashMap<FocObject, InternalFrame>();
    }
    return objectsInternalFramesMap ;
  }

  protected Boolean shouldCreateNewInternalFrame(Object o){
    boolean shouldCreateNewInternalFrame = true;
    if (isUniquePoopUp()){
     InternalFrame internal = getObjectsInternalFramesMap().get(o);
      if (internal  != null){
        shouldCreateNewInternalFrame = false;
      }
    }
    return shouldCreateNewInternalFrame;
  }
  
  protected void associateInternalFrameToObject(FocObject o, InternalFrame internal){
    getObjectsInternalFramesMap().put(o, internal);
  }
  
  protected InternalFrame getInternalFrameAssociatedToObject(FocObject o){
    return getObjectsInternalFramesMap().get(o);
  }
  
  public void construct(){
    FocListFilter listFilter = getTableView().getFilter();
    if(listFilter != null){
      listFilter.setSelectionPanel(this);  
    }
    
    fTable = new FTable(fTableModel);
    if(addMouseListener()){
      fTable.addMouseListener(this);
    }
    
    addTable();
    
    addButtonsAndActions();
    
    PrintingAction printingAction = getFocList().getFocDesc().newPrintingAction();
		if(printingAction != null){
			PrnPopupMenu menu = new PrnPopupMenu(this);
			getTable().getPopupMenu().add(menu);
			printingAction.dispose();
		}
		
    FocGuiPlugs.getInstance().callTableAfterConstruct(this);
  }

  protected void addTable(){
    add(fTable.getScrollPane(), 0, 1, 2, 1, 0.99, 0.99, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
  }
  
  public FindObjectGuiDetailsPanel_ForFooter addFilterExpressionPanel(){
  	if(filterExpressionPanel == null){
  		filterExpressionPanel = new FindObjectGuiDetailsPanel_ForFooter(this);
  		FTableView tableView = getTableView();
  		if(tableView != null){
  			for(int i=0; i<tableView.getColumnCount(); i++){
  				FTableColumn tCol = tableView.getColumnAt(i);
  				FFieldPath   path = tCol.getFieldPath();
  				if(path != null){
	  				FField fld = path.getFieldFromDesc(getFocList().getFocDesc());
//	  				if(fld != null){// && fld.getSqlType() == Types.VARCHAR){
	  					filterExpressionPanel.addField(path);
//	  				}
  				}
  			}
  		}
  		getButtonsPanel().addComponent_Before(filterExpressionPanel);
  		setFilterKeyStrokeAction();
  		getButtonsPanel().readjustSize();
  		filterExpressionPanel.executeFind();
  	}
  	return filterExpressionPanel; 
  }

  public FindObjectGuiDetailsPanel_ForFooter getFilterExpressionPanel(){
  	return filterExpressionPanel;
  }
  
  private void addButtonsAndActions(){
    setDefaultCopyPasteKeyStrokeActions();
    setFindObjectKeyStrokeAction();
    
    // Creating buttons panel for Insert Delete Ok ...
    FButtonsPanel buttonsPanel = getButtonsPanel();
    buttonsPanel.setInsets(0, 0, 0, 0);

    getTable().getActionMap().put("celledit", getEditCellAction());
    getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getCellEditStroke(), "celledit");
    
    // -----------------------
    // Column selector Button
    // -----------------------
    FGButtonAction columnSelectorAction = getColumnSelectorAction();
    if(columnSelectorAction != null){
	    columnSelector = new FGButton(Globals.getIcons().getColumnSelectorIcon());
	    StaticComponent.setComponentToolTipText(columnSelector, "View selection");
	    columnSelector.setName(getButtonName(BUTTON_COLUMN_SELECTOR));
	    columnSelectorAction.setAssociatedButton(columnSelector);
	    columnSelector.addActionListener(columnSelectorAction);
	    
	    UserView view = getTableView().getUserView();
	    if(view != null){
	    	FProperty prop = view.getFocProperty(UserViewDesc.FLD_VIEW);
	    	/*viewComboBox = (FObjectPanel) prop.getGuiComponent();
	    	viewComboBox.setSelectButtonVisible(false);
	    	*/
	    	viewComboBox = (FGObjectComboBox) prop.getGuiComponent();
	    	if(getTableView().getGrpViewRights() != null && getTableView().getGrpViewRights().getRight() == GrpViewRightsDesc.ALLOW_NOTHING){
	    		viewComboBox.setEnabled(false);
	    	}
	    	buttonsPanel.addComponent(viewComboBox);
	    	
	    	Dimension dim = viewComboBox.getPreferredSize();
	    	dim.setSize(dim.getWidth(), columnSelector.getPreferredSize().getHeight());
	    	viewComboBox.setPreferredSize(dim);
	    	/*
	    	viewComboBox.addPopupMenuListener(new PopupMenuListener(){
					public void popupMenuCanceled(PopupMenuEvent e) {
					}

					public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
					}

					public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
						viewComboBox.refreshList();						
					}
	    	});
	    	*/
	    }
	    
	    buttonsPanel.addButton(columnSelector);
	    getTable().getActionMap().put("columnSelector", columnSelectorAction);
    }
    
    // -----------------------
    // New Button
    // -----------------------
    FGButtonAction newAction = getNewAction();
    if(newAction != null){
	    insert = new FGButton(Globals.getIcons().getInsertIcon());
	    StaticComponent.setComponentToolTipText(insert, "Insert new item");
	    insert.setName(getButtonName(BUTTON_INSERT));
	    if(ConfigInfo.isUnitDevMode()){
	    	StaticComponent.setComponentToolTipText(insert, insert.getName());
	    }	    
	    newAction.setAssociatedButton(insert);
	    insert.addActionListener(newAction);    
	    buttonsPanel.addButton(insert);
	    getTable().getActionMap().put("new", newAction);
    }

    // -----------------------
    // Edit Button
    // -----------------------
    FGButtonAction editAction = getEditAction();
    if(editAction != null){
	    edit = new FGButton(Globals.getIcons().getEditIcon());
	    StaticComponent.setComponentToolTipText(edit, "Edit current item");
	    edit.setName(getButtonName(BUTTON_EDIT));
	    if(ConfigInfo.isUnitDevMode()){
	    	StaticComponent.setComponentToolTipText(edit, edit.getName());
	    }	    	    
	    editAction.setAssociatedButton(edit);
	    edit.addActionListener(editAction);
	    buttonsPanel.addButton(edit);
	    getTable().getActionMap().put("edit", editAction);
    }
    
    // -----------------------
    // Delete Button
    // -----------------------
    FGButtonAction deleteAction = getDeleteAction();
    if(deleteAction != null){
	    delete = new FGButton(Globals.getIcons().getDeleteIcon());
	    delete.setDisableValidationProcess(true, getTable());
	    StaticComponent.setComponentToolTipText(delete, "Delete current item");
	    delete.setName(getButtonName(BUTTON_DELETE));
	    if(ConfigInfo.isUnitDevMode()){
	    	StaticComponent.setComponentToolTipText(delete, delete.getName());
	    }	    	    
	    deleteAction.setAssociatedButton(delete);
	    delete.addActionListener(deleteAction);
	    buttonsPanel.addButton(delete);
	    getTable().getActionMap().put("delete", deleteAction);
    }
    
    // -----------------------
    // Duplicate Button
    // -----------------------
    FGButtonAction duplicateAction = getDuplicateAction();
    if(duplicateAction != null){
	    duplicate = new FGButton(Globals.getIcons().getCopyIcon());
	    StaticComponent.setComponentToolTipText(duplicate, "Duplicate current item");
	    duplicate.setName(getButtonName(BUTTON_DUPLICATE));
	    duplicateAction.setAssociatedButton(duplicate);
	    duplicate.addActionListener(duplicateAction);    
	    buttonsPanel.addButton(duplicate);
	    getTable().getActionMap().put("duplicate", duplicateAction); 
    }
    
    //-----------------------
    // Print Button
    // -----------------------
    FGButtonAction printAction = getPrintAction();
    if(printAction != null){
      print = new FGButton(Globals.getIcons().getPrintIcon());
      StaticComponent.setComponentToolTipText(print, "Print list...");
      print.setName(getButtonName(BUTTON_PRINT));
      printAction.setAssociatedButton(print);
      print.addActionListener(printAction);    
      buttonsPanel.addButton(print);
      getTable().getActionMap().put("printList", printAction); 
    }
        
    //-----------------------
    // Redirect Button
    // -----------------------
    /*FGButtonAction redirectAction = getRedirectAction();
    if(redirectAction != null){
	    redirect = new FGButton(Globals.getIcons().getRedirectIcon());
	    redirect.setToolTipText("Redirect");
	    redirect.setName(getButtonName(BUTTON_REDIRECT));
	    if(ConfigInfo.isUnitDevMode()){
	    	redirect.setToolTipText(redirect.getName());
	    }	    	    
	    redirectAction.setAssociatedButton(redirect);
	    redirect.addActionListener(redirectAction);
	    buttonsPanel.addButton(redirect);
	    getTable().getActionMap().put("redirect", redirectAction);
    }*/
    
    // -----------------------
    // Filter Button
    // -----------------------
    FGButtonAction filterAction = getFilterAction();
    if(filterAction != null){
	    filter = new FGButton(Globals.getIcons().getFilterIcon());
	    StaticComponent.setComponentToolTipText(filter, "Filter");
	    filter.setName(getButtonName(BUTTON_FILTER));
	    filterAction.setAssociatedButton(filter);
	    filter.addActionListener(filterAction);    
	    buttonsPanel.addButton(filter);
	    getTable().getActionMap().put("filter", filterAction);
    }
    
    showFilterButton(false);
    showModificationButtons(true);
    showDuplicateButton(false);
    showColomnSelectorButton(false);

    showCSVExportButton(true);
    showPrintButton(true, false);
    
    fTable.addSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent e) {
        Globals.getDisplayManager().removeLockFocusForObject(getSelectedObject());    
      }      
    });
  }

  public void addButtonActionToMap_MoveUp_MoveDown(){
	  // -----------------------
	  // Move Up
	  // -----------------------
	  FGButtonAction moveUpAction = getMoveUpAction();
	  if(moveUpAction != null){
	    getTable().getActionMap().put("moveUp", moveUpAction);
	  }
	  
	  // -----------------------
	  // Move Down
	  // -----------------------
	  FGButtonAction moveDownAction = getMoveDownAction();
	  if(moveDownAction != null){
	    getTable().getActionMap().put("moveDown", moveDownAction);
	  }
  }
    
  public String getPanelName(){
    FocList focList = getFocList();
    FocDesc focDesc = focList.getFocDesc();
  	return focDesc.getStorageName();
  }
  
  public String getButtonName(String suffix){
  	return getPanelName()+"."+suffix;
  }

  public static String getFormulaExpressionGuiFieldName(String panelName){
  	return panelName+".FORMULA_EXPRESSION_FIELD";
  }  
  
  public String getFormulaExpressionGuiFieldName(){
  	return getFormulaExpressionGuiFieldName(getPanelName());
  }
  
  public FTable getTable(){
    return fTable;
  }

  public FAbstractTableModel getTableModel(){
    return fTableModel;
  }
  
  public void setTableModel(FAbstractTableModel fTableModel){
    this.fTableModel = fTableModel;
  }
  
  public FTableView getTableView() {
    FTableView view = null;
    if (fTableModel != null) {
      view = fTableModel.getTableView();
    }
    return view;
  }
  
  public FocList getFocList(){
    return fTableModel != null ? fTableModel.getFocList() : null; 
  }

  public int getSelectedRow() {
    int row = 0;
    FTable ftable = getTable();
    if(ftable != null){
      row = ftable.getSelectedRow();
    }
    return row;
  }

  public FocObject getSelectedObject() {
    FocObject ret = null;
    int row = 0;
    FTable ftable = getTable();
    if(ftable != null){
      row = ftable.getSelectedRow();
      if(row >= 0){
        ret = ftable.getElementAt(row);
      }
    }
    return ret;
  }
  
  public void setSelectedObject(FocObject selectedObject) {
    FTable ftable = getTable();
    if(ftable != null){
      ftable.setSelectedObject(selectedObject);
    }
  }
  
  public FAbstractListPanel getThis() {
    return this;
  }

  
  public void addCellStartEditingListener(FTableCellStartEditingListener listener){
    FTable table = getTable();
    if(table != null){
      table.addCellStartEditingListener(listener);
    }
  }
  
  public void removeCellStartEditingListener(FTableCellStartEditingListener listener){
    FTable table = getTable();
    if(table != null){
      table.removeCellStartEditingListener(listener);
    }
  }
  
  /**
   * @return Returns the buttonsPanel.
   */
  public FButtonsPanel getButtonsPanel(){
    if(buttonsPanel == null){
      buttonsPanel = new FButtonsPanel();
      buttonsPanel.setFill(FPanel.FILL_NONE);
      StaticComponent.setComponentToolTipText(buttonsPanel, "Buttons Panel");//AUTOSIZE
      add(buttonsPanel, 1, 2, 1, 1, 0.9, 0.01, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE);      
    }
    return buttonsPanel;
  }
  
  public FPanel getTotalsPanel() {
    if(totalsPanel == null){
      totalsPanel = new FPanel();
      add(totalsPanel, 0, 2, 1, 1, 0.9, 0.01, GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL);      
    }
    return totalsPanel;
  }

  public void requestFocusOnTable(){
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        FTable table = getTable();
        if(table != null){
        	table.requestFocusInWindow();
        }
      }
    });
  }
  
  /*
  public void repaint(){
    super.repaint();
    requestFocusOnCurrentItem();
  }
  */

  protected void printPanel(){
    try {
      FTable        table        = getTable();
      MessageFormat footerFormat = new MessageFormat("- {0} -");
      Thread.sleep(500);
      table.print(JTable.PrintMode.FIT_WIDTH, null, footerFormat);
    }catch (Exception pe) {
    	Globals.logException(pe);
     	Globals.logString("Error printing: " + pe.getMessage());
    }
  }
  
  //-----------------------
  // Print Action
  // -----------------------
	public FGButtonAction getPrintAction() {
    if (printAction == null) {
      printAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
        	printPanel();
      	}
    	};
  	}
    return printAction;
  }
  
  public void showPrintButton(boolean showMenu, boolean showButton) {
    if(print != null) print.setVisible(showButton);
    if (showMenu) {
      //getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getPrintStroke(), "printList");
      addPopupItem(Globals.getIcons().getPrintIcon(), POPUP_LABEL_PRINT_LIST, getPrintAction());
    } else {
      //getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getPrintStroke());
      removePopupItem(POPUP_LABEL_PRINT_LIST);
    }
  }

  public void showCSVExportButton(boolean show) {
    //if(print != null) print.setVisible(show);
    if (show) {
      //getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getPrintStroke(), "csvExport");
      addPopupItem(Globals.getIcons().getExportIcon(), POPUP_LABEL_CSV_EXPORT, getExportAction());
    } else {
      //getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getPrintStroke());
      removePopupItem(POPUP_LABEL_CSV_EXPORT);
    }
  }

  public void showFilterButton(boolean show) {
    if(filter != null) filter.setVisible(show);
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getFilterStroke(), "filter");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getFilterStroke());
    }
  }

  public void enableAddButton(boolean enable) {
    if(insert != null){
      if(enable) insert.setVisible(true);
      insert.setEnabled(enable);
    }
    if (enable) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getInsertStroke(), "new");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getInsertStroke());
    }
    enablePopupItem(POPUP_LABEL_ADD, enable);
  }
  
  public void enableRemoveButton(boolean enable) {
    if(delete != null){
      if(enable) delete.setVisible(true);
      delete.setEnabled(enable);
    }
    if (enable) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getDeleteStroke(), "delete");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getDeleteStroke());
    }
    enablePopupItem(POPUP_LABEL_DELETE, enable);
  }  
  
  public void showAddButton(boolean show) {
  	FocList list = getFocList();
  	show = show && list.getFocDesc().workflow_IsAllowInsert();
		if(insert != null){
      insert.setVisible(show);
    }

    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getInsertStroke(), "new");
      addPopupItem(Globals.getIcons().getInsertIcon(), POPUP_LABEL_ADD, getNewAction());
    } else {
    	removePopupItem(POPUP_LABEL_ADD);
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getInsertStroke());
    }
  }
  
  public void showRemoveButton(boolean show) {
    if(delete != null){
      delete.setVisible(show);
    }
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getDeleteStroke(), "delete");
      addPopupItem(Globals.getIcons().getDeleteIcon(), POPUP_LABEL_DELETE, getDeleteAction());
    } else {
    	removePopupItem(POPUP_LABEL_DELETE);
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getDeleteStroke());
    }
  }
  
  public void showRedirectButtonInPopupMenu(boolean show){
  	if(show){
  		if(Globals.getApp().getGroup().allowNamingModif()){
  			addPopupItem(Globals.getIcons().getRedirectIcon(), POPUP_LABEL_REDIRECT, getRedirectAction());
  		}
  	}else{
  		removePopupItem(POPUP_LABEL_REDIRECT);
  	}
  }
  
  public void showMoveUpButton(boolean show) {
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getMoveUpStroke(), "moveUp");
      addPopupItem(Globals.getIcons().getMoveUpIcon(), POPUP_LABEL_MOVE_UP, getMoveUpAction());
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getMoveUpStroke());
      removePopupItem(POPUP_LABEL_MOVE_UP);
    }
  }
  
  public void showMoveDownButton(boolean show) {
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getMoveDownStroke(), "moveDown");
      addPopupItem(Globals.getIcons().getMoveDownIcon(), POPUP_LABEL_MOVE_DOWN, getMoveDownAction());
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getMoveDownStroke());
      removePopupItem(POPUP_LABEL_MOVE_DOWN);
    }
  }
  
  public void showModificationButtons(boolean show) {
    showAddButton(show);
    showRemoveButton(show);
    showEditButton(show);
  }

  public void enableModificationButtons(boolean enable) {
    enableAddButton(enable);
    enableRemoveButton(enable);
    enableEditButton(enable);
  }

  @Deprecated
  public void showColomnSelectorButton(boolean show, String viewKey) {
  	if(columnSelector != null){
  		columnSelector.setVisible(show);
  	}
    if(show){
      getTableView().setViewKey(viewKey);
      getTableView().setColumnVisibilityAccordinglyToConfig();
    }
    if(getTable() != null){
	    if(show){
	      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getColumnSelectorStroke(), "columnSelector");
	    }else{
	      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getColumnSelectorStroke());
	    }
    }
  }

  public void showColomnSelectorButton(boolean show){
  	if(columnSelector != null){
  		columnSelector.setVisible(show);
  	}
    if(getTable() != null){
	    if(show){
	      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getColumnSelectorStroke(), "columnSelector");
	    }else{
	      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getColumnSelectorStroke());
	    }
    }
  }

  public boolean isShowEditButton(){
    return edit.isVisible();
  }

  public void enableEditButton(boolean enable) {
    if (edit != null) {
      if(enable){
        edit.setVisible(true);
      }
      edit.setEnabled(enable);
    }
    if (enable) {    	
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getRowEditStroke(), "edit");
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getRowEditStrokeCtrl(), "edit");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getRowEditStroke());
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getRowEditStrokeCtrl());      
    }
    enablePopupItem(POPUP_LABEL_EDIT, enable);
  }
  
  public void showEditButton(boolean show) {
    if(edit != null){
      edit.setVisible(show);
    }    
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getRowEditStroke(), "edit");
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getRowEditStrokeCtrl(), "edit");
      
      addPopupItem(Globals.getIcons().getEditIcon(), POPUP_LABEL_EDIT, getEditAction());      
    } else {
    	removePopupItem(POPUP_LABEL_EDIT);
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getRowEditStroke());
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getRowEditStrokeCtrl());      
    }
  }
  
  private JMenuItem getItemIndexInPopup(FPopupMenu popupMenu, String text){
  	JMenuItem foundMenuItem = null;
    for( int i = 0; i < popupMenu.getComponents().length && foundMenuItem == null; i++){
      JMenuItem mi = (javax.swing.JMenuItem)popupMenu.getComponent(i);
      if( mi.getText().equals(text)){
      	foundMenuItem = mi ;
      }
    }
    
    return foundMenuItem;
  }
  
  public void removePopupItem(String text){
  	FPopupMenu popupMenu = getTable().getPopupMenu();
  	if(popupMenu != null){
  		JMenuItem menuItem = getItemIndexInPopup(popupMenu, text);
	  	if(menuItem != null){
	  		popupMenu.remove(menuItem);	
	  	}
  	}
  }

  public void addPopupItem(ImageIcon icon, String text, AbstractAction action){
  	FPopupMenu popupMenu = getTable().getPopupMenu();
  	if(popupMenu != null){
  		JMenuItem menuItem = getItemIndexInPopup(popupMenu, text); 
      if( menuItem == null ){
        menuItem = new JMenuItem(text, icon);
        menuItem.addActionListener(action);
        popupMenu.add(menuItem);  
      }
  	}
  }
  
  private void enablePopupItem(String text, boolean enable){
  	FPopupMenu popupMenu = getTable().getPopupMenu();
  	if(popupMenu != null){
  		JMenuItem menuItem = getItemIndexInPopup(popupMenu, text); 
      if(menuItem != null){
      	menuItem.setEnabled(enable);
	  	}
  	}
  }
  
  public void disableEditKey() {
    getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getRowEditStroke());    
    //getTable().getInputMap(WHEN_IN_FOCUSED_WINDOW).remove(FocKeys.getRowEditStrokeCtrl());
  }
  
  public void showDuplicateButton(boolean show) {
    if (duplicate != null) {
      duplicate.setVisible(show);
    }
    if (show) {
    	addPopupItem(Globals.getIcons().getCopyIcon(), POPUP_LABEL_DUPLICATE, getDuplicateAction());
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getDuplicateStroke(), "duplicate");
    } else {
    	removePopupItem(POPUP_LABEL_DUPLICATE);
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getDuplicateStroke());
    }
  }  
  
  public FGButton getFilterButton() {
    return filter;
  }
  
  public void requestFocusOnCurrentItem(){
    FTable table = getTable();
    //FTableModel fTabMod = (FTableModel) fTableModel;  
    if(fTableModel != null && table != null){
      int row = 0;
      FocList list = fTableModel.getFocList();
      if(list != null){
        FProperty selProp = list.getSelectionProperty();
        if(selProp != null){
          FocObject selectedObj = (FocObject) selProp.getObject();
          if(selectedObj != null){
            row = list.getRowForObject(selectedObj);
          }
        }
      }
 
      if(row < 0) row = 0;
      if(row < table.getRowCount()){
        table.setRowSelectionInterval(row, row);
  
        SwingUtilities.invokeLater(new Runnable(){
          public void run(){
            FTable table = getTable();
            if(table != null){
              table.requestFocusInWindow();
            }
          }
        });
      }
    }
  }
  
  public void setDropable(DropTargetListener dropTargetListener){
  	if(fTable != null){
  		FixedColumnScrollPane scrollPan = fTable.getScrollPane();
  		if(scrollPan != null){
  			scrollPan.setDropable(dropTargetListener);
  		}
  	}
  }
  
  private void setFindObjectKeyStrokeAction(){
    getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getFindStroke(), ACTION_MAP_KEY_FIND);
    findAction = new FindAction(this);
    getTable().getActionMap().put(ACTION_MAP_KEY_FIND, findAction);
  }

  private void setFilterKeyStrokeAction(){
    getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getFilterStroke(), ACTION_MAP_KEY_FILTER);
    filterAction = new FilterAction(this);
    getTable().getActionMap().put(ACTION_MAP_KEY_FILTER, filterAction);
  }

  public CopyPasteContent getCopyPasteContent(){
    if( copyPasteContent == null ){
      copyPasteContent = new CopyPasteContent();
    }
    return copyPasteContent;
  }
  
  private void setDefaultCopyPasteKeyStrokeActions(){
    getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getControlCStroke(), ACTION_MAP_KEY_COPY);
    copyAction = new CopyAction(this);
    getTable().getActionMap().put(ACTION_MAP_KEY_COPY, copyAction);
    
    getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getControlVStroke(), ACTION_MAP_KEY_PASTE);
    pasteAction = new PasteAction(this);
    getTable().getActionMap().put(ACTION_MAP_KEY_PASTE, pasteAction);
  }
  
  public void setCopyKeyStrokeAction(CopyAction action){
    getTable().getActionMap().remove(ACTION_MAP_KEY_COPY);
    copyAction.dispose();
    copyAction = action;
    getTable().getActionMap().put(ACTION_MAP_KEY_COPY, copyAction);
  }

  public void setPasteKeyStrokeAction(PasteAction action){
    getTable().getActionMap().remove(ACTION_MAP_KEY_PASTE);
    pasteAction.dispose();
    pasteAction = action;
    getTable().getActionMap().put(ACTION_MAP_KEY_PASTE, pasteAction);
  }

  public FPanel newDetailsPanelForFocObject(FocObject focObj, int viewID){
    return focObj.newDetailsPanel(viewID);
  }
  
  public boolean isDirectlyEditable() {
    return getFocList().isDirectlyEditable();
  }
  
  protected FocObject newListItem() {
    return getFocList().newEmptyItem();
  }
  
  public FocObject newEmptyItem() {
    FocObject focObj  = null;
    FocList   focList = getFocList();
    
    if (focList != null) {
      focList.sort();
      boolean backup = focList.isDisableReSortAfterAdd();      
      focList.setDisableReSortAfterAdd(true);
      
      focObj = newListItem();
      
      focList.setDisableReSortAfterAdd(backup);
      
      if(focObj != null){
      	popupDetailPanel(focObj);
      }
    }

    if(focObj != null){
    	setSelectedObject(focObj);
    }
    return focObj;
  }
  
  public void popupDetailPanel(FocObject focObj){
    FPanel newDetailsPanel = newDetailsPanel(focObj, getTableView().getDetailPanelViewIDForNewItem());
    if(newDetailsPanel == null && getTableView().getDetailPanelViewIDForNewItem() != -1){
      newDetailsPanel = newDetailsPanelForFocObject(focObj, getTableView().getDetailPanelViewIDForNewItem());
    }
    
    if(newDetailsPanel == null){
      if(focObj.isCreated() && !getThis().isDirectlyEditable()){
        setCurrentDefaultFocusComponent(getTable());
        newDetailsPanel = newDetailsPanelForFocObject(focObj, getTableView().getDetailPanelViewID());
      }
    }
    
    if(newDetailsPanel != null){
    	//The edit after insert can either be indicated on the tableView level if does not depend on the object or on the object level
    	//Like in C3 application the underlyings are edit after insert if we have a ParamSet
    	if((getTableView().isEditAfterInsertion() || focObj.isEditAfterInsert()) && focObj.isCreated()){
    		EditAfterCreationData editAfterCreationData = new EditAfterCreationData(this, focObj);
    		newDetailsPanel.setEditAfterCreationData(editAfterCreationData);
    	}
    	if(Globals.getDisplayManager().getActiveDialog() != null){
    		Globals.getDisplayManager().popupDialog(newDetailsPanel, newDetailsPanel.getFrameTitle(), true, 200, 200);
    	}else{
    		Globals.getDisplayManager().newInternalFrame(newDetailsPanel);	
    	}
    	
    	/*
      setSelectedObject(focObj);
      if(getTableView().isEditAfterInsertion()){
        editCurrentItem(focObj);
      }
      */
    }

  }
  
  public void afterQuickFilterApplication(){
  }
  
  //------------------------------------------------------------- 
  //-------------------------------------------------------------
  //Mouse listener
  //-------------------------------------------------------------
  //-------------------------------------------------------------

  private boolean addMouseListener(){
    boolean addMouseListener = true;
    /*
    FTableView view = fTableModel != null ? fTableModel.getTableView() : null;
    if(view != null ) {
      for(int i = 0; i < view.getColumnCount(); i++) {
        FTableColumn fCol = view.getColumnAt(i);
        if(fCol != null && fCol.getEditable()){
          addMouseListener = false;
          break;
        }
      }
    }
    */
    return addMouseListener;
  }
  
  private ArrayList<DoubleClickListener> doubleClickListeners = null;
  
  private int doubleClickListeners_size(){
  	return doubleClickListeners != null ? doubleClickListeners.size() : 0;
  }
  
  private DoubleClickListener doubleClickListeners_get(int at){
  	return doubleClickListeners != null ? doubleClickListeners.get(at) : null;
  }
  
  public void addDoubleClickListeners(DoubleClickListener doubleClickListener){
  	if(doubleClickListener != null){
	  	if(doubleClickListeners == null){
	  		doubleClickListeners = new ArrayList<DoubleClickListener>();
	  	}
	  	doubleClickListeners.add(doubleClickListener);
  	}
  }

  public void removeDoubleClickListeners(DoubleClickListener doubleClickListener){
  	if(doubleClickListener != null && doubleClickListeners != null){
  		doubleClickListeners.remove(doubleClickListener);
  	}
  	doubleClickListeners.remove(doubleClickListener);
	}

	public boolean doubleClickListeners_FireEvent(int colID, int row){
		boolean consumed = false;
		if(doubleClickListeners_size() > 0){
			DoubleClickEvent    event = new DoubleClickEvent(row, colID);
			FAbstractTableModel model = (FAbstractTableModel) getTableModel();
			event.setFocObject(model.getRowFocObject(row));
	  	for(int i=0; i<doubleClickListeners_size(); i++){
	  		consumed = consumed || doubleClickListeners_get(i).doubleClick(event);
	  	}
		}
		return consumed;
	}

  protected boolean doubleClickEvent(int colID, int row){
  	return doubleClickListeners_FireEvent(colID, row);
  }
  
  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
   */
  public void mouseClicked(MouseEvent e) {
    if(e.getClickCount()>=2){
      FAbstractTableModel model 			= (FAbstractTableModel) getTableModel();
      Point 							cell 				= getTable().getCellCoordinatesForMouseCurrentPosition();

      FTableColumn 				tableColumn = cell == null ? null : getTableModel().getTableView().getVisibleColumnAt(cell.x);
      if(tableColumn == null && cell != null && getTable().getScrollPane() != null){
      	if(getTable().getScrollPane().getFixedTable() != null && getTable().getScrollPane().getFixedTable().getTableView() != null){
      		tableColumn = getTable().getScrollPane().getFixedTable().getTableView().getVisibleColumnAt(cell.x);
      	}
      	if(tableColumn == null && getTable().getScrollPane().getScrollTable() != null && getTable().getScrollPane().getScrollTable().getTableView() != null){
      		tableColumn = getTable().getScrollPane().getScrollTable().getTableView().getVisibleColumnAt(cell.x);
      	}
      }
      if(tableColumn == null || !tableColumn.getEditable()){
	      if(tableColumn == null || !doubleClickEvent(tableColumn.getID(), cell.y)){      		
		      if(!model.getFocList().isDirectlyEditable()){
		      	FIFooterPanel fPanel = (FValidationPanel) getValidationPanel();
		      	boolean consumed = false;
		      	if(fPanel instanceof FValidationPanel){
		      		FValidationPanel vPanel = (FValidationPanel) fPanel;
		      		if(vPanel != null && vPanel.getSelectionType() == FValidationPanel.SELECTION_ENABLED){
		      			vPanel.doValidate();
		      			consumed = true;
		      		}
		      	}
		      	if(!consumed && isShowEditButton()){
		      		editCurrentItem();
		      	}
		      	/*
		      	if(getFocList().getSelectionProperty() != null){
		      		getFocList().validateSelectedObject();
		      		Globals.getDisplayManager().goBack();
		      	}else if(isShowEditButton()){
		          editCurrentItem();
		        }
		        */
		      }
	      }
      }
    }else{
      getTable().requestFocusInWindow();
    }
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  public void mousePressed(MouseEvent e) {
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
   */
  public void mouseReleased(MouseEvent e) {
    
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  public void mouseEntered(MouseEvent e) {
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  public void mouseExited(MouseEvent e) {
  }
	public String getPrnContextName() {
		return prnContextName;
	}
	public void setPrnContextName(String prnContextName) {
		this.prnContextName = prnContextName;
	}
	
	public static void addCustomColumns(FTableView tableView, FocDesc focDesc, int colIDShift){
		addCustomColumns(tableView, focDesc, colIDShift, true);
	}
	
	public static void addCustomColumns(FTableView tableView, FocDesc focDesc, int colIDShift, boolean editable){
		ArrayList<Integer> idArray = new ArrayList<Integer>();
		
		FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(enumer != null && enumer.hasNext()){
    	FField fld = (FField) enumer.next();
    	if(fld != null && fld.isFabField()){
    		idArray.add(fld.getID());
    	}
    }
    
    Collections.sort(idArray);
    for(int i=0; i<idArray.size(); i++){
    	FField fld = focDesc.getFieldByID((Integer) (idArray.get(i)));
    	
  		tableView.addColumn(focDesc, fld.getID(), colIDShift + fld.getID(), fld.getTitle(), editable);
  		
  		if(fld.isWithInheritance()){
  			tableView.addColumn(focDesc, fld.getInheritanceField().getID(), colIDShift + fld.getInheritanceField().getID(), fld.getInheritanceField().getTitle(), editable);
  		}
    }
	}
	
  public FGButtonAction getExportAction(){
    if (exportAction == null) {
      exportAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          try {
            FTable ftable = getTable();
            ftable.csv_Export();
          } catch (Exception e2) {
            Globals.logException(e2);
          }
        }
      };
    }
    return exportAction;
  }
}
