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

import java.awt.Color;
import java.util.Comparator;

import javax.swing.Icon;

import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.property.FObject;
import com.foc.property.FProperty;
import com.foc.tree.FNode;
import com.foc.tree.FTree;
import com.foc.tree.TreeScanner;

public class FObjectTree<ON extends FObjectNode, OO extends FocObject> extends FTree<ON, OO> {

  private int     displayFieldId       = 0;
  private int     fatherNodeId         = FField.FLD_FATHER_NODE_FIELD_ID;
  private FocList focList              = null;
  private int     depthVisibilityLimit = 0;
  //private boolean hasFicticiousNonDBResidentRootForCalculation = false;
  private int     rootMode             = 0;  
  private int     nextNewItemNumber    = 0;
  
  public static final int ROOT_MODE_NO_OBJECT           = 0;
  public static final int ROOT_MODE_DISCONNECTED_OBJECT = 1;
  public static final int ROOT_MODE_REAL_OBJECT         = 2;
  
  public FObjectTree(int rootMode, boolean isRootVisible){
  	super(isRootVisible);
  	init(rootMode);
  }
  
  public FObjectTree(boolean hasFicticiousNonDBResidentRootForCalculation){
    super();
    init(hasFicticiousNonDBResidentRootForCalculation ? ROOT_MODE_DISCONNECTED_OBJECT : ROOT_MODE_NO_OBJECT);
  }

  @Deprecated
  public FObjectTree(boolean hasFicticiousNonDBResidentRootForCalculation, boolean isRootVisible){
  	this(hasFicticiousNonDBResidentRootForCalculation ? ROOT_MODE_DISCONNECTED_OBJECT : ROOT_MODE_NO_OBJECT, isRootVisible);
  }
  
  public FObjectTree(){
    this(false);
  }
  
  @Deprecated
  public FObjectTree(boolean hasFicticiousNonDBResidentRootForCalculation, int colorMode){
    super(colorMode);
    init(hasFicticiousNonDBResidentRootForCalculation ? ROOT_MODE_DISCONNECTED_OBJECT : ROOT_MODE_NO_OBJECT);
  }
  
  private void init(int rootMode){
 		root = newRootNode();
    setAllowNonLeavesDeletion(true);
    this.rootMode = rootMode;
    setAutomaticlyListenToListEvents(false);
  }
  
  public void dispose(){
    super.dispose();
    focList = null;
  }
  
  public int getRootMode(){
  	return rootMode;
  }
  
  @Override
  public Color getBackgroundColorForLevel(int level) {
    return super.getBackgroundColorForLevel(level);
  }

  public void setDisplayFieldId(int id){
    displayFieldId = id;
  }
  
  public void setFatherNodeId(int fnid){
    fatherNodeId = fnid;
  }

  public int getFatherNodeId(){
    return fatherNodeId;
  }

  public int getDisplayFieldId (){
    return displayFieldId;
  }

  //BAntoineS - RECURSIVITY
  /*
  private int getMasterRef(FocObject focObj){
    FocObject obj = focObj.getPropertyObject(fatherNodeId);
    return obj != null ? obj.getReference().getInteger() : 0;
  }
  
  private ArrayList sortFocListAccordingToDependencies(FocList focList){
    ArrayList<FocObject> list = new ArrayList<FocObject>();
    for(int y = 0; y < focList.size(); y++){
      list.add(focList.getFocObject(y));
    }
    int n = list.size();
    for(int i = 1; i < n; i++){
      for(int j = 0; j < (n-i); j++){
        FocObject focObj1 = list.get(j);
        FocObject focObj2 = list.get(j+1);
        if(getMasterRef(focObj1) > getMasterRef(focObj2)){
          list.set(j, focObj2);
          list.set(j+1, focObj1);
        }else{
          list.set(j, focObj1);
          list.set(j+1, focObj2);
        }
      }
    }
    return list;
  }
  */
  //EAntoineS - RECURSIVITY
  
