/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.gui.treeTable;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.dragNDrop.FocTransferable;
import com.foc.event.FocEvent;
import com.foc.event.FocListener;
import com.foc.gui.DisplayManager;
import com.foc.gui.table.FAbstractTableModel;
import com.foc.gui.table.FTable;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.cellControler.TreeCellControler;
import com.foc.gui.tree.FTreeModel;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.filter.FocListFilterListener;
import com.foc.list.filter.IFocListFilter;
import com.foc.property.FProperty;
import com.foc.tree.FNode;
import com.foc.tree.FTree;
 
@SuppressWarnings({ "serial", "unchecked" })
public class FTreeTableModel extends FAbstractTableModel implements FocListener, FocListFilterListener{

  protected FGTreeInTable                   gTree        = null;
	protected EventListenerList               listenerList = null;
  private   FTable                          table        = null;
  private   FTree                           tree         = null;
  private   TreeExpansionListener           treeExpansionListener = null;
  private   ListToTreeSelectionModelWrapper selectionWrapper      = null;
  
  public FTreeTableModel(FTree tree) {
    refreshTree(tree);
    this.tree = tree;
    startListeningToListEvents_IfNecessary();
    IFocListFilter focListFilter = tree.getFocListFilter();
    if(focListFilter != null){
      focListFilter.addListener((FocListFilterListener)this);
    }
  }
  
  public void dispose(){
    super.dispose();
    unplugTreeListeners();
    if(tree != null){ 
//      for(int i=0; i < tree.getFocListFilter().getListenersCount(); i++){
      IFocListFilter focListFilter = tree.getFocListFilter();
      if(focListFilter != null){
        focListFilter.removeListener((FocListFilterListener)this);
      }
//      }
    }
    disposeTree();
    listenerList = null;
    table = null;
    FocList list = getFocList();
    if(list != null){
    	list.removeFocListener(this);
    }
    tree = null;
  }
  
  public void disposeTree(){
    if(gTree != null){
      //removeListeners();
      gTree.dispose();
      gTree = null;
    }
  }
  
  public void startListeningToListEvents_IfNecessary(){
    FocList list = getFocList();
    if(list != null && tree.isAutomaticlyListenToListEvents()){
    	list.addFocListener(this);
    }
  }

  public void stopListeningToListEvents_IfWasListening(){
    FocList list = getFocList();
   	list.removeFocListener(this);
  }

  @Override
  public Color getCellColor(int row, int col) {
  	
  	Color color = super.getCellColor(row, col);
  	if(color == null && tree != null){
  		FNode node = getNodeForRow(row);
  		if(node != null){
  			color = tree.getColorForNode(node, row, getGuiTree());
  		}
  	}
    return color != null ? color : Color.WHITE;
  }

  public void refreshTree(FTree tree) {
    refreshTree(tree, false);
  }
  
  public void refreshTree(FTree tree, boolean copyExpansion) {
    FGTreeInTable gNewTree = new FGTreeInTable(new FTreeModel(tree));
    if(copyExpansion){
      gNewTree.copyExpansion(gTree);
    }
    disposeTree();
    gTree = gNewTree;
    
    //levelsColors = new ArrayList<Color>();
    //tree.initLevelsColors();
    
    fireTableDataChanged();

    plugTreeListeners();
  }

  @Override
  public void refreshGui() {
  	if(!isSuspendGuiRefresh()){
	    super.refreshGui();
	    //fireTableDataChanged();
	    if(gTree != null) gTree.updateUI();
  	}
  }
  
