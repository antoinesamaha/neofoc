/*
 * Created on 24 fevr. 2004
 */
package com.foc.gui;

import javax.swing.*;

import com.fab.gui.browse.GuiBrowse;
import com.fab.model.table.TableDefinition;
import com.foc.*;
import com.foc.access.AccessSubject;
import com.foc.desc.*;
import com.foc.gui.lock.ListFocusLock;
import com.foc.gui.table.*;
import com.foc.gui.table.cellControler.*;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.filter.FocDescForFilter;
import com.foc.list.filter.FocListFilter;
import com.foc.list.filter.FocListFilterGuiBrowsePanel;
import com.foc.property.*;

import java.awt.event.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FListPanel extends FAbstractListPanel {
	
  private FGButtonAction newAction            = null;
  protected FGButtonAction deleteAction         = null;
  private FGButtonAction editAction           = null;
  private FGButtonAction selectAction         = null;
  private FGButtonAction duplicateAction      = null;
  private FGButtonAction filterAction         = null;
  private FGButtonAction columnSelectorAction = null;
  private FGButtonAction redirectAction       = null;
  private FGButtonAction moveUpAction         = null;
  private FGButtonAction moveDownAction       = null;

  private FPanel   selectAllUnselectAllPanel  = null;
  private FGButton selectAllButton            = null;
  private FGButton unselectAllButton          = null;
  private String   selectAllLabel             = "Select all";
  private String   unselectAllLabel           = "Unselect all";
  
  public FListPanel() {
  	super();
  }
  
  public FListPanel(FocList focList) {
  	try{
  		setFocList(focList);
  	}catch(Exception e){
  		Globals.logException(e);
  	}
  }
  
  public FListPanel(String frameTitle, int frameSizing, int panelFill) {
  	super(frameTitle, frameSizing, panelFill);
  }
    
  public FListPanel(String panelTitle, int panelFill) {
  	super(panelTitle, panelFill);
  }

  public FListPanel(FocList focList, int ddw) {
  	super();
  	try{
  		setFocList(focList);
  	}catch(Exception e){
  		Globals.logException(e);
  	}
  }
  
  public void dispose(){
    if(fTable != null){
      fTable.removeMouseListener(this);
    }
    
    if(getFocList() != null){
      getFocList().removeAttachedListPanel(this);
    }

    super.dispose();
    
    newAction            = null;
    deleteAction         = null;
    editAction           = null;
    selectAction         = null;
    duplicateAction      = null;
    filterAction         = null;
    columnSelectorAction = null;
    redirectAction       = null;
    moveUpAction         = null;
    moveDownAction       = null;
  }

  public FListPanel getThis() {
    return this;
  }

  public void setFocList(FocList focList){
  	if(focList.getFocDesc().workflow_getTransactionConfig() != null){
  		if(Globals.getApp().getUser() != null && Globals.getApp().getUser().isTransactionOrderIncremental()){
  			focList.getListOrder().setReverted(false);
  			focList.sort();
  		}
  	}
  	if(fTableModel != null){
  		Globals.logString("01BARMAJA Warning - In ListPanel FocList should be set once!");
  	}
    this.fTableModel = new FTableModel(focList, this);
    focList.setAttachedListPanel(this);
    setName(focList.getFocDesc().getStorageName());
  }
  
  //Moving this function to FAbstractListPanel
  /*public FocList getFocList(){
    return fTableModel != null ? ((FTableModel)fTableModel).getFocList() : null; 
  }*/
  
  public boolean isSelectionEnabled() {
    boolean b = false;
    FocList list = getFocList();
    b = list != null ? list.getSelectionProperty() != null : false;
    return b;
  }
  
  public FocDesc getFocDesc() {
    FocDesc desc = null;
    FocList focList = getFocList();
    if (focList != null) {
      desc = focList.getFocDesc();
    }
    return desc;
  }
  
  public void constructFromDescription(boolean withCheckBox) {
    getFocDesc().fillTableModelWithKeyFields(fTableModel, withCheckBox);
    construct();
  }

  public void constructFromDescription() {
    constructFromDescription(false);
  }

  public void construct(){
  	super.construct();
  	
  	if(getFocDesc().hasOrderField()){
  		addButtonActionToMap_MoveUp_MoveDown();
  	}
  	
  	if(			getFocDesc().workflow_getTransactionConfig() != null 
  			&& 	Globals.getApp().getUser() != null 
  			&& 	Globals.getApp().getUser().isTransactionOrderIncremental()){
  		if(getTable() != null && getTable().getRowCount() > 0){
  			getTable().setRowSelectionInterval(getTable().getRowCount()-1, getTable().getRowCount()-1);
  		}
  	}
    //fTable.addMouseListener(this);
  }
  
  public FPanel newDetailsPanel(FocObject focObject, int viewID){
    return null;
  }
  
  public FocObject newEmptyItem() {
    FocObject focObj = super.newEmptyItem();
    
    if(focObj.isCreated()){
      if(getThis().isDirectlyEditable()){
        setSelectedObject(focObj);
        if(getTable() != null){
          getTable().setColumnSelectionInterval(0, 0);
        }
        requestFocusOnTable();
        //BGuiLock
        ListFocusLock listFocusLock = new ListFocusLock(getTable(), getFocList(), focObj);
        Globals.getDisplayManager().setFocusLock(listFocusLock);
      }
    }
    
    return focObj;
  }
  
  public String getSelectAllLabel() {
		return selectAllLabel;
	}

	public void setSelectAllLabel(String selectAllLabel) {
		this.selectAllLabel = selectAllLabel;
		if(selectAllButton != null){
			selectAllButton.setText(this.selectAllLabel); 
		}
	}

	public String getUnselectAllLabel() {
		return unselectAllLabel;
	}

	public void setUnselectAllLabel(String unselectAllLabel) {
		this.unselectAllLabel = unselectAllLabel;
		if(unselectAllButton != null){
			unselectAllButton.setText(this.unselectAllLabel);
		}
	}
	
	public void addSelectAllActionListener(ActionListener listener) {
		if(selectAllButton != null){
			selectAllButton.addActionListener(listener); 
		}
	}

	public void addUnselectAllActionListener(ActionListener listener) {
		if(unselectAllButton != null){
			unselectAllButton.addActionListener(listener); 
		}
	}
  
  public FPanel getSelecAllUnselectAllPanel(){
  	if(selectAllUnselectAllPanel == null){
  		selectAllUnselectAllPanel = new FPanel();
  		FGButton selectAll = createSelectAllButton();
  		FGButton unselectAll = createUnselectAllButton();
  		selectAllUnselectAllPanel.add(selectAll,0,0);
  		selectAllUnselectAllPanel.add(unselectAll,1,0);
  		selectAllUnselectAllPanel.setFill(FPanel.FILL_NONE);
  	}
  	return selectAllUnselectAllPanel;
  }
  
  public FPanel showSelecAllUnselectAllPanel(){
  	FPanel panel = getSelecAllUnselectAllPanel();
  	getTotalsPanel().add(panel, 0, 0);
  	return panel;
  }
  
  private FGButton createSelectAllButton(){
  	if(selectAllButton == null){
  		selectAllButton = new FGButton(getSelectAllLabel());
  		selectAllButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
          FTable table = getTable();
          FTableModel model = (FTableModel)table.getTableModel();
          for (int i = 0; i < model.getRowCount(); i++) {
            FocListElement listElmt = model.getRowListElement(i);
            if (listElmt != null) {
            	FProperty selectedProperty = listElmt.getSelectedProperty();
            	if(selectedProperty != null && !selectedProperty.isValueLocked()){
            		listElmt.setSelected(true);
            	}
            }
          }
          model.fireTableDataChanged();
				}
  		});
  	}
  	return selectAllButton;
  }
  
  private FGButton createUnselectAllButton(){
  	if(unselectAllButton == null){
  		unselectAllButton = new FGButton(getUnselectAllLabel());
  		unselectAllButton.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
          FTable table = getTable();
          FTableModel model = (FTableModel)table.getTableModel();
          for (int i = 0; i < model.getRowCount(); i++) {
            FocListElement listElmt = model.getRowListElement(i);
            if (listElmt != null) {
            	FProperty selectedProperty = listElmt.getSelectedProperty();
            	if(selectedProperty != null && !selectedProperty.isValueLocked()){
            		listElmt.setSelected(false);
            	}
            }
          }
          model.fireTableDataChanged();
				}
  		});
  	}
  	return unselectAllButton;
  }
  
  // -----------------------
  // New Action
  // -----------------------
  public FGButtonAction getNewAction() {
    if (newAction == null) {
      newAction = new FGButtonAction(null) {
        /**
         * 
         */
        private static final long serialVersionUID = -3052616839523635420L;

        public void focActionPerformed(ActionEvent e) {
          try {
            //BGuiLock
            //if(!getTable().isEditing() && getFocList().isContentValid())
            //getTable().getEditorComponent().getE
            
            /*if(getTable().isEditing()){
              getTable().
            }*/
            //if(requestFocusInWindow()){
          	SwingUtilities.invokeLater(new Runnable(){
							public void run() {
	              newEmptyItem();								
							}
          	});

            //}
            
            //if(/*!getTable().isEditing() && */!Globals.getDisplayManager().shouldLockFocus(true)){
            //EGuiLock
            //  newEmptyItem();
            //}
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return newAction;
  }
  
  // -----------------------
  // Delete Action
  // -----------------------
  
  protected FocObject deleteFocObject(int row, boolean withDialog){
  	FocObject highLightedObject = null;
  	
  	int dialogRet = JOptionPane.YES_OPTION;
  	if(withDialog){
	    dialogRet = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
	        "Confirm item deletion",
	        "01Barmaja - Confirmation",
	        JOptionPane.YES_NO_OPTION,
	        JOptionPane.WARNING_MESSAGE,
	        null);
  	}
  	
  	if(dialogRet == JOptionPane.YES_OPTION){
	    FTable ftable = getTable();
	    if(row >= 0){
	      FocObject focObj = (FocObject) ftable.getElementAt(row);
	
		  	//We want to set the object status to deleted.
		    //But since we might have removed the father status for autonomy reasons
		    //We put it again for the moment just to make the list react
		    AccessSubject fatherSubject = focObj.getFatherSubject();
		    if(fatherSubject != getFocList()){
		      focObj.setFatherSubject(getFocList());
		    }
		    focObj.setDeleted(true);
		    if(fatherSubject != getFocList()){
		      focObj.setFatherSubject(fatherSubject);
		    }
	      highLightedObject = (FocObject) ftable.getElementAt(row);
	      if(highLightedObject == null && row > 0){
	        highLightedObject = (FocObject) ftable.getElementAt(row - 1);                      
	      }
	    }
  	}
    return highLightedObject;
  }
  
  public FGButtonAction getDeleteAction() {
    if (deleteAction == null) {
      deleteAction = new FGButtonAction(null) {
        /**
         * 
         */
        private static final long serialVersionUID = -429900189381760891L;

        public void focActionPerformed(ActionEvent e) {// ici
          FocObject highLightedObject = getFocList().getSelectedObject();
          //if(!getTable().isEditing()){
            try {
              FTable ftable = getTable();
              int row = ftable.getSelectedRow();
              if(row >= 0){
                FocObject focObj = (FocObject) ftable.getElementAt(row);
                StringBuffer messageBuffer = focObj.checkDeletionWithMessage();
                
                if(messageBuffer != null && messageBuffer.length() > 0){
                	Globals.getApp().getDisplayManager().popupMessage(messageBuffer.toString());
                }else{
                  highLightedObject = deleteFocObject(row, true); 
                }
              }
            } catch (Exception e2) {
              Globals.logException(e2);
            }
          //}
          
          setSelectedObject(highLightedObject);
          requestFocusOnTable();
        }
      };
    }
    return deleteAction;
  }
  
  // -----------------------
  // Edit Action
  // -----------------------

  public void editCurrentItem(FocObject focObj){
  	Globals.setMouseComputing(true);
  	FDialog waitDialog = getTableView().isPopupLoadingMessage() ? Globals.getDisplayManager().popupMessage_WithoutButtons("Please Wait", "Loading...") : null;

  	if(waitDialog != null){
  		editCurrentItem_Internal_WithInvoqueLater(focObj, waitDialog);
  	}else{
  		editCurrentItem_Internal(focObj);	
  	}
  	Globals.setMouseComputing(false);
  }
  
  public void editCurrentItem(){
    try {
      FTable table = getTable();
      if(table != null){
        int row = table.getSelectedRow();
        FocObject focObj = row >= 0 ? (FocObject) table.getElementAt(row) : null;
        if(focObj == null){
        	Globals.getDisplayManager().popupMessage("Select a row first");
        }else{
        	editCurrentItem(focObj);
        }
      }
    } catch (Exception e2) {
    	Globals.logException(e2);
    }
  }
  
  private void editCurrentItem_Internal_WithInvoqueLater(final FocObject focObj, final FDialog waitDialog){
  	SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				editCurrentItem_Internal(focObj);
        if(waitDialog != null){
        	Globals.getDisplayManager().closeDialog(waitDialog);
        }
			}
  	});
  }
  
  private void editCurrentItem_Internal(FocObject focObj){
    Boolean restorSuccecfull = false;
    FPanel  focObjPanel      = null;

    focObj.backup();
    if(shouldCreateNewInternalFrame(focObj)){
    	/*
    	focObjPanel = newDetailsPanel(focObj, getTableView().getDetailPanelViewID());
    	if(focObjPanel == null){
    		focObjPanel = newDetailsPanelForFocObject(focObj, getTableView().getDetailPanelViewID());
    	}
      setSelectedObject(focObj);
      
      popupDetailsPanel(focObj, focObjPanel);
      //internalFrame = Globals.getDisplayManager().newInternalFrame(focObjPanel);
      //associateInternalFrameToObject(focObj,internalFrame);
      
      restorSuccecfull = true;
      */
    }else{
      restorSuccecfull = Globals.getDisplayManager().restoreInternalFrame(getInternalFrameAssociatedToObject(focObj));
    }
    if(!restorSuccecfull){
    	focObjPanel = newDetailsPanel(focObj, getTableView().getDetailPanelViewID());
    	if(focObjPanel == null){
	      //discardAssociatedInternalFrame(focObj);
	    	focObjPanel = newDetailsPanelForFocObject(focObj, getTableView().getDetailPanelViewID());
    	}
    	setSelectedObject(focObj);
    	
    	popupDetailsPanel(focObj, focObjPanel);
    	//internalFrame = Globals.getDisplayManager().newInternalFrame(focObjPanel);
      //associateInternalFrameToObject(focObj,internalFrame);
    }
  }
  
  protected InternalFrame popupDetailsPanel(FocObject focObj, FPanel focObjPanel){
  	InternalFrame internalFrame = null;
  	//FDialog dialog = Globals.getDisplayManager().getActiveDialog();
  	/*if(dialog != null){
  		Globals.getDisplayManager().changePanel(focObjPanel);
  	}else{*/
    	internalFrame = Globals.getDisplayManager().newInternalFrame(focObjPanel);
      associateInternalFrameToObject(focObj, internalFrame);
  	//}
  	return internalFrame;
  }
  
  public FGButtonAction getEditAction() {
    if (editAction == null) {
      editAction = new FGButtonAction(null) {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void focActionPerformed(ActionEvent e) {
          //if(!getTable().isEditing()){
          FTable table = getTable();
          setCurrentDefaultFocusComponent(table);
          editCurrentItem();
          	/*
            try {
              FTable ftable = getTable();
              int row = ftable.getSelectedRow();
              FocObject focObj = (FocObject) ftable.getElementAt(row);
              if (focObj != null) {
                focObj.backup();
                FPanel focObjPanel = focObj.newDetailsPanel(FocObject.DEFAULT_VIEW_ID);
                setSelectedObject(focObj);
                
                Globals.getDisplayManager().changePanel(focObjPanel);
              }              
            } catch (Exception e2) {
              Globals.logException(e2);
            }
            */
          }
        //}
      };
    }
    return editAction;
  }

  // -----------------------
  // Select Action
  // -----------------------
  public FGButtonAction getSelectAction() {
    if (selectAction == null) {
      selectAction = new FGButtonAction (null) {      

        public void focActionPerformed(ActionEvent e) {
          //BGuiLock
          //if(!getTable().isEditing() && getFocList().isContentValid())
          //if(!getTable().isEditing() && !Globals.getDisplayManager().shouldLockFocus(true)){
          //EGuiLock
            try {
              /*
               * FTable ftable = getTable(); int row = ftable.getSelectedRow();
               * FocObject focObj = (FocObject) ftable.getElementAt(row); if
               * (focObj != null) {
               */
              Globals.getDisplayManager().goBack();
              FocList focList = getFocList();
              if (focList != null) {
                focList.validateSelectedObject();
              }
              /*
               * if (focList != null) {
               * focList.fireEvent(FocEvent.ID_ITEM_SELECT); }
               */
              // }
            } catch (Exception e2) {
              Globals.logException(e2);
            }
          }
        //}
      };
    }
    return selectAction;
  }

  // -----------------------
  // Edit Cell Action
  // -----------------------
  public FGButtonAction getEditCellAction() {
    if (selectAction == null) {
      selectAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          FTable ftable = getTable(); 
          int selRow = ftable.getSelectedRow();
          int selCol = ftable.getSelectedColumn();
          FocObject focObj = (FocObject) ftable.getElementAt(selRow);
          if (focObj != null) {
            FTableModel tableModel = (FTableModel) ftable.getModel();
            FTableView tableView = tableModel.getTableView();
            FTableColumn fColumn = tableView.getColumnAt(selCol);
            if(fColumn != null){
              AbstractCellControler cellEditor = fColumn.getCellEditor();
              if(cellEditor != null){
                cellEditor.editRequested(ftable, selRow, selCol);
              }
            }
          }
        }
      };
    }
    return selectAction;
  }

  //-----------------------
  // Duplicate Action
  // -----------------------
  
  public FGButtonAction getDuplicateAction() {
    if (duplicateAction == null) {
      duplicateAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          try {
            //BGuiLock
            //if(getFocList().isContentValid())
            //if(!Globals.getDisplayManager().shouldLockFocus(true)){
            //EGuiLock
              duplicate();
              
              /*
              boolean backupControler = getFocList().isControler();
              getFocList().forceControler(true);
              getFocList().validate();
              getFocList().forceControler(backupControler);
              getFocList().reloadFromDB();
              */
            //}
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return duplicateAction;
  }
  
  public void duplicate(){
    FTable ftable = getTable();
    int row = ftable.getSelectedRow();
    if(row >= 0){
      FocObject sourceObj = (FocObject) ftable.getElementAt(row);
      
      if(sourceObj != null){
      	boolean callValidation = true;
      	if(!getFocList().isDirectlyEditable() && getFocList().isDirectImpactOnDatabase()){
      		callValidation = false;
      	}
      	FocObject newObj = newEmptyItem();
        sourceObj.duplicate(newObj, sourceObj.getMasterObject(), true, callValidation);
        if(!callValidation){
        	popupDetailPanel(newObj);
        }
      }
    }
  }
  
  // -----------------------
  // Filter Action
  // -----------------------
  public FGButtonAction getFilterAction() {
    if (filterAction == null) {
      filterAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          try {
            FTable ftable = getTable();
            FTableModel model = (FTableModel) ftable.getModel();
            FocListFilter filter = model.getFilter();
            if(filter != null){
            	if(filter.getThisFocDesc().isDbResident()){
            		FocDescForFilter filterFocDesc = (FocDescForFilter) filter.getThisFocDesc(); 
            		FocList listOfFilters = filterFocDesc.newFilterFocList(FocList.LOAD_IF_NEEDED);
            		FocListFilterGuiBrowsePanel guiBrowe = new FocListFilterGuiBrowsePanel(filter, listOfFilters, FocObject.DEFAULT_VIEW_ID);
            		Globals.getDisplayManager().changePanel(guiBrowe);
            	}else{
	              FPanel panel = filter.newDetailsPanel(0);
	              Globals.getDisplayManager().popupDialog(panel, "Filter", true);
	              getTableModel().fireTableDataChanged();
            	}
            }
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return filterAction;
  }  
  
  // -----------------------
  // Column selector Action
  // -----------------------
  public FGButtonAction getColumnSelectorAction() {
    if (columnSelectorAction == null) {
      columnSelectorAction = new FGButtonAction(null) {
        private static final long serialVersionUID = 1L;

        public void focActionPerformed(ActionEvent e) {
          try {
            getTableView().popupColumnConfigurationPanel(getTable());
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return columnSelectorAction;
  }
  
  //-----------------------
  // Redirect Action
  // -----------------------
  
  @Override
	public FGButtonAction getRedirectAction() {
		if(redirectAction == null && Globals.getApp().getGroup().allowNamingModif()){
			redirectAction = new FGButtonAction(null){
				@Override
				public void focActionPerformed(ActionEvent e) {
					FocObject focObjectToRedirectFrom = getSelectedObject();
					if(focObjectToRedirectFrom != null){
						FocDesc focDesc = getFocList().getFocDesc();
						FocList listToChooseFrom = focDesc.getFocList(FocList.LOAD_IF_NEEDED);
						FObject selectionProperty = new FObject(null, 1, null);
						listToChooseFrom.setSelectionProperty(selectionProperty);
						FAbstractListPanel listPanel = listToChooseFrom.getSelectionPanel(false);
						if(listPanel != null){
							listPanel.showFilterButton(listPanel.getTableView().getFilter() != null);
							listPanel.showAddButton(false);
							listPanel.showRemoveButton(false);
							listPanel.showEditButton(false);
							listPanel.showDuplicateButton(false);
							listPanel.showRedirectButtonInPopupMenu(false);
							
							FValidationPanel validPanel = (FValidationPanel) listPanel.getValidationPanel();
							validPanel.setSelectionType(FValidationPanel.SELECTION_ENABLED);
							Globals.getDisplayManager().popupDialog(listPanel, "Choose new object", true);
							selectionProperty = listToChooseFrom.getSelectionProperty();
							FocObject focObjectToRedirectTo = (FocObject) selectionProperty.getObject();
							if(focObjectToRedirectTo != null){
								focObjectToRedirectFrom.referenceCheck_RedirectToNewFocObject(focObjectToRedirectTo);
							}
						}
					}
				}
			};
		}
  	return redirectAction;
	}
  
  //-----------------------
  // Move Down Action
  // -----------------------
  
  public FGButtonAction getMoveDownAction() {
    if (moveDownAction == null) {
      moveDownAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          try {
            int r = getTable().getSelectedRow();
            if( r < getTable().getFocList().size()-1){
            	FocObject obj1 = getTableModel().getRowFocObject(r);
            	FocObject obj2 = getTableModel().getRowFocObject(r+1);
            	
            	int swap = obj1.getOrder();
            	obj1.setOrder(obj2.getOrder());
            	obj2.setOrder(swap);
            	getFocList().sort();
            }
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return moveDownAction;
  }
  
  //-----------------------
  // Move Up Action
  // -----------------------
  
  public FGButtonAction getMoveUpAction() {
    if (moveUpAction == null) {
      moveUpAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          try {
            int r = getTable().getSelectedRow();
            if( r > 0 ){
            	FocObject obj1 = getTableModel().getRowFocObject(r);
            	FocObject obj2 = getTableModel().getRowFocObject(r-1);
            	
            	int swap = obj1.getOrder();
            	obj1.setOrder(obj2.getOrder());
            	obj2.setOrder(swap);
            	getFocList().sort();
            }
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return moveUpAction;
  }
  
  //-----------------------
  // Print Action
  // -----------------------

  /**
   * @return
   */
  public boolean isDirectlyEditable() {
    return getFocList().isDirectlyEditable();
  }

  /**
   * @param b
   */
  public void setDirectlyEditable(boolean b) {
    getFocList().setDirectlyEditable(b);
    /*
    if (b && getFocList().isDirectImpactOnDatabase()) {
      Exception e = new Exception("List Panel cannot be DIRECTLY_EDITABLE when list is DIRECT_IMPACT_ON_DATABASE");
      Globals.logException(e);
    } else {
      getFocList().setWaitForValidationToAddObject(false);
      directlyEditable = b;
      if (directlyEditable) {
        FTableView tableView = this.getTableView();
        if (tableView != null) {
          for (int i = 0; i < tableView.getColumnCount(); i++) {
            FTableColumn col = tableView.getColumnAt(i);
            if (col != null) {
              col.setEditable(true);
            }
          }
        }
      }
    }
    */
  }
  
/*  public void requestFocusOnCurrentItem(){
    FTable table = getTable();
    FTableModel fTabMod = (FTableModel) fTableModel;  
    if(fTabMod != null && table != null){
      int row = 0;
      FocList list = fTabMod.getFocList();
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
  }*/
 
  public void addColumns_ForUserFields(){
  	addColumns_ForUserFields(true);
  }
  
  public void addColumns_ForUserFields(boolean editable){
  	FocDesc         focDesc  = getFocDesc();
  	TableDefinition tableDef = TableDefinition.getTableDefinitionForFocDesc(focDesc);
  	if(tableDef != null){
  		FocList   browseList = tableDef.getBrowseViewDefinitionList();
  		if(browseList.size() > 0){
	  		GuiBrowse guiBrowse  = (GuiBrowse) browseList.getFocObject(0);
	  		if(guiBrowse != null){
	  			guiBrowse.addColumns(getTableView(), focDesc, editable);
	  		}
  		}
  	}
  }
}