  public void growTreeFromFocList(FocList focList){
  	growTreeFromFocList(focList, false);
  }
  
  public void growTreeFromFocList(FocList focList, boolean withoutSearch_FirstLoad){
  	boolean sortableBackup = isSortable();
  	setSortable(false);
  	
    clearTree();
    
    setFocList(focList);
//    if( !this.focList.isDirectlyEditable() ){
//      Globals.getDisplayManager().popupMessage("We Are changing the directly editable flag of the focList");
//    }
    
    addFocListenerToFocList();
    //BAntoineS - RECURSIVITY
    /*
    storage = new ArrayList<FocObject>();
    nodeMap = new HashMap<Integer, ON>();
    ArrayList list = sortFocListAccordingToDependencies(focList);
    
    grow(list);
    */
    
    for(int i=0; i<focList.size(); i++){
    	OO objectToInsert = (OO) focList.getFocObject(i);
      if(objectToInsert != null){
    		addFocObject(objectToInsert, withoutSearch_FirstLoad);
    	}
    }
    /*
    Iterator iterator = focList.listElementIterator();
    while(iterator != null && iterator.hasNext()){
      FocListElement focElem = (FocListElement) iterator.next();
      if(focElem != null && (!focElem.isHide() || isDiscardHideFlagInFocList())){
      	addFocObject((OO)focElem.getFocObject());
      }
    }
    */
    //EAntoineS - RECURSIVITY
    
    if(getRootMode() == ROOT_MODE_DISCONNECTED_OBJECT){
      FocObject focObj = createRootFocObjectForCalculation();
      getRoot().setObject(focObj);  
    }
    
    setSortable(sortableBackup);
    if(sortableBackup){
    	sort();
    }
  }
  
  public FocObject createRootFocObjectForCalculation(){
    OO focObj = (OO) focList.newEmptyDisconnectedItem();
    focObj.setPropertyString(displayFieldId, getRoot().getTitle());
    return focObj;
  }
  
  public ON addFocObject(OO childObject){
  	return addFocObject(childObject, false);
  }
  