	public void refreshTreeFromList(){
		if(this.tree != null){
			ArrayList<ArrayList<String>> expandedPathesNodesTitle = this.gTree.getExpandedPathesNodesTitles();
			this.tree.growTreeFromFocList(getFocList());
			this.tree.sort();
			//refreshTree(this.tree);
			refreshGui();
			this.gTree.expandPathes(expandedPathesNodesTitle);
			/*Iterator<ArrayList<String>> iter = expandedPathesNodesTitle.iterator();
		  while(iter != null && iter.hasNext()){
		  	ArrayList<String> nodesPathTitles = iter.next();
		  	FNode[] objectPath = new FNode[nodesPathTitles.size()];
		  	boolean targetPathContainsNullVaLues = false;
		  	for(int i = 0; i < nodesPathTitles.size() && !targetPathContainsNullVaLues; i++){
		  		String nodeTitle = nodesPathTitles.get(i);
		  		FNode node = this.gTree.getNodeByTitle(nodeTitle);
		  		if(node == null){
		  			targetPathContainsNullVaLues = true;
		  		}
		  		objectPath[i] = node;
		  	}
		  	if(!targetPathContainsNullVaLues){
		  		TreePath targetPath = new TreePath(objectPath);
		  		this.gTree.expandPath(targetPath);
		  	}
		  }*/
		}
  }
    
  /*  
  public void removeListeners(){
    removeTreeExpansionListeners();
    removeTreeSelectionListeners();
    removeTreeWillExpandListeners();
    //removeListSelectionListeners();
  }

  private void removeTreeExpansionListeners(){
    if(gTree != null){
      TreeExpansionListener[] expansionLiteners =  gTree.getTreeExpansionListeners();
      for(int i = 0; i < expansionLiteners.length; i++){
        TreeExpansionListener expansionListener = expansionLiteners[i];
        gTree.removeTreeExpansionListener(expansionListener);
      }
    }
  }

  private void removeTreeSelectionListeners(){
    if(gTree != null){
      TreeSelectionListener[] selectionLiteners =  gTree.getTreeSelectionListeners();
      for(int i = 0; i < selectionLiteners.length; i++){
        TreeSelectionListener expansionListener = selectionLiteners[i];
        gTree.removeTreeSelectionListener(expansionListener);
      }
    }
  }
  
  private void removeTreeWillExpandListeners(){
    if(gTree != null){
      TreeWillExpandListener[] willExpandLiteners =  gTree.getTreeWillExpandListeners();
      for(int i = 0; i < willExpandLiteners.length; i++){
        TreeWillExpandListener expansionListener = willExpandLiteners[i];
        gTree.removeTreeWillExpandListener(expansionListener);
      }
    }
  }
  
  private void removeListSelectionListeners(){
    if(gTree != null){
      TreeSelectionModel listSelectionModelWraper = gTree.getSelectionModel();
      if(listSelectionModelWraper != null){
        ListSelectionModel defaultListSelectionModel = ((ListToTreeSelectionModelWrapper)listSelectionModelWraper).getListSelectionModel();
        if(defaultListSelectionModel != null){
          ListSelectionListener[] listSelectionListeners = ((DefaultListSelectionModel)defaultListSelectionModel).getListSelectionListeners();
          if(listSelectionListeners != null){
            for(int i = 0; i < listSelectionListeners.length; i++){
              ListSelectionListener listener = listSelectionListeners[i];
              defaultListSelectionModel.removeListSelectionListener(listener);
            }
          }
        }
      }
    }
  }
  */
	
	public FGTreeInTable getGuiTree(){
		return gTree;
	}

	public FTree getTree(){
		return (getGuiTree() != null && getGuiTree().getModel() != null) ? ((FTreeModel)getGuiTree().getModel()).getTree() : null;
	}
  
  public FNode getNodeForRow(int row){
    TreePath treePath = getGuiTree().getPathForRow(row);
    FNode node = treePath != null ? (FNode) treePath.getLastPathComponent() : null;
    return node;
  }
  
  public int getRowForNode( FNode node ){
    int row = -1;
    if(node != null){
	    int nodeDepth = node.getNodeDepth()+1;
	    Object[] objPath = new Object[nodeDepth];
	    int index = objPath.length -1;
	    objPath[index--] = node;
	    while( node.getFatherNode() != null ){
	      node = node.getFatherNode();
	      objPath[index--] = node;
	    }
	    TreePath path = new TreePath(objPath);
	    
	    //TEMP
	    try {
	      row = getGuiTree().getRowForPath(path);  
	    }catch(Exception e){
	      row = -1;
	    }
	    //TEMP
    }
    return row;
  }
	
	@Override
	public FocObject getRowFocObject(int row) {
		FNode node = getNodeForRow(row);		
		return node != null ? (FocObject) node.getObject() : null;
	}

