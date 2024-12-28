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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.Icon;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.gui.treeTable.FGTreeInTable;
import com.foc.gui.treeTable.FTreeTableModel;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.property.FProperty;
import com.foc.property.FString;
import com.foc.tree.FINode;
import com.foc.tree.FNode;
import com.foc.tree.FTree;
import com.foc.tree.TreeScanner;
//import com.vaadin.data.Item;

public class FCriteriaTree<N extends FCriteriaNode, O extends FocObject> extends FTree<FCriteriaNode, FocObject> {
	private FTreeDesc treeDesc    = null;
	private FocList   focList     = null;
	private ArrayList arrayAddedObjects = null;

  public FCriteriaTree(){
    this(FTree.COLOR_MODE_PREDEFINED);
  }

  public FCriteriaTree(int colorMode){
    super(colorMode);
    setAutomaticlyListenToListEvents(true);
  }
  
	public FCriteriaTree(FTreeDesc treeDesc){
    this();
    setTreeDesc(treeDesc);
	}

  public FCriteriaTree(FTreeDesc treeDesc, String rootTitle){
    this();
    setTreeDesc(treeDesc);
    FINode root = getRoot();
    if(root != null){
      root.setTitle(rootTitle);
    }
  }

	public void dispose(){
		clearTree();
    super.dispose();

    if(treeDesc != null){
      treeDesc.dispose();
      treeDesc = null;
    }
    
   	focList = null; 
	}
	
  @Override
	protected N newRootNode() {
    return (N) (new FCriteriaRootNode("Root", treeDesc.getNodeInfoForLevel(0), this));
	}

	public void clearTree(){
    scanTreeAndDisposeDisconnectedNonLeafItems();
    super.clearTree();
  }
  
  private void scanTreeAndDisposeDisconnectedNonLeafItems(){
  	boolean backup = isListListenerEnabled();
  	setListListenerEnabled(false);
    scan(new TreeScanner<FCriteriaNode>(){
      public void afterChildren(FCriteriaNode node) {
        FocObject obj = (FocObject) node.getObject();
        if(obj != null && obj.getFatherSubject() == null){//If Father subject is null then this is a disconnected item
        	getFocList().remove(obj);
          obj.dispose();
        }
      }

      public boolean beforChildren(FCriteriaNode node) {
        return true;
      }
    });
    setListListenerEnabled(backup);
  }
  
  public void setTreeDesc(FTreeDesc treeDesc){
    this.treeDesc = treeDesc;
    /*if(treeDesc != null){
      root = new FCriteriaRootNode("Root", treeDesc.getNodeInfoForLevel(0), this);
    }*/
  }
  
  public FTreeDesc getTreeDesc(){
    return treeDesc;
  }

  public boolean isIncluded(FocObject focObject){
  	return true;
  }
  
  public void growTreeFromFocList(FocList focList){
  	clearTree();
  	boolean sortableBackup = isSortable();
  	setSortable(false);
  	if(treeDesc != null){
      root = newRootNode();
//  		root = null;
    }
  	
  	this.focList = focList;
    addFocListenerToFocList();
    
//	    for(int j = 0; j < focList.size(); j++){
//	      FocObject focObj = focList.getFocObject(j);
//	      addFocObject(focObj);
//	    }
    
    arrayAddedObjects = new ArrayList();
    
    Iterator iterator = focList.listElementIterator();
    while(iterator != null && iterator.hasNext()){
      FocListElement focElem = (FocListElement) iterator.next();
      if(focElem != null && (!focElem.isHide() || isDiscardHideFlagInFocList()) && isIncluded(focElem.getFocObject())){
      	addFocObject(focElem.getFocObject());
      }
    }
    
    if(root != null && !root.isLeaf()){
      FocObject objDisconnected = focList.newEmptyDisconnectedItem();
      arrayAddedObjects.add(objDisconnected);
      root.setObject(objDisconnected);
    }
    
    //TEMPREF 2012 - Begin
    if(arrayAddedObjects != null && !Globals.getApp().isWithGui()){
      boolean backupOfListenerFlag = isListListenerEnabled();
      setListListenerEnabled(false);
      for(int i=0; i<arrayAddedObjects.size(); i++){
        FocObject addedObject = (FocObject) arrayAddedObjects.get(i);
        focList.add(addedObject, false);
        addedObject.setDbResident(false);
      }
      setListListenerEnabled(backupOfListenerFlag);
    }
    //TEMPREF 2012 - End

    
    setSortable(sortableBackup);
    
//    for(int i=0; i<root.getChildCount(); i++){
//    	N child = (N) root.getChildAt(i);
//    	child.setFatherNode(null);
//    }
//    root = null;
  }
  