  //BAntoineS - RECURSIVITY
  @SuppressWarnings({ "unchecked" })
  public ON addFocObject(OO childObject, boolean withoutSearch){
  	//Globals.logString(" ADDING : "+childObject.getReference().getInteger());
  	//Small Change In How we find the father
  	FObject fatherObjProp = (FObject) childObject.getFocProperty(fatherNodeId);
  	OO fatherObject = null;
		ON fatherNode   = null; 

		if(fatherObjProp.getLocalReferenceInt() == 0){
  		
  	}else{
  		fatherObject = (OO) getFocList().searchByReference(fatherObjProp.getLocalReferenceInt());
  		if(fatherObject == null){
  			Globals.showNotification("Object has a father that cannot be found in this list!", "Object Type:"+getFocList().getFocDesc().getStorageName()+"\nRef:"+fatherObjProp.getLocalReferenceInt(), IFocEnvironment.TYPE_ERROR_MESSAGE);
  			fatherNode = getRoot();
  			fatherObject = (OO) getRoot().getObject();
  			childObject.getFocProperty(FField.REF_FIELD_ID).setBackground(Color.RED);
  		}
  	}
  	//OO fatherObject = (OO) childObject.getPropertyObject(fatherNodeId);
  	  	
  	if(fatherObject != null){
  		fatherNode = findNode(fatherObject);
  		//fatherNode = (ON) findNodeLocally(fatherObject);
      if(fatherNode == null){
      	if(childObject.getReference().getLong() == fatherObject.getReference().getLong()){
      		Globals.showNotification("DATA Error "+childObject.getThisFocDesc().getStorageName(), childObject.getReference().getLong()+" is pointing to itself as father!", IFocEnvironment.TYPE_ERROR_MESSAGE);
      	}else{
      		fatherNode = addFocObject(fatherObject);
      	}
  		}else{
      	//Globals.logString("  "+childObject.getReference().getInteger()+" has father and father found = "+fatherObject.getReference().getInteger()+ " found "+((FocObject)fatherNode.getObject()).getReference().getInteger());
  		}
      
  	}else{
  		//Globals.logString("  "+childObject.getReference().getInteger()+" Does not have father");
  	}
  	
  	if(fatherNode == null && getRootMode() != ROOT_MODE_REAL_OBJECT){
  		fatherNode = (ON) getRoot();
  	}

  	ON node = null;
  	
  	if(fatherNode != null){
	  	//Now I have the father node
  		if(withoutSearch){
  			node = (ON) fatherNode.addChild_WithoutSearch(childObject.getPropertyString(displayFieldId), childObject);
  		}else{
  			node = (ON) fatherNode.addChild_SearchByObject(childObject.getPropertyString(displayFieldId), childObject);
  		}

  		//update the visibilityLimit
  		{
  			ON nde = node;
	      int level = 0;
	      while ( nde != null && !(nde.isRoot()) ){
	        level++;
	        nde = (ON) nde.getFatherNode();
	      }
	      if( level > depthVisibilityLimit){
	        depthVisibilityLimit = level;
	      }
  		}
  			
  		/*
	  	if(getDisplayCodeFieldID() != FField.NO_FIELD_ID && !childObject.getPropertyString(getDisplayCodeFieldID()).isEmpty()){
	  		node = (ON) fatherNode.addChild(childObject.getPropertyString(getDisplayCodeFieldID()), childObject.getPropertyString(displayFieldId), childObject);	
	  	}else{
	  		node = (ON) fatherNode.addChild(childObject.getPropertyString(displayFieldId), childObject);
	  	}
	  	*/
  	}else{
  		getRoot().setObject(childObject);
  		node = getRoot();
  	}
  	
  	return node;
  }
  //EAntoineS - RECURSIVITY
    
  public void addFocObject(FocObject childObject, FObjectNode fatherNode, String title){
    if(fatherNode == null){
      fatherNode = (FObjectNode) getRoot();
    }  
    FObjectNode node = new FObjectNode(title, fatherNode);
    node.setObject(childObject);
    fatherNode.addChild(node);  
  }
  
  //BAntoineS - RECURSIVITY
  /*
  public void grow(ArrayList list){
    FocObject pointedToFocObject = null;
    
    for(int j = 0; j < list.size(); j++){
      FocObject focObj = (FocObject)list.get(j);      
      ON node = getRoot();      
      
      pointedToFocObject = focObj.getPropertyObject(fatherNodeId);
      
      if (pointedToFocObject == null){
        node = (ON) getRoot().addChild(focObj.getPropertyString(displayFieldId));
        node.setObject(focObj);
        nodeMap.put(focObj.getPropertyInteger(FField.REF_FIELD_ID), node);
      }else {
        ON createdNode = nodeMap.get(pointedToFocObject.getPropertyInteger(FField.REF_FIELD_ID));
        if(createdNode != null){
          
          if( !isAllowNodeNameDuplication() ){
            createdNode = (ON) createdNode.addChild(focObj.getPropertyString(displayFieldId));  
          }else {
          	createdNode = (ON) createdNode.addChild(focObj.getPropertyString(displayFieldId));
          	//createdNode  = new FObjectNode(focObj.getPropertyString(displayFieldId), (FObjectNode)createdNode);
          	//createdNode.getFatherNode().addChild((ON)createdNode);
          }
          
          createdNode.setObject(focObj);
          nodeMap.put(focObj.getPropertyInteger(FField.REF_FIELD_ID), createdNode);
          if(storage.size() > 0){
            storage.remove(focObj);  
          }
        }else{
          if (!storage.contains(focObj)){
            storage.add(focObj);  
          }
        }
      }
    }
    if(storage.size() > 0){
      grow(storage);
    }
  }
  */
  //EAntoineS - RECURSIVITY
    
