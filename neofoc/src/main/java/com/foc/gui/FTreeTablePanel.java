/*
 * Created on 24 fevr. 2004
 */
package com.foc.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import com.foc.FocKeys;
import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.table.FTable;
import com.foc.gui.table.FixedColumnScrollPane;
import com.foc.gui.table.cellControler.editor.TreeTableTextField;
import com.foc.gui.tree.TreeCollapseScanner;
import com.foc.gui.treeTable.FGTreeInTable;
import com.foc.gui.treeTable.FTreeTableModel;
import com.foc.list.filter.FocListFilter;
import com.foc.tree.FNode;
import com.foc.tree.FTree;
import com.foc.tree.TreeScanner;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FTreeTablePanel extends FAbstractListPanel implements MouseListener {
	private FGButtonAction toggleCodeDisplay = null;
	private FGButtonAction expandAllAction   = null;
	private FGButtonAction collapseAllAction = null;
  private FGButtonAction newAction         = null;
  private FGButtonAction deleteAction      = null;
  private FGButtonAction editAction        = null;
  private FGButtonAction columnSelectorAction = null;
  private FGButtonAction filterAction      = null;
  private FGButtonAction duplicateAction   = null;
  private FGButtonAction deepExpandAction  = null;
  private FGButtonAction expandUpToThisLevelAction   = null;
  private FGButtonAction collapseUpToThisLevelAction = null;
  private FGButtonAction moveUpAction      = null;
  private FGButtonAction moveDownAction    = null;
  //private FNode newlyCreatedNode = null;
  
  private FGButton expandAll = null;
  private FGButton collapseAll = null;
  
  public  final static String BUTTON_EXPAND_ALL                     = "EXPAND_ALL"   ;
  public  final static String BUTTON_COLLAPSE_ALL                   = "COLLAPSE_ALL" ;
  private static final String POPUP_LABEL_EXPAND_UP_TO_THIS_LEVEL   = "Expand up to this level";
  private static final String POPUP_LABEL_DEEP_EXPAND               = "Deep Expand"  ;
  private static final String POPUP_LABEL_COLLAPSE_UP_TO_THIS_LEVEL = "Collapse up to this level";
  private static final String POPUP_LABEL_TOGGLE_CODE_DISPLAY       = "Toggle Code Display";
  
  public FTreeTablePanel(FTree fTree) {
  	this();
  	setTree(fTree);
  }

  public FTreeTablePanel() {
  	super();
  }
  
  public void dispose(){
    if(fTable != null){
      fTable.removeMouseListener(this);
    }     
    
    super.dispose();
    toggleCodeDisplay = null;    
    expandAllAction = null;
    collapseAllAction = null;
    newAction = null;
    deleteAction = null;
    editAction = null;
    columnSelectorAction = null;
    filterAction = null;
    duplicateAction = null;
    expandUpToThisLevelAction = null;
    collapseUpToThisLevelAction = null;
    moveUpAction = null;
    moveDownAction = null;
    expandAll = null;
    collapseAll = null;
  }

  public FTree getTree(){
  	return getTableModel() != null ? ((FTreeTableModel)getTableModel()).getTree() : null;
  }
  
  public void setTree(FTree tree){
  	if(this.fTableModel == null){
	    this.fTableModel = new FTreeTableModel(tree);
	    getTableView().addTreeColumn(((FTreeTableModel)fTableModel).getGuiTree());
  	}else{
  		Globals.logString("setTree should be used once. This time it is neglected");
  	}
  }
  
  public void refreshTree(){
    ((FTreeTableModel)fTableModel).refreshGui();
  }

  public void expandAll(){
  	if(fTableModel != null && ((FTreeTableModel)fTableModel).getGuiTree() != null){
  		((FTreeTableModel)fTableModel).getGuiTree().expandAll();
  	}
  }

  public void collapseAll(){
  	if(fTableModel != null && ((FTreeTableModel)fTableModel).getGuiTree() != null){
  		((FTreeTableModel)fTableModel).getGuiTree().collapseAll();
  	}
  }
  
  public void construct(){
  	super.construct();
    //fTable.addMouseListener(this);
  	
    // -----------------------
    // Toggle Code Display
    // -----------------------
    FGButtonAction toggleCodeDisplayAction = getToggleCodeDisplayAction();
    if(toggleCodeDisplayAction != null){
      getTable().getActionMap().put("toggleCodeDisplay", toggleCodeDisplayAction);
    }
    
    // -----------------------
    // Expand all Button
    // -----------------------
    FGButtonAction expandAllAction = getExpandAllAction();
    if(expandAllAction != null){
      expandAll = new FGButton(Globals.getIcons().getExpandAllIcon());
      StaticComponent.setComponentToolTipText(expandAll, "Expand all nodes");
      expandAll.setName(getButtonName(BUTTON_EXPAND_ALL));
      expandAllAction.setAssociatedButton(expandAll);
      expandAll.addActionListener(expandAllAction);    
      buttonsPanel.addButton(expandAll);
      getTable().getActionMap().put("expandAll", expandAllAction);
    }
    
    // -----------------------
    // Collapse all Button
    // -----------------------
    FGButtonAction collapseAllAction = getCollapseAllAction();
    if(collapseAllAction != null){
      collapseAll = new FGButton(Globals.getIcons().getCollapseAllIcon());
      StaticComponent.setComponentToolTipText(collapseAll, "Collapse all nodes");
      collapseAll.setName(getButtonName(BUTTON_COLLAPSE_ALL));
      collapseAllAction.setAssociatedButton(collapseAll);
      collapseAll.addActionListener(collapseAllAction);    
      buttonsPanel.addButton(collapseAll);
      getTable().getActionMap().put("collapseAll", collapseAllAction);
    }
    
    // -----------------------
    // Collapse Up To This Level
    // -----------------------
    FGButtonAction collapseUpToThisLevelAction = getCollapseUpToThisLevelAction();
    if(collapseUpToThisLevelAction != null){
      getTable().getActionMap().put("collapseUpToThisLevel", collapseUpToThisLevelAction);
    }
    
    // -----------------------
    // Expand Up To This Level
    // -----------------------
    FGButtonAction expandUpToThisLevelAction = getExpandUpToThisLevelAction();
    if(expandUpToThisLevelAction != null){
      getTable().getActionMap().put("expandUpToThisLevel", expandUpToThisLevelAction);
    }
    
 		addButtonActionToMap_MoveUp_MoveDown();
    
    showCollapseAllButton(true);
  	showExpandAllButton(true);
    
    showCollapseUpToThisLevelButton(true);
    showExpandUpToThisLevelButton(true);
    
    if( getFocList() != null ){
      FocDesc focDesc = getFocList().getFocDesc();
      if( focDesc != null ){
        showMoveUpButton(focDesc.hasOrderField());
        showMoveDownButton(focDesc.hasOrderField());            
      }
    }
    if(getTree().isWithNodeCollapseSaving()){
	    getTree().scan(new TreeScanner<FNode>() {
				@Override
				public boolean beforChildren(FNode node) {
					//boolean carryOn  = true;
					boolean collapse = false;
					if(node != null && node.getObject() != null && ((FocObject)(node.getObject())).isNodeCollapsed()){
						collapse = true;
					}
					FTreeTableModel treeModel = (FTreeTableModel) getTableModel();
					int row = treeModel.getRowForNode(node);
					if(collapse){
						getGuiTree().collapseRow(row);
					}else{
						getGuiTree().expandRow(row);
					}
					
					return !collapse;
				}
	
				@Override
				public void afterChildren(FNode node) {
				}
			});
    }
    
    /*
    FocList list getFocList();
    for(int i=0; i<; i++){
    	
    }
    */
  }
  
  public void showExpandAllButton(boolean show) {
    if(expandAll != null) expandAll.setVisible(show);
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getExpandAllStroke(), "expandAll");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getExpandAllStroke());
    }
  }
  
  public void showCollapseAllButton(boolean show) {
    if(collapseAll != null) collapseAll.setVisible(show);
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getCollapseAllStroke(), "collapseAll");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getCollapseAllStroke());
    }
  }
  
  public void showExpandUpToThisLevelButton(boolean show) {
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getExpandUpToThisLevelStroke(), "expandUpToThisLevel");
      addPopupItem(Globals.getIcons().getExpandAllIcon(), POPUP_LABEL_EXPAND_UP_TO_THIS_LEVEL, getExpandUpToThisLevelAction());
      
      //getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getExpandUpToThisLevelStroke(), "expandUpToThisLevel");
      addPopupItem(Globals.getIcons().getExpandAllIcon(), POPUP_LABEL_DEEP_EXPAND, getDeepExpandAction());
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getExpandUpToThisLevelStroke());
      removePopupItem(POPUP_LABEL_EXPAND_UP_TO_THIS_LEVEL);
    }
  }

  public void showToggelCodeButton(boolean show) {
    if (show) {
      addPopupItem(null, POPUP_LABEL_TOGGLE_CODE_DISPLAY, getToggleCodeDisplayAction());
    } else {
      removePopupItem(POPUP_LABEL_TOGGLE_CODE_DISPLAY);
    }
  }
  
  public void showCollapseUpToThisLevelButton(boolean show) {
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getCollapseUpToThisLevelStroke(), "collapseUpToThisLevel");
      addPopupItem(Globals.getIcons().getCollapseAllIcon(), POPUP_LABEL_COLLAPSE_UP_TO_THIS_LEVEL, getCollapseUpToThisLevelAction());
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getCollapseUpToThisLevelStroke());
      removePopupItem(POPUP_LABEL_COLLAPSE_UP_TO_THIS_LEVEL);
    }
  }
    
  public FGTreeInTable getGuiTree(){
  	FGTreeInTable treeInTable = null;
		FTreeTableModel tableModle = (FTreeTableModel)getTableModel();
		if(tableModle != null){
			treeInTable = tableModle.getGuiTree();
		}
		return treeInTable;
  }
  
  public FNode getNodeAt(int row){
  	FNode  node  = null;
  	FTable table = getTable();
  	if(table != null){
	  	FTreeTableModel treeTableModle = (FTreeTableModel)table.getTableModel();
		  if(treeTableModle != null){
		  	node = treeTableModle.getNodeForRow(row);
		  }
  	}
  	return node;
  }
  
  public FNode getSelectedNode(){
  	return getTable() != null ? getNodeAt(getTable().getSelectedRow()) : null;
  }
  
	@Override
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

	@Override
  public FGButtonAction getDeleteAction(){
    if(deleteAction == null){
    	deleteAction = new FGButtonAction(null){
        public void focActionPerformed(ActionEvent e){
          try{
            SwingUtilities.invokeLater(new Runnable(){
              public void run(){
                deleteItem();               
              }
            });
          }catch (Exception e1){
            Globals.logException(e1);
          }
        }
      };
    }
    return deleteAction;
  }

	//-----------------------
  // Edit Action
  // -----------------------
    
	@Override
  public void editCurrentItem(FocObject focObj){
    InternalFrame internalFrame = null;
    Boolean restorSuccecfull = false;
    FPanel focObjPanel = null;
  	
    FNode node = focObj != null ? getTree().findNodeFromFocObject(focObj) : null;
    
    if(node != null && focObj != null && focObj.getFatherSubject() == null){
    	//In this case we are in a criteria tree on a node not leaf
    	//Or in a Object Tree on the root that is disconnected and simply has an item for computation
    	Globals.getDisplayManager().popupMessage("Please select a valid line");
    }else if (focObj != null) {
  		Globals.setMouseComputing(true);
      focObj.backup();
      
      int viewId = node != null ? node.getDetailsPanelViewId() : FNode.NO_SPECIFIC_VIEW_ID;
    	if(viewId == FNode.NO_SPECIFIC_VIEW_ID){
    		viewId = getTableView().getDetailPanelViewID();
    	}
      if (shouldCreateNewInternalFrame(focObj)){
      	focObjPanel = newDetailsPanelForFocObject(focObj, viewId);
        if( focObjPanel != null ){
          setSelectedObject(focObj);
          internalFrame = Globals.getDisplayManager().newInternalFrame(focObjPanel);
          associateInternalFrameToObject(focObj,internalFrame);
        }
        restorSuccecfull = true;
      }else{
        restorSuccecfull = Globals.getDisplayManager().restoreInternalFrame(getInternalFrameAssociatedToObject(focObj));
      }
      if (!restorSuccecfull){
        //discardAssociatedInternalFrame(focObj);
      	focObjPanel = newDetailsPanelForFocObject(focObj, viewId);
        internalFrame = Globals.getDisplayManager().newInternalFrame(focObjPanel);
        associateInternalFrameToObject(focObj,internalFrame);
      }
  		Globals.setMouseComputing(false);
    }
  }
  
  public void editCurrentItem(){
    try {
      FTable table = getTable();
      if(table != null){
        int row = table.getSelectedRow();
        FTreeTableModel treeTableModle = (FTreeTableModel)table.getTableModel();
        FNode node = treeTableModle.getNodeForRow(row);
        FocObject focObj = node != null ? node.getFocObjectToShowDetailsPanelFor() : null;
        if(focObj != null){
        	editCurrentItem(focObj);
        }else{
        	Globals.getDisplayManager().popupMessage("Select an Item first.");
        }
      }
    } catch (Exception e2) {
      Globals.logException(e2);
    } 
  }
  
  @Override
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

	@Override
	public FGButtonAction getEditCellAction() {
		return null;
	}

  @Override
  public FGButtonAction getFilterAction() {
    if (filterAction == null) {
      filterAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          try {
            FTreeTableModel model = (FTreeTableModel) getTable().getModel();
            FocListFilter filter = model.getFilter();
            if(filter != null){
              FPanel panel = filter.newDetailsPanel(0);
              Globals.getDisplayManager().popupDialog(panel, "Filter", true);
            }
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return filterAction;
  }
	/*public FGButtonAction getFilterAction() {
		return null;
	}*/

  @Override
	public FGButtonAction getNewAction() {
    if (newAction == null) {
      newAction = new FGButtonAction(null) {
        /**
         * 
         */
        private static final long serialVersionUID = -3052616839523635420L;

        public void focActionPerformed(ActionEvent e) {

          try {
            SwingUtilities.invokeLater(new Runnable(){
              public void run() {
              	try{
              		newEmptyItem();
              	}catch(Exception e){
              		Globals.logException(e);
              	}
              }
            });

          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return newAction;
	}
  
	public void popupSelectANodeDialog(){
		Globals.getApp().getDisplayManager().popupMessage("Select a node first.");
	}
	
	public FTree getFTree(){
		return ((FTreeTableModel)fTableModel).getTree();
	}
  
	/*
  private void addGuiFieldsForCriteriaTree(FocObject newFocObj, GuiDetails detailsViewDefinition){
    
    FCriteriaTree tree =  (FCriteriaTree)getFTree();
    int nodeLevelsCount = tree.getTreeDesc().getNodeLevelsCount();
    int counterY = 0;
    ArrayList<FField> guiFieldList = new ArrayList<FField>();
    
    for( int i = 0; i < nodeLevelsCount; i++){
      FFieldPath path = tree.getTreeDesc().getNodeLevelAt(i).getPath();
      
      if( path != null ){
        FField fField = newFocObj.getFocProperty(path.get(0)).getFocField();
        GuiDetailsComponent guiDetailsComponent = detailsViewDefinition.addGuiDetailsFieldForFField(fField);
        guiFieldList.add(fField);
        alignGuiDetailComponents(guiDetailsComponent, 0, counterY++);
      }
    }
    
    FocFieldEnum iter = new FocFieldEnum(newFocObj.getThisFocDesc(), newFocObj, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext()){
      iter.next();
      FProperty prop = (FProperty) iter.getProperty();
      if(prop != null){
        FField field = prop.getFocField();
        if ( field != null && field.isMandatory() && !guiFieldList.contains(field)){
          GuiDetailsComponent guiDetailsComponent = detailsViewDefinition.addGuiDetailsFieldForFField(field);
          alignGuiDetailComponents(guiDetailsComponent, 0, counterY++);
        }
      }
    }
  }
  
  private void alignGuiDetailComponents(GuiDetailsComponent guiDetailsComponent, int x, int y){
    if(guiDetailsComponent != null){
      guiDetailsComponent.setX(0);
      guiDetailsComponent.setY(y);
    }
  }
	*/
	
  @SuppressWarnings("unchecked")
	public FocObject insertNode(FNode fatherNode){
    FTree tree = getFTree();
    FocObject newlyCreatedObject = tree.newEmptyItem(fatherNode);
    ((FTreeTableModel)fTableModel).refreshGui();
    return newlyCreatedObject;
  }
  
  protected FocObject newListItem() {
    FocObject newFocObject = null;
    int row = getTable().getSelectedRow();
    FNode node = ((FTreeTableModel)fTableModel).getNodeForRow(row);
    newFocObject = insertNode(node);
    
    return newFocObject;
  }
  
  public FocObject newEmptyItem() {
    int row = getTable().getSelectedRow();
    FocObject newFocObj = super.newEmptyItem();
    
    FNode newlyCreatedNode = getFTree().findNodeFromFocObject(newFocObj);;
    
    FTreeTableModel tableModle  = (FTreeTableModel)getTableModel();
    FGTreeInTable   treeInTable = tableModle.getGuiTree();
    treeInTable.expandRow(row);
    
    if(newlyCreatedNode != null){
      FixedColumnScrollPane scrollPane = getTable().getScrollPane();
      getTable().changeSelection(tableModle.getRowForNode(newlyCreatedNode), 0, false, false);    
      
      if(scrollPane.getFixedTable() != null){
      	tableModle = (FTreeTableModel) scrollPane.getFixedTable().getTableModel();
      }
      
      newFocObj = (FocObject) newlyCreatedNode.getObject();
      SwingUtilities.invokeLater(new StartEditingNewNode(tableModle, newlyCreatedNode));  
    }
    
    return newFocObj;
  }
  
  public class StartEditingNewNode implements Runnable{
  	private FTreeTableModel tableModle       = null;
  	private FNode           newlyCreatedNode = null;
  	
  	public StartEditingNewNode(FTreeTableModel tableModle, FNode newlyCreatedNode){
  		this.tableModle = tableModle;
  		this.newlyCreatedNode = newlyCreatedNode;
  	}
  	
  	private void dispose(){
      tableModle       = null;
      newlyCreatedNode = null;
  	}
  	
    public void run() {
      int row = tableModle.getRowForNode(newlyCreatedNode);
      if( row != -1 ){
      	FTable table = getTable();
      	FixedColumnScrollPane scrollPane = getTable() != null ? getTable().getScrollPane() : null;
      	if(scrollPane != null && scrollPane.getFixedTable() != null){
      		table = scrollPane.getFixedTable();
      	}
        	
        table.editCellAt(row, 0);
        Component component = table.getEditorComponent();
        if(component != null){
        	component.requestFocusInWindow();
	        if(component instanceof JTextField){
	          ((JTextComponent)component).setSelectionStart(0);  
	        }else if(component instanceof TreeTableTextField){
	        	component = ((TreeTableTextField)component).getTitleComp();
	        	if(component instanceof JTextField){
	        		component.requestFocusInWindow();
	        		final Component compFinal = component;
	        		SwingUtilities.invokeLater(new Runnable(){
								public void run() {
									compFinal.requestFocusInWindow();
			        		((JTextComponent)compFinal).setSelectionStart(0);
			        		((JTextComponent)compFinal).setCaretPosition(0);
			        		compFinal.requestFocusInWindow();
								}
	        		});
	        	}
	        }
        }

      }
      tableModle = null;
      dispose();
    }
  }
  
  public boolean deleteItem() {
    FTree tree = ((FTreeTableModel)fTableModel).getTree();
    int   row  = getTable().getSelectedRow();
    FNode node = ((FTreeTableModel)fTableModel).getNodeForRow(row);
    boolean ret = tree.deleteNode(((FTreeTableModel)fTableModel), node);
    if(getFilterExpressionPanel() != null) getFilterExpressionPanel().executeFind();
    return ret;
  }
  
	@Override
	public FGButtonAction getSelectAction() {
		return null;
	}

	public FGButtonAction getExpandAllAction() {
    if (expandAllAction == null) {
    	expandAllAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          try {
          	FTreeTableModel tableModle = (FTreeTableModel)getTableModel();
          	if(tableModle != null){
          		FGTreeInTable treeInTable = tableModle.getGuiTree();
          		if(treeInTable != null){
          			treeInTable.expandAll();
          		}
          	}
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return expandAllAction;
  }

	//-----------------------
  // Expand Up To The Level Action
  // -----------------------
  
  public FGButtonAction getExpandUpToThisLevelAction() {
    if (expandUpToThisLevelAction == null) {
      expandUpToThisLevelAction = new FGButtonAction(null) {

        public void focActionPerformed(ActionEvent e) {
          try {
            int r = getTable().getSelectedRow();
            if(r != -1){
              FNode node = ((FTreeTableModel)getTableModel()).getNodeForRow(r);
              final FGTreeInTable guiTree = ((FTreeTableModel)getTableModel()).getGuiTree();
              guiTree.expandAll();
              
              TreeCollapseScanner collScanner = new TreeCollapseScanner((FTreeTableModel)getTableModel(), node.getNodeDepth());
              ((FTreeTableModel)getTableModel()).getTree().scan(collScanner);
              collScanner.dispose();
            }
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return expandUpToThisLevelAction;
  }

	// -----------------------
  // Deep Expand Action
  // -----------------------
  
  public FGButtonAction getDeepExpandAction() {
    if (deepExpandAction == null) {
    	deepExpandAction = new FGButtonAction(null) {

        public void focActionPerformed(ActionEvent e) {
          try {
            int r = getTable().getSelectedRow();
            if(r != -1){
              FNode node = ((FTreeTableModel)getTableModel()).getNodeForRow(r);
              node.scan(new TreeScanner<FNode>(){

                public void afterChildren(FNode node) {
                  
                }

                public boolean beforChildren(FNode node) {
                  FGTreeInTable guiTree = ((FTreeTableModel)getTableModel()).getGuiTree();    
                  int row = ((FTreeTableModel)getTableModel()).getRowForNode(node);
                  guiTree.expandRow(row);
                  return true;
                }
              });
            }
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return deepExpandAction;
  }

  // -----------------------
  // Toggle Code Display
  // -----------------------
  public FGButtonAction getToggleCodeDisplayAction() {
    if(toggleCodeDisplay == null){
    	toggleCodeDisplay = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          try{
          	FTree tree = ((FTreeTableModel)getTableModel()).getTree();
          	tree.setHideCode(!tree.isHideCode());
          	((FTreeTableModel)getTableModel()).refreshGui();
          }catch(Exception e1){
            Globals.logException(e1);
          }
        }
      };
    }
    return toggleCodeDisplay;
  }
  
  // -----------------------
  // Collapse Up To The Level Action
  // -----------------------

  public void expandRoot(){
  	getGuiTree().expandRow(0);
  }
  
  public void collapseUpoToThisLevel(int nodeDepth){
	  TreeCollapseScanner collScanner = new TreeCollapseScanner((FTreeTableModel) getTableModel(), nodeDepth);
	  ((FTreeTableModel)getTableModel()).getTree().scan(collScanner);
	  collScanner.dispose();
  }
  
  public FGButtonAction getCollapseUpToThisLevelAction() {
    if (collapseUpToThisLevelAction == null) {
      collapseUpToThisLevelAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          try {
            int r = getTable().getSelectedRow();
            if( r != -1 ){
              FNode node = ((FTreeTableModel)getTableModel()).getNodeForRow(r);
              collapseUpoToThisLevel(node.getNodeDepth());
            }
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return collapseUpToThisLevelAction;
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
            if( r != -1 ){
              FNode upperNode = ((FTreeTableModel)getTableModel()).getNodeForRow(r);
              ((FTreeTableModel)getTableModel()).getTree().moveDown(upperNode);
              ((FTreeTableModel)getTableModel()).refreshGui();
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
            if( r != -1 ){
              FNode lowerNode = ((FTreeTableModel)getTableModel()).getNodeForRow(r);
              ((FTreeTableModel)getTableModel()).getTree().moveUp(lowerNode);
              ((FTreeTableModel)getTableModel()).refreshGui();
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
              FTable ftable = getTable();
              int row = ftable.getSelectedRow();
              if(row >= 0){
                FocObject sourceObj = (FocObject) ftable.getElementAt(row);
                
                if(sourceObj != null){
                  /*FocObject targetObj = */sourceObj.duplicate(newEmptyItem(), sourceObj.getMasterObject(), true, false);
                  //getFocList().add(targetObj);
                }
              }
              
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
  
	public FGButtonAction getCollapseAllAction() {
    if (collapseAllAction == null) {
    	collapseAllAction = new FGButtonAction(null) {
        private static final long serialVersionUID = 1L;

        public void focActionPerformed(ActionEvent e) {
          try {
          	FTreeTableModel tableModle = (FTreeTableModel)getTableModel();
          	if(tableModle != null){
          		FGTreeInTable treeInTable = tableModle.getGuiTree();
          		if(treeInTable != null){
          			treeInTable.collapseAll();
          		}
          	}
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return collapseAllAction;
  }

	@Override
	public FGButtonAction getRedirectAction() {
		return null;
	}
}