	public int getRowCount() {
		int rowCount = 0;
		if(getGuiTree() == null){
      Globals.logString("Gui Tree Null");
    }else{
    	rowCount = getGuiTree().getRowCount();
    }
    return rowCount;
	}
	
	public FocListElement getRowListElement(int row){
  	FocList list = getFocList(); 
    FocListElement element = list != null ? list.getFocListElement(row) : null;
    return element;
  }
	
	@Override
	public FProperty getSpecialFProperty(FTableColumn tc, FocObject rowObject, int row, int col) {
  	FProperty objectProperty = super.getSpecialFProperty(tc, rowObject, row, col);
		
  	if(objectProperty == null){
	    if(tc.getID() == FField.TREE_FIELD_ID){
	      objectProperty = getTree().getTreeSpecialProperty(getNodeForRow(row));  
	    }
  	}
    return objectProperty;
	}
  
  public void setValueAt(Object aValue, int row, int column) {
    super.setValueAt(aValue, row, column);

    //This additional part to change the Node title in memory when it is edited
    FProperty prop = getFProperty(row, column);
    FTableColumn tc = (FTableColumn) tableView.getColumnAt(column);

    if (prop != null && tc != null) {
      if(tc.getID() == FField.TREE_FIELD_ID){
        FNode node = getNodeForRow(row);
        if(node != null){
          node.setTitle(prop.getString());
        }
      }
    }
  }
  
  public Object getValueAt(int row, int col){
    Object obj = super.getValueAt(row, col);
    
    FProperty prop = getFProperty(row, col);
    if(prop != null){
      FField field = prop.getFocField();
      
      if(field != null && prop.isInherited()){
      	if(field.getInheritedPropertyGetter() == null){
	        FNode node = getNodeForRow(row);
	        if(node != null){
	          FProperty firstCustomizedProp = node.getFirstAncestorCustomizedProperty(field.getID());
	          if(firstCustomizedProp != null){
	            FTableColumn tc = (FTableColumn) tableView.getColumnAt(col);
	            obj = firstCustomizedProp.getTableDisplayObject(tc.getFormat());
	          }
	        }
      	}
      }
    }
    
    if(obj == null){
    	FTableColumn tc = (FTableColumn) tableView.getColumnAt(col);
    	if(tc != null && tc.getID() == FField.TREE_FIELD_ID){
    		obj = gTree;
    	}
    }
    return obj;
  }
  
  public Color getDefaultBackgroundColor(Color bg, Component comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		TreePath treePath = getGuiTree().getPathForRow(row);
		FNode node = (FNode) treePath.getLastPathComponent();
    
		//System.out.println("Color list "+node.getLevelDepth());
    return node.isLeaf() ? Color.WHITE : tree.getBackgroundColorForLevel(node.getLevelDepth()) /*levelsColors.get(node.getLevelDepth()*/;		
  }

  public boolean isCellEditable(int row, int col) {
    if (tableView != null) {
      FTableColumn tableColumn = tableView.getColumnAt(col);
      if (tableColumn != null) {
      	if(tableColumn.getID() == FField.TREE_FIELD_ID){
          return true;
        }
      }
    }
  	return super.isCellEditable(row, col);
  }

	public void setRowHeight(int rowHeight) {
		if(gTree.getRowHeight() != rowHeight){
			gTree.setRowHeight(rowHeight);
		}
		/*
		if(table != null && table.getRowHeight() != rowHeight){
			table.setRowHeight(rowHeight);
		}
		*/
	}

