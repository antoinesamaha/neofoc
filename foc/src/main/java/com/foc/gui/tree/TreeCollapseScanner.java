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
package com.foc.gui.tree;

import com.foc.gui.treeTable.FGTreeInTable;
import com.foc.gui.treeTable.FTreeTableModel;
import com.foc.tree.FNode;
import com.foc.tree.TreeScanner;

public class TreeCollapseScanner implements TreeScanner<FNode>{

	private FTreeTableModel treeTableModel = null;
	private int             level          = -1;
	
	public TreeCollapseScanner(FTreeTableModel treeTableModel, int level){
		this.level          = level;
		this.treeTableModel = treeTableModel;
	}
	
	public void dispose(){
		treeTableModel = null;
	}
	
  public void afterChildren(FNode node) {
    
  }

  public boolean beforChildren(FNode node) {
  	if(treeTableModel != null){
	    FGTreeInTable guiTree = treeTableModel.getGuiTree();    
	    if(node.getNodeDepth() == level){
	      int row = treeTableModel.getRowForNode(node);
	      guiTree.collapseRow(row);
	    }
  	}    
    return true;
  }
}