  public N addFocObject(FocObject focObj){
  	N node = null;
    if(focList != null && focList.includeObject_ByListFilter(focObj)){
	    FCriteriaTree   tree  = this;
	    node = (N) tree.getRoot();
	    for(int i = 1; i<treeDesc.getNodeLevelsCount(); i++){
	      FNodeLevel nodeLevel = treeDesc.getNodeLevelAt(i);
	      FFieldPath path      = nodeLevel.getPath();
	      FProperty  prop      = path.getPropertyFromObject(focObj);
	      String     childName = prop == null ? nodeLevel.getNoneStringDisplay() : prop.getString();
	      node = (N) node.addChild(childName);
	      if(node.getObject() == null){
	        if(i == treeDesc.getNodeLevelsCount()-1){
	          //In this case we are in a leaf node
	          node.setObject(focObj);
	          
	          //Debug
	          /*
	          if(ConfigInfo.isDebugMode()){
	          	ArrayList<String> array = new ArrayList<String>();
	          	N currNode = node;
	          	while(currNode != null){
	          		String title = currNode.getTitle();
	          		array.add(title);
	          		currNode = (N) currNode.getFatherNode();
	          	}
	          	Globals.logString("Added Node ,"+childName+array.get(2)+","+array.get(1)+","+array.get(0));
	          }
	          */
	          //-----
	        }else{
	        	fillNodeObject(node, nodeLevel, focObj);        	
	        }
	      }else{
	      	cumulNodeObject(node, nodeLevel, focObj);
	      }
	    }
	    sort();
    }
    return node;
  }
  
  protected void cumulNodeObject(FCriteriaNode node, FNodeLevel nodeLevel, FocObject leafFocObject){
  	
  }
  
  protected void fillNodeObject(FCriteriaNode node, FNodeLevel nodeLevel, FocObject leafFocObject){
  	FFieldPath path = nodeLevel.getPath();  	
    FProperty firstLevelProp = path.getPropertyFromObject(leafFocObject, 0);
    Object obj = firstLevelProp != null ? firstLevelProp.getObject() : null;
  	
    FocObject objDisconnected = focList.newEmptyDisconnectedItem();
    if(arrayAddedObjects != null) arrayAddedObjects.add(objDisconnected);
    FProperty nodeObjectProp = path.getPropertyFromObject(objDisconnected, 0);
    if(nodeObjectProp != null){
    	nodeObjectProp.setObject(obj);
    }
    node.setObject(objDisconnected);  	
  }
  
  public N findExistingLevelForObject(O focObj){
    N node     = (N) this.getRoot();
    N nextNode = node;
    for(int i = 1; i<treeDesc.getNodeLevelsCount() && nextNode != null; i++){
      FNodeLevel nodeLevel = treeDesc.getNodeLevelAt(i);
      FFieldPath path      = nodeLevel.getPath();
      FProperty  prop      = path.getPropertyFromObject(focObj);
      String     childName = prop == null ? " < None > " : prop.getString();
      
      nextNode = (N) node.findChild(childName);
      if(nextNode != null){
      	node = nextNode; 
      }
    }
    return node;
  }
  
  public boolean containsFieldPath(FFieldPath fieldPath){
    return (treeDesc != null)? treeDesc.containsFieldPath(fieldPath) : false;
  }

  @Override
  public int getDepthVisibilityLimit() {
    return (treeDesc != null)? treeDesc.getDepthVisibilityLimit() : 1;
  }

  @Override
  public FocObject newEmptyItem(FCriteriaNode fatherNode) {
    int newItemNumber = 1;
    String newItemName = FNode.NEW_ITEM;
    if(fatherNode != null){
	    while(fatherNode.findChild(newItemName) != null){
	      newItemName = FNode.NEW_ITEM+" ("+(++newItemNumber)+")";
	    }
    }

    boolean backupDirectlyEditable = focList.isDirectlyEditable();
    focList.setDirectlyEditable(false);
    
    FocObject newFocObj = focList.newEmptyItem();
    
    if(newFocObj != null){
    	/*
    	FCriteriaNode currNode = fatherNode;
    	while(currNode != null){
    		
    		String propStringValue = currNode.getTitle();
    		FFieldPath fieldPath = currNode.getNodeLevel().getPath();
    		//FNodeLevel level = currNode.getNodeLevel();
    		if(fieldPath != null){
    			FProperty prop = fieldPath.getPropertyFromObject(newFocObj, 0);
    			if(currNode.isLeaf()){
    				FField fld = prop.getFocField();
    				if(fld instanceof FCharField && propStringValue != null && !propStringValue.isEmpty()){
    					propStringValue = propStringValue + "_Copy";
    					//if(propStringValue.length() > ((FCharField)fld).getSize()){
    					//	propStringValue
    					//}
    				}
    			}
    			prop.setString(propStringValue);
    		}
    		
    		currNode = (FCriteriaNode) currNode.getFatherNode();
    	}
    	*/

    	FCriteriaNode criteriaNode = (FCriteriaNode)fatherNode;
      while(criteriaNode != null && criteriaNode.getFatherNode() != null){
      	FFieldPath fieldPath = criteriaNode.getNodeLevel().getPath();
        FProperty property    = fieldPath.getPropertyFromObject((FocObject)criteriaNode.getObject(), 0);
        FProperty newProperty = newFocObj.getFocProperty(property.getFocField().getID());
        //If this is a key string and leaf then we don;t want to copy because this will give duplicate items message
        if(!criteriaNode.isLeaf() || !(newProperty instanceof FString) || !newProperty.getFocField().getKey()){
        	newProperty.setObject(property.getObject());	
        }
        criteriaNode = (FCriteriaNode)criteriaNode.getFatherNode();
      }
      
      FNodeLevel levelAtTheInsertion = null;
      if(fatherNode != null && fatherNode.getLevelDepth()+1 < getTreeDesc().getNodeLevelsCount()){
      	levelAtTheInsertion = getTreeDesc().getNodeLevelAt(fatherNode.getLevelDepth()+1);
      }
    	
      if(levelAtTheInsertion != null && levelAtTheInsertion.getPath() != null){
      	levelAtTheInsertion.getPath().getPropertyFromObject(newFocObj, 0).setString(newItemName);
      }
     
      if(backupDirectlyEditable) focList.add(newFocObj);
      //newChildNode = (N) findNodeFromFocObject(newFocObj);
    }
    
    focList.setDirectlyEditable(backupDirectlyEditable);
    //treeTableModel.refreshTree(this, true);
    return newFocObj;
  }
  