  @Override
  public int getDepthVisibilityLimit() {
    /*
    scan(new TreeScanner<FNode>() {

      public void afterChildren(FNode node) {
       if( node.isLeaf() ){
         int level = 0;
         while ( node != null && !(node.isRoot()) ){
           level++;
           node = node.getFatherNode();
         }
         if( level > depthVisibilityLimit){
           depthVisibilityLimit = level;
         }
       }
      }

      public boolean beforChildren(FNode node) {
        return true;
      }
      
    });
    */
    return depthVisibilityLimit+1;
  }

  public int getNextNewItemNumber(){
    if(nextNewItemNumber == 0){
    	scan(new TreeScanner<FObjectNode>() {
				@Override
				public void afterChildren(FObjectNode node) {
				}

				@Override
				public boolean beforChildren(FObjectNode node) {
					if(node.getTitle().startsWith(FNode.NEW_ITEM)){
						String rest = node.getTitle().substring(FNode.NEW_ITEM.length());
						rest = rest.trim();
						int nbr = Integer.valueOf(rest);
						if(nextNewItemNumber < nbr) nextNewItemNumber = nbr;
					}
					return true;
				}
			});
    }  	
    nextNewItemNumber++;
    return nextNewItemNumber;
  }
  
  @Override
  public FocObject newEmptyItem(ON fatherNode) {
    FocObject newFocObj = null;
  	int nextNbr = getNextNewItemNumber();
  	String newItemName = FNode.NEW_ITEM+" "+nextNbr;
  	      
    boolean backupDirectlyEditable = focList.isDirectlyEditable();
    focList.setDirectlyEditable(false);
    newFocObj = focList.newEmptyItem();
    
    if(newFocObj != null){
      newFocObj.setPropertyString(displayFieldId, newItemName);
      FocObject targetObj = fatherNode != null ? (FocObject) fatherNode.getObject() : null;
      newFocObj.setPropertyObject(fatherNodeId, targetObj);
      if(backupDirectlyEditable) focList.add(newFocObj);
      
      //newNode = (ON) findNodeFromFocObject(newFocObj);
    }
    focList.setDirectlyEditable(backupDirectlyEditable);
    
    return newFocObj;
  }

  @Override
  public FProperty getTreeSpecialProperty(ON node) {
    FProperty prop = null;
    FocObject focObj = (FocObject)node.getObject();
    if( focObj != null ){
      prop = focObj.getFocProperty(getDisplayFieldId());
    }
    return prop;
  }

  @Override
  public boolean isNodeLocked(ON node) {
    FocObject focObject = (FocObject)node.getObject();
    return focObject != null ? focObject.getFocProperty(getDisplayFieldId()).isValueLocked() : true;
  }

  public FocList getFocList() {
    return focList;
  }

  public void setFocList(FocList list) {
    focList = list;
  }
  
  @Override
  public Icon getIconForNode(ON node) {
    return null;
  }

  @Override
  public int getDisplayCodeFieldID() {
    return NO_DISPLAY_CODE_ID;
  }

