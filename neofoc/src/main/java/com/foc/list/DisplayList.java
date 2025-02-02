/*
 * Created on Sep 7, 2005
 */
package com.foc.list;

import java.util.ArrayList;
import java.util.Iterator;

import com.foc.desc.FocObject;

/**
 * @author 01Barmaja
 */
public abstract class DisplayList {
  private FocList realList = null;
  private FocList displayList = null;
  private boolean doNotRemoveRealItems = false;
  
  public abstract void completeTheDisplayList(FocList realList, FocList displayList);
  public abstract void copyFromObjectToObject(FocObject target, FocObject source);
  public abstract boolean isDisplayItemToBeSaved(FocObject object);
  public abstract FocObject findObjectInList(FocList focList, FocObject object);

  public DisplayList(FocList realList) {
    this.realList = realList;
    
    displayList = new FocList(realList.getMasterObject(), new FocLinkSimple(realList.getFocDesc()), null);

    displayList.setDbResident(false);
    
    copyFromRealListToDisplayList();
        
    displayList.setListOrder(realList.getListOrder());
  }

  public void dispose(){
    if(displayList != null){
      displayList.dispose();
      displayList = null;
    }
    
    realList = null;
  }
  
  public boolean isDoNotRemoveRealItems() {
    return doNotRemoveRealItems;
  }
  
  public void setDoNotRemoveRealItems(boolean doNotRemoveRealItems) {
    this.doNotRemoveRealItems = doNotRemoveRealItems;
  }
  
  public void construct(){
    completeTheDisplayList(realList, displayList);
  }

  public void reconstructDisplayList(){
  	getDisplayList().removeAll();
  	copyFromRealListToDisplayList();
  	construct();
  }

  public FocList getDisplayList(){
    return displayList;
  }

  public FocList getRealList(){
    return realList;
  }
  
  private void copyFromRealListToDisplayList(){
    Iterator iter = realList.focObjectIterator();
    while(iter != null && iter.hasNext()){
      FocObject sourceObj = (FocObject) iter.next();
      if(sourceObj != null){
        FocObject targetObj = null;
        targetObj = displayList.newEmptyItem();
        copyFromObjectToObjectInternal(targetObj, sourceObj);
        displayList.add(targetObj);
        targetObj.backup();
        
        targetObj.setCreated(sourceObj.isCreated());
        //targetObj.getFocProperty(2).setBackground(Color.blue);
        targetObj.setModified(sourceObj.isModified());//dsfsfsfsfsdsfsfs
        //targetObj.setModified(sourceObj.isModified());
      }
    }
    //displayList.copy(realList);
  }
  
  private void copyFromObjectToObjectInternal(FocObject target, FocObject source){
    copyFromObjectToObject(target, source);
    //Copying the real reference is usefull from RealList To Display List 
    //because if we want to edit the details panel of an object it is important to have the real database ref
    //This line could have been added only after the copyFromObjectToObject of the copyRealToDiaplay 
    //but we generalized the intervension.
    if(source.hasRealReference()){
      target.setReference(source.getReference().getInteger());
    }
    //End.
  }
  
  public void updateRealList(){
    Iterator iter = null;
    
    ArrayList<FocObject> arrayOfObjectsToRemove = new ArrayList<FocObject>();
    
    //Removing realItems abscent from the display
    if(!isDoNotRemoveRealItems()){
      iter = realList.newSubjectIterator();
      while(iter != null && iter.hasNext()){
        FocObject realObj = (FocObject) iter.next();
        FocObject dispObj = findObjectInList(displayList, realObj);
        
        if(dispObj == null || !isDisplayItemToBeSaved(dispObj)){
          //realObj.setCreated(false);
          //realObj.setModified(false);
        	arrayOfObjectsToRemove.add(realObj);
        }
      }        
    }

    for(int i=0; i<arrayOfObjectsToRemove.size(); i++){
    	FocObject realObj = (FocObject) arrayOfObjectsToRemove.get(i);
    	realObj.setDeletable(true);
    }
    
    //Adding and copying from the display list
    iter = displayList.newSubjectIterator();
    while(iter != null && iter.hasNext()){
      FocObject dispObj = (FocObject) iter.next();
      
      FocObject realObj = null;
      if(isDoNotRemoveRealItems()){
        realObj = findObjectInList(realList, dispObj);
        
        if(realObj == null && isDisplayItemToBeSaved(dispObj)){
          realObj = (FocObject) realList.newEmptyItem();
          realList.add(realObj);
        }
        
      }else{
        if(isDisplayItemToBeSaved(dispObj)){
          if(dispObj.isCreated() || dispObj.isModified()){
            realObj = findObjectInList(realList, dispObj);

            if(realObj == null){
              realObj = (FocObject) realList.newEmptyItem();
              realList.add(realObj);
            }            
          }
        }
      }

      if(realObj != null){
        copyFromObjectToObjectInternal(realObj, dispObj);
        //realObj.setCreated(dispObj.isCreated());
        //realObj.setModified(dispObj.isModified());
      }
    }
    
    realList.setModified(true);
    if(realList.getMasterObject() != null && realList.getMasterObject().isCreated()){
      realList.setCreated(true);
    }
    
    if(!realList.isCreated()){
      FocObject uniqueForeignObject = realList.getTheUniqueForeignObject();
      if(uniqueForeignObject != null && uniqueForeignObject.isCreated()){
        realList.setCreated(true);
      }
    }
  }
}