  @Override
  public boolean deleteNode(FTreeTableModel treeTableModel, FNode node) {
    
    FCriteriaNode criteriaNode = (FCriteriaNode)node;
    
    if(criteriaNode.isDisplayLeaf()){
      return super.deleteNode(treeTableModel, node);  
    }
    
    return false;
  }

  public FProperty getTreeSpecialProperty_Internal(FCriteriaNode node) {
    FFieldPath path = node.getNodeLevel() != null ? node.getNodeLevel().getPath() : null;
    return path != null ? path.getPropertyFromObject((FocObject)node.getObject()) : null;
  }
  
  @Override
  public FProperty getTreeSpecialProperty(FCriteriaNode node) {
  	return getTreeSpecialProperty_Internal(node);
  }

  @Override
  public boolean isNodeLocked(FCriteriaNode node) {
    return !node.isLeaf() && node.getNodeDepth() < treeDesc.getDepthVisibilityLimit() ? true : false;
  }

  @Override
  public FocList getFocList() {
    return focList;
  }

  @Override
  public Icon getIconForNode(FCriteriaNode node) {
    return null;
  }
  
  public Color getColorForNode(FNode node, int row, FGTreeInTable treeInTable ){
  	Color col = null;
  	if((node != null && node != null && !node.isLeaf() && treeDesc != null && node.getNodeDepth() < treeDesc.getDepthVisibilityLimit()) || node.isRoot()){
  		col = super.getColorForNode(node, row, treeInTable );
  	}
    
    return col;
  }
  
  protected Comparator<FCriteriaNode> getDefaultComparator(){
		 return new Comparator<FCriteriaNode>(){

			public int compare(FCriteriaNode o1, FCriteriaNode o2) {
				int res = 0;
				if(o1 != null && o2!= null){
					FCriteriaNode c1 = (FCriteriaNode) o1;
					FocObject focObject1 = (FocObject) o1.getObject();
					FocObject focObject2 = (FocObject) o2.getObject();
					FFieldPath fieldPath = c1.getNodeLevel().getPath();
					if(fieldPath != null){
						FProperty property1 = fieldPath.getPropertyFromObject(focObject1);
						FProperty property2 = fieldPath.getPropertyFromObject(focObject2);
						if(property1 != null && property2 != null){
							res = property1.compareTo(property2);
						}else if(property1 != null && property2 == null){
							res = 1;
						}else if(property1 == null && property2 != null){
							res = -1;
						}
					}									
				}
				return res;
			}
		 };
	 }

  @Override
  public int getDisplayCodeFieldID() {
    return NO_DISPLAY_CODE_ID;
  }

  @Override
  public void moveDown(FNode node) {
  }

  @Override
  public void moveUp(FNode node) {
  }

	//------------------------------------------------------
	// Vaadin Hierarchical
	//------------------------------------------------------

  @Override
  public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  public FNode vaadin_FindNode(Object itemId){
    FNode node = null;
    try{
      Long integ = (Long) itemId;
      node = findNode(integ.longValue());
    }catch(Exception e){
      Globals.logException(e);
    }
    
    return node;
  }
  
//  @Override
  public boolean removeItem(Object itemId) throws UnsupportedOperationException {
//  	throw new UnsupportedOperationException("FCriteriaTree.removeItem");
  	return true;
  }

//  @Override
//  public Item addItem(Object itemId) throws UnsupportedOperationException {
////  	throw new UnsupportedOperationException("FCriteriaTree.addItem(Object itemID)");
//  	return null;
//  }

//  @Override
  public Object addItem() throws UnsupportedOperationException {
//  	throw new UnsupportedOperationException("FCriteriaTree.addItem()");
  	return null;
  }

  @Override
  public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
  	throw new UnsupportedOperationException("FCriteriaTree.addContainerProperty");
  }

  @Override
  public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
  	throw new UnsupportedOperationException("FCriteriaTree.removeContainerProperty");
  }

  @Override
  public boolean removeAllItems() throws UnsupportedOperationException {
  	throw new UnsupportedOperationException("FCriteriaTree.removeAllItems");
  }
  
  
  
  
}
