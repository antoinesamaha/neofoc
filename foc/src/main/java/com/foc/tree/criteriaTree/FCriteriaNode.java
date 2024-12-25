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

import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.property.FObject;
import com.foc.property.FProperty;
import com.foc.tree.FNode;
import com.foc.tree.FTree;
import com.foc.tree.TreeScanner;

public class FCriteriaNode<N extends FCriteriaNode, O extends FocObject> extends FNode<FCriteriaNode, FocObject>{
  private FNodeLevel nodeInfo = null;

	public FCriteriaNode(FNodeLevel level){
		super();
		setNodeLevel(level);
	}

	public FCriteriaNode(String title, FNodeLevel level, N fatherNode){
		super(title, fatherNode);
    setNodeLevel(level);
	}

	public void dispose(){
    super.dispose();
    nodeInfo = null;
	}

  public int getNodeDepth(){
    return getNodeLevel() != null ? getNodeLevel().getLevelDepth() : -1;
  }
  
  public FNodeLevel getNodeLevel() {
    return nodeInfo;
  }

  public void setNodeLevel(FNodeLevel level) {
    this.nodeInfo = level;
  }
  
  public int getLevelDepth(){
    FNodeLevel nodeLevel = getNodeLevel();
    return nodeLevel != null ? nodeLevel.getLevelDepth() : -1;
  }
  
  public FTreeDesc getTreeDesc(){
    FCriteriaTree tree = (FCriteriaTree<N,O>) this.getTree();
    return tree != null ? tree.getTreeDesc() : null;
  }
  
  @Override
  public boolean isSortable(){
  	boolean sortable = false;
  	FCriteriaTree critTree = (FCriteriaTree) getTree();
  	if(critTree.isSortable()){
	  	FNodeLevel nodeLevel = getNodeLevel();
	  	if(nodeLevel != null){
	  		sortable = nodeLevel.isSortable();
	  	}
  	}
  	return sortable;
  }
  
  public boolean doesAnyFatherContainFieldPath(FFieldPath fieldPath){
    boolean contains = false;
    FCriteriaNode fatherNode = (FCriteriaNode) getFatherNode();
    while(fatherNode != null && !contains){
      FNodeLevel fatherNodeLevel = fatherNode.getNodeLevel();
      FFieldPath fatherPath = fatherNodeLevel != null ? fatherNodeLevel.getPath() : null;
      contains = fatherPath != null && fieldPath.isEqualTo(fatherPath);
      fatherNode = (FCriteriaNode) fatherNode.getFatherNode();
    }
    return contains;
  }

  protected N createChildNode(String title, FNodeLevel childInfo, N node){
  	return (N) new FCriteriaNode(title, childInfo, node);
  }
  
  @Override
	public N createChildNode(String childTitle){
		N inserted = null;
    /*int thisNodeLevel = -1;
    FNodeLevel thisNodeInfo = getTreeDesc().getNodeInfoForLevel(getLevelDepth());
    thisNodeLevel = thisNodeInfo != null ? thisNodeInfo.getLevelDepth() : -1;
    FNodeLevel childInfo = getTreeDesc().getNodeInfoForLevel(thisNodeLevel + 1);
    */
		int thisNodeLevel = getLevelDepth() >=0 ? getLevelDepth()+1 : 0;
    FNodeLevel childInfo = getTreeDesc().getNodeInfoForLevel(thisNodeLevel);
		inserted = (N) createChildNode(childTitle, childInfo, (N) this);
		
		return inserted;
	}
  
  public boolean isDisplayLeaf(){
    return isLeaf() && getNodeDepth() == getTreeDesc().getDepthVisibilityLimit();
  }
  
  public FocObject getFocObjectToShowDetailsPanelFor(){
  	LeafFinderScaner scaner = new LeafFinderScaner();
  	scan(scaner);
  	FocObject focObjectToShowDetaislPanelFor = scaner.getAnyLeafFocObject();
  	
  	if(focObjectToShowDetaislPanelFor != null){
	  	FNodeLevel nodeLevel = getNodeLevel();
	  	if(nodeLevel != null){
	  		FFieldPath fieldPath = nodeLevel.getPath();
	  		if(fieldPath != null){
	  			for(int i = 0 ; i < fieldPath.size(); i++){
	  				int at = fieldPath.get(i);
	  				FProperty prop = focObjectToShowDetaislPanelFor.getFocProperty(at);
	  				if(prop instanceof FObject){
		  				FocObject focObjFromProp = (FocObject) prop.getObject();
		  				if(focObjFromProp != null){
		  					focObjectToShowDetaislPanelFor = focObjFromProp;
		  				}
	  				}
	  			}
	  		}
	  	}
  	}
  	return focObjectToShowDetaislPanelFor;
  }
  
  public int getDetailsPanelViewId(){
  	int viewID = FNode.NO_SPECIFIC_VIEW_ID;
  	FNodeLevel nodeLevel = getNodeLevel();
  	if(nodeLevel != null){
  		viewID = nodeLevel.getDetailsPanelViewID();
  	}
  	return viewID;
  }
  
  private class LeafFinderScaner implements TreeScanner<FCriteriaNode>{
  	private FocObject anyLeafFocObject = null;
  	
		public void afterChildren(FCriteriaNode node) {
			if(node.isLeaf()){
				anyLeafFocObject = (FocObject) node.getObject();
			}
		}

		public boolean beforChildren(FCriteriaNode node) {
			return anyLeafFocObject == null;
		}
		
		private FocObject getAnyLeafFocObject(){
			return anyLeafFocObject;
		}
  	
  }

	public void setTree(FTree tree) {
	}
}