  public void plugTreeListeners(){
    if(table != null){
      unplugTreeListeners();
      treeExpansionListener = new TreeExpansionListener() {
      	
      	private void nodeCollapseStatusSave(TreeExpansionEvent event, boolean collapse){
        	if(getTree().isWithNodeCollapseSaving()){
	        	Object 		pathObjects[] = event.getPath().getPath();
	        	FNode     lastNode     	= (FNode) pathObjects[pathObjects.length - 1];
	        	FocObject lastObj 			= lastNode != null ? (FocObject) lastNode.getObject() : null;
	        	if(lastObj != null) lastObj.setNodeCollapsed(collapse);
        	}
      	}
      	
        // Don't use fireTableRowsInserted() here; the selection model
        // would get updated twice.
        public void treeExpanded(TreeExpansionEvent event) {
        	nodeCollapseStatusSave(event, false);
        	
        	if(!gTree.isHoldTreeExpansionReaction()){
	          fireTableDataChanged();
	          if(table != null && table.getScrollPane() != null){
	          	table.getScrollPane().refreshRowHeaderSize();
	          }
        	}
        }
    
        public void treeCollapsed(TreeExpansionEvent event) {
        	nodeCollapseStatusSave(event, true);

        	if(!gTree.isHoldTreeExpansionReaction()){
	          fireTableDataChanged();
	          if(table != null && table.getScrollPane() != null){
	          	table.getScrollPane().refreshRowHeaderSize();
	          }
        	}
        }
      };
      
      gTree.addTreeExpansionListener(treeExpansionListener);
      
      // Force the JTable and JTree to share their row selection models.
      selectionWrapper = new ListToTreeSelectionModelWrapper();
      gTree.setSelectionModel(selectionWrapper);
      table.setSelectionModel(selectionWrapper.getListSelectionModel());
    }
  }

  public void unplugTreeListeners(){
    if(table != null){
      gTree.removeTreeExpansionListener(treeExpansionListener);  
      selectionWrapper = null;
      treeExpansionListener = null;
    }
  }

  public FocList getFocList(){
  	FocList list = null;
  	if(this.tree != null){
  		list = tree.getFocList();
  	}
  	return list;
  }
  
  @Override
	public void afterTableConstruction(final FTable table) {
    
    if(getFocList().isKeepNewLineFocusUntilValidation()){
      table.addFocusListener(table);
    }
    
    //table.addFocusListener(table);
    /*table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent e) {
        table.reactToFocusChange(false);
      }
    });
     table.requestFocus();
    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    table.setInputVerifier(FTable.getTableLockInputVerifier());*/

    
		// No grid.
		// table.setShowGrid(false);

		// No intercell spacing
		// table.setIntercellSpacing(new Dimension(0, 0));

		// And update the height of the trees row to match that of
		// the table.
		if (gTree.getRowHeight() < 1) {
			// Metal looks better like this.
			if(Globals.getDisplayManager().getLookAndFeel() == DisplayManager.LOOK_AND_FEEL_METAL){
				table.setRowHeight(20);
			}
		}
		
		FTableColumn fCol = getTableView().getColumnById(FField.TREE_FIELD_ID);
		if(fCol != null){
			TreeCellControler controler = (TreeCellControler)fCol.getCellEditor();
			if(controler != null){
        controler.setTable(table);
			}
		}
    
    this.table = table;
    plugTreeListeners();
    if(getFocList() != null ){
      plugListListenerToCellPropertiesModifications();  
    }
    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		
		// Install a TreeModelListener that can update the table when
		// tree changes. We use delayedFireTableDataChanged as we can
		// not be guaranteed the tree will have finished processing
		// the event before us.
		
		  /*gTree.getModel().addTreeModelListener(new TreeModelListener() { 
        
       public void treeNodesChanged(TreeModelEvent e) { 
         delayedFireTableDataChanged(); 
       }
  		 
  		 public void treeNodesInserted(TreeModelEvent e) {
  		   delayedFireTableDataChanged(); 
       }
  		 
  		 public void treeNodesRemoved(TreeModelEvent e) {
  		   delayedFireTableDataChanged(); 
       }
  		 
  		 public void treeStructureChanged(TreeModelEvent e) {
  		   delayedFireTableDataChanged(); 
         } 
       });*/
		 
	}
	
