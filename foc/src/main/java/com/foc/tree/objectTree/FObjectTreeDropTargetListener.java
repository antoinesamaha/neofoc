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
package com.foc.tree.objectTree;

import java.awt.dnd.DropTargetDropEvent;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.dragNDrop.FocDefaultDropTargetListener;
import com.foc.dragNDrop.FocTransferable;
import com.foc.gui.FTreeTablePanel;
import com.foc.gui.treeTable.FTreeTableModel;

public class FObjectTreeDropTargetListener extends FocDefaultDropTargetListener {
  
  private FTreeTablePanel selectionPanel = null;
  
  public FObjectTreeDropTargetListener(FTreeTablePanel selectionPanel) {
    this.selectionPanel = selectionPanel;
    FTreeTableModel treeTableModel = (FTreeTableModel) selectionPanel.getTableModel();
    treeTableModel.getTree();
  }
  
  public void dispose(){
  	super.dispose();
  	selectionPanel = null;
  }
  
  public FTreeTableModel getTableModel(){
  	return selectionPanel != null ? (FTreeTableModel) selectionPanel.getTableModel() : null;
  }
  
  public FObjectTree getTree(){
  	return getTableModel() != null ? (FObjectTree) getTableModel().getTree() : null;
  }
  
  public FTreeTablePanel getSelectionPanel(){
  	return selectionPanel;
  }

  public int getFatherNodeID(){
  	return getTree().getFatherNodeId();
  }

  public FObjectNode getTargetNode(FocTransferable focTransferable){
  	return (FObjectNode) getTableModel().getNodeForRow(focTransferable.getTableTargetRow());
  }

  public FocObject getTargetObject(FocTransferable focTransferable){
  	return (FocObject) getTableModel().getRowFocObject(focTransferable.getTableTargetRow());
  }

  public FObjectNode getSourceNode(FocTransferable focTransferable){
  	return (FObjectNode) getTableModel().getNodeForRow(focTransferable.getTableSourceRow());
  }

  public FocObject getSourceObject(FocTransferable focTransferable){
  	return (FocObject) getTableModel().getRowFocObject(focTransferable.getTableSourceRow());
  }
  
  public void drop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
    try {
      fillDropInfo(focTransferable, dtde);
      
      if(shouldExecuteDrop(focTransferable, dtde)){
        executeDrop(focTransferable, dtde);
      }
    }catch(Exception e){
      Globals.logException(e);
    }
  }
  
  public boolean executeDrop(FocTransferable focTransferable, DropTargetDropEvent dtde){
    FTreeTablePanel treeTablePanel = selectionPanel;
    FTreeTableModel treeTableModel = (FTreeTableModel)treeTablePanel.getTableModel();
    FObjectNode targetNode = (FObjectNode) treeTableModel.getNodeForRow(focTransferable.getTableTargetRow());
    FObjectNode sourceNode = (FObjectNode) treeTableModel.getNodeForRow(focTransferable.getTableSourceRow());
    FocObject targetObj = (FocObject)targetNode.getObject();
    FocObject sourceObj = (FocObject)sourceNode.getObject();
    
    boolean execute = shouldExecuteDrop(focTransferable, dtde);
    
    if(execute){
      sourceObj.setPropertyObject(getFatherNodeID(), targetObj);
      /*sourceNode.getFatherNode().removeChild(sourceNode);
      sourceNode.setFatherNode(targetNode);
      targetNode.addChild(sourceNode);*/
      sourceNode.moveTo(targetNode);
      //treeTableModel.refreshTree(getTree(), true);
      treeTableModel.refreshGui();
    }
    
    return execute;
  }
  
  public boolean shouldExecuteDrop(FocTransferable focTransferable, DropTargetDropEvent dtde){
  	boolean shouldExecute = false;
    FTreeTablePanel treeTablePanel = selectionPanel;
    FTreeTableModel treeTableModel = (FTreeTableModel)treeTablePanel.getTableModel();
    FObjectNode targetNode = (FObjectNode) treeTableModel.getNodeForRow(focTransferable.getTableTargetRow());
    FObjectNode sourceNode = (FObjectNode) treeTableModel.getNodeForRow(focTransferable.getTableSourceRow());
    if(sourceNode != null && targetNode != null){
	    FocObject targetObj = (FocObject)targetNode.getObject();
	    FocObject sourceObj = (FocObject)sourceNode.getObject();
	    shouldExecute = (sourceObj != null && sourceObj != targetObj) && (!sourceNode.isAncestorOf(targetNode));
    }
    return shouldExecute;
  }
}