	@Override
	protected ON newRootNode() {
		return (ON) new FObjectRootNode("Root", this);
	}

/*  public void colorInheritedValues(final Color color){
    
    scan(new TreeScanner<FObjectNode>(){

      public void afterChildren(FObjectNode node) {
        FocObject focObject = (FocObject)node.getObject();
        
        if( focObject != null ){
          FocObject fatherObj = focObject != null ? (FocObject)focObject.getPropertyObject(fatherNodeId) : null;
          
          int nodeDepth = node.getNodeDepth();
          
          FocFieldEnum iter = new FocFieldEnum(focObject.getThisFocDesc(), focObject, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
          while(iter != null && iter.hasNext()){
            iter.next();
            FProperty prop = (FProperty) iter.getProperty();
            if(prop != null && prop.getFocField().isWithInheritance()){
              boolean equalPropVal = useRootNodeForCalculation ? focObject.getPropertyString(prop.getFocField().getID()).equals(((FocObject)getRoot().getObject()).getPropertyString(prop.getFocField().getID())) : false;
              if(nodeDepth == 1){
                if(equalPropVal && prop.isInherited()){
                  prop.setBackground(color);  
                }else{
                  prop.setBackground(getColorForLevel(nodeDepth));
                }
              }
              
              
              if(fatherObj != null ){
                
                equalPropVal = focObject.getPropertyString(prop.getFocField().getID()).equals(fatherObj.getPropertyString(prop.getFocField().getID()));
                //boolean noVal = prop.getObject() == null || prop.getObject().equals(new Double(0));
                boolean noVal = prop.isEmpty();
                
                if( (equalPropVal || noVal) && prop.isInherited()){
                  prop.setBackground(color);
                }else{
                  prop.setBackground(node.isLeaf() ? Color.WHITE : getColorForLevel(nodeDepth));
                }
              }
            }
          }
        }
        
      }

      public boolean beforChildren(FObjectNode node) {
        return true;
      }
      
    });
    
  }*/
  
  @Override
  protected Comparator<ON> getDefaultComparator() {
    return new Comparator<ON>(){
      public int compare(ON node1, ON node2){
      	int comp = 0;
        FocObject focObj1 = (FocObject) node1.getObject();
        FocObject focObj2 = (FocObject) node2.getObject();
        if(focObj1 == null || focObj1.getOrderProperty() == null){
        	comp = -1;
        }else if(focObj2 == null || focObj2.getOrderProperty() == null){
        	comp =  1;
        }else{
	        Integer int1 = new Integer(focObj1.getOrderProperty().getInteger());
	        Integer int2 = new Integer(focObj2.getOrderProperty().getInteger());
	        comp = int1.compareTo(int2);
        }
        return comp; 
      }
    }; 
  }

  @Override
  public void moveDown(FNode node) {
    FNode upperNode = node;
    if( upperNode != null ){
      FocObject upperFocObject = (FocObject) upperNode.getObject();
      FNode fatherNode = upperNode.getFatherNode();
      if( fatherNode != null ){
        int upperIndex = fatherNode.findChildIndex(upperNode);
        if( (upperIndex+1) < fatherNode.getChildCount() ){
          FNode lowerNode = fatherNode.getChildAt(upperIndex+1);
          if( lowerNode != null ){
            FocObject lowerFocObject = (FocObject) lowerNode.getObject();
            if( lowerFocObject != null && upperFocObject != null ){
              int lowerOrder = lowerFocObject.getOrderProperty().getInteger();
              int upperOrder = upperFocObject.getOrderProperty().getInteger();
              lowerFocObject.setOrder(upperOrder);
              upperFocObject.setOrder(lowerOrder);
              sort();
            }
          }
        }  
      }
    }
  }

  @Override
  public void moveUp(FNode node) {
    FNode lowerNode = node;
    if( lowerNode != null ){
      FocObject lowerFocObject = (FocObject) lowerNode.getObject();
      FNode fatherNode = lowerNode.getFatherNode();
      if( fatherNode != null ){
        int lowerIndex = fatherNode.findChildIndex(lowerNode);
        if( lowerIndex > 0 ){
          FNode upperNode = fatherNode.getChildAt(lowerIndex-1);
          if( upperNode != null ){
            FocObject upperFocObject = (FocObject) upperNode.getObject();
            if( upperFocObject != null && lowerFocObject != null ){
              int upperOrder = upperFocObject.getOrderProperty().getInteger();
              int lowerOrder = lowerFocObject.getOrderProperty().getInteger();
              upperFocObject.setOrder(lowerOrder);
              lowerFocObject.setOrder(upperOrder);
              sort();
            }
          }
        }
      }
    }
  }
}
