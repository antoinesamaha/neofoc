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

import javax.swing.tree.TreePath;

import com.foc.Globals;
import com.foc.tree.FNode;
import com.foc.tree.FTree;

public class FTreeModel extends FAbstractTreeModel {

	private FTree tree = null;
	
	public FTreeModel(FTree tree){
		this.tree = tree;
  }
	
	public FTree getTree(){
		return tree;
	}
	
  public void dispose(){
    tree = null;
  }
  
	public Object getChild(Object parent, int index) {
		FNode parentNode = (FNode) parent;
    FNode child = parentNode.getVisibleChildAt(index);
		return child;
	}

	public int getChildCount(Object parent) {
		FNode parentNode = (FNode) parent;
    int count = (parentNode == null || parentNode.getNodeDepth() >= tree.getDepthVisibilityLimit()) ? 0 : parentNode.getVisibleChildCount();    
    return count;
	}

	public int getIndexOfChild(Object parent, Object child) {
		FNode parentNode = (FNode) parent;
		return parentNode.findChildIndex((FNode)child);
	}

	public Object getRoot() {		
		return tree.getRoot();
	}

	public boolean isLeaf(Object node){
    FNode parentNode = (FNode) node;
    boolean isLeaf = false;
    try{
      isLeaf = parentNode.getNodeDepth() >= tree.getDepthVisibilityLimit() ? true : parentNode.isLeaf();
    }catch(Exception e){
      Globals.logException(e);
    }
		return isLeaf;
  }

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

}