	/**
	 * Invokes fireTableDataChanged after all the pending events have been
	 * processed. SwingUtilities.invokeLater is used to handle this.
	 */
	protected void delayedFireTableDataChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				fireTableDataChanged();
			}
		});
	}
	
  // --------------------------
  // Drag implementation
  // --------------------------
  public void fillSpecificDragInfo(FocTransferable focTransferable){
  	if(focTransferable != null){
	  	super.fillSpecificDragInfo(focTransferable);
	  	int selectedRow = focTransferable.getTableSourceRow();
	  	focTransferable.setSourceNode(getNodeForRow(selectedRow));
  	}
  }
	
	//--------------------------
  // FocListener implementation
  // --------------------------
	public void focActionPerformed(FocEvent evt){
		if(this.tree != null && this.tree.isListListenerEnabled()){
			if (evt.getID() == FocEvent.ID_ITEM_ADD || evt.getID() == FocEvent.ID_ITEM_REMOVE || evt.getID() == FocEvent.ID_ITEM_MODIFY || evt.getID() == FocEvent.ID_WAIK_UP_LIST_LISTENERS) {
        refreshTreeFromList();
			}
		}
	}
	
	/**
	 * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel to listen
	 * for changes in the ListSelectionModel it maintains. Once a change in the
	 * ListSelectionModel happens, the paths are updated in the
	 * DefaultTreeSelectionModel.
	 */
	class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {
		/** Set to true when we are updating the ListSelectionModel. */
		protected boolean updatingListSelectionModel;

		public ListToTreeSelectionModelWrapper() {
			super();
			getListSelectionModel().addListSelectionListener(createListSelectionListener());
		}

		/**
		 * Returns the list selection model. ListToTreeSelectionModelWrapper listens
		 * for changes to this model and updates the selected paths accordingly.
		 */
		ListSelectionModel getListSelectionModel() {
			return listSelectionModel;
		}

		/**
		 * This is overridden to set <code>updatingListSelectionModel</code> and
		 * message super. This is the only place DefaultTreeSelectionModel alters
		 * the ListSelectionModel.
		 */
		public void resetRowSelection() {
			if (!updatingListSelectionModel) {
				updatingListSelectionModel = true;
				try {
					super.resetRowSelection();
				} finally {
					updatingListSelectionModel = false;
				}
			}
			// Notice how we don't message super if
			// updatingListSelectionModel is true. If
			// updatingListSelectionModel is true, it implies the
			// ListSelectionModel has already been updated and the
			// paths are the only thing that needs to be updated.
		}

		/**
		 * Creates and returns an instance of ListSelectionHandler.
		 */
		protected ListSelectionListener createListSelectionListener() {
			return new ListSelectionHandler();
		}

		/**
		 * If <code>updatingListSelectionModel</code> is false, this will reset
		 * the selected paths from the selected rows in the list selection model.
		 */
		protected void updateSelectedPathsFromSelectedRows() {
			if (!updatingListSelectionModel) {
				updatingListSelectionModel = true;
				try {
					// This is way expensive, ListSelectionModel needs an
					// enumerator for iterating.
					int min = listSelectionModel.getMinSelectionIndex();
					int max = listSelectionModel.getMaxSelectionIndex();

					clearSelection();
					if (min != -1 && max != -1) {
						for (int counter = min; counter <= max; counter++) {
							if (listSelectionModel.isSelectedIndex(counter)) {
								TreePath selPath = gTree.getPathForRow(counter);

								if (selPath != null) {
									addSelectionPath(selPath);
								}
							}
						}
					}
				} finally {
					updatingListSelectionModel = false;
				}
			}
		}
    
		/**
		 * Class responsible for calling updateSelectedPathsFromSelectedRows when
		 * the selection of the list changse.
		 */
		class ListSelectionHandler implements ListSelectionListener {
			public void valueChanged(ListSelectionEvent e) {
				updateSelectedPathsFromSelectedRows();
        //01Barmaja
        //This line was added to update the FGCurrentItemPanel.
        //It's real place would be in the after table construction
        //But the listener wrappers prevent the call of the selectionChanged...
        //We do not know why????
        //table.reactToFocusChange(false);
			}
		}
    
	}
  
  /*private FocListListener tableCellModificationListener = null;
  public void plugListListenerToCellPropertiesMoifications(){
    FTableView view = getTableView();
    if (view != null) {
      FocListener focListener = new FocListener(){
        public void focActionPerformed(FocEvent evt) {
          fireTableRowsUpdated(0, getRowCount());
        }

        public void dispose() {
        }
      };
      
      //disposeTableCellModificationListener();
      
      
      tableCellModificationListener = new FocListListener(focList);
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
  public void visibleArrayReset() {
    getTree().resetVisibleChildren();
  }
}
