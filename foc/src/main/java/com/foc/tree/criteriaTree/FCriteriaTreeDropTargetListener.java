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
package com.foc.tree.criteriaTree;

import java.awt.Point;
import java.awt.dnd.DropTargetDropEvent;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.dragNDrop.FocDefaultDropTargetListener;
import com.foc.dragNDrop.FocTransferable;
import com.foc.gui.FTreeTablePanel;
import com.foc.gui.treeTable.FTreeTableModel;
import com.foc.property.FProperty;
import com.foc.tree.FTree;

public class FCriteriaTreeDropTargetListener extends FocDefaultDropTargetListener {

  private FTreeTablePanel selectionPanel = null;
  
  public FCriteriaTreeDropTargetListener(FTreeTablePanel selectionPanel ){
    this.selectionPanel = selectionPanel;
  }
  
  public void drop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
    try {
      /*Transferable transferable = dtde.getTransferable();
      FocTransferable focTransferable = (FocTransferable)transferable.getTransferData(FocTransferable.getFocDataFlavor());*/
      FocObject sourceObj = focTransferable.getSourceFocObject();
      
      Point targetPoint = selectionPanel.getTable().getCellCoordinatesForMouseCurrentPosition();
      FTreeTablePanel treeTablePanel = selectionPanel;
      FTreeTableModel treeTableModel = (FTreeTableModel)treeTablePanel.getTableModel();
      FCriteriaNode targetNode = (FCriteriaNode) treeTableModel.getNodeForRow(targetPoint.y);
      if(!targetNode.isDisplayLeaf() && !targetNode.isRoot()){
        int row = focTransferable.getTableSourceRow();
        FCriteriaNode sourceNode = (FCriteriaNode)treeTableModel.getNodeForRow(row);
        
        
        sourceNode.getFatherNode().removeChild(sourceNode);
        sourceNode.setFatherNode(targetNode);
        targetNode.addChild(sourceNode);
        
        
        FCriteriaNode criteriaNode = (FCriteriaNode)sourceNode.getFatherNode();
        while( !(criteriaNode.isRoot()) ){
          FFieldPath fieldPath = criteriaNode.getNodeLevel().getPath();
          FProperty property = fieldPath.getPropertyFromObject((FocObject)criteriaNode.getObject(), 0);
          sourceObj.getFocProperty(property.getFocField().getID()).setObject(property.getObject());
          criteriaNode = (FCriteriaNode)criteriaNode.getFatherNode();
        }
        
        FTree tree = ((FTreeTableModel)selectionPanel.getTableModel()).getTree();
        treeTableModel.refreshTree(tree, true);  
      }
      
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
  
}
