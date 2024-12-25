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
package com.foc.wrapper;

import java.util.ArrayList;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.list.FocList;

public class WrapperLevel {

  private FocDesc focDesc = null;
  private int foreignKeyField = FField.NO_FIELD_ID;
  private int displayField = FField.NO_FIELD_ID;
  private int objectFieldID = FField.NO_FIELD_ID;
  private WrapperLevel fatherLevel = null;
  
  private ArrayList<WrapperLevel> childrenLevelList = null;
  
  public WrapperLevel(FocDesc desc, int foreignField, int displayField){
    this.focDesc = desc;
    this.foreignKeyField = foreignField;
    this.displayField = displayField;
    childrenLevelList = new ArrayList<WrapperLevel>();    
  }
  
  public WrapperLevel(FocDesc desc, int displayField){
    this(desc, FField.NO_FIELD_ID, displayField);
  }
    
  public void dispose(){
    focDesc = null;
    if (childrenLevelList != null){
      for(int i=0; i<childrenLevelList.size(); i++){
        WrapperLevel level = childrenLevelList.get(i);
        level.dispose();
      }
      childrenLevelList.clear();
      childrenLevelList = null;
    }
    fatherLevel = null;
  }
  
  public WrapperLevel getFatherLevel(){
  	return this.fatherLevel;
  }
  
  public void setFatherLevel(WrapperLevel fatherLevel){
  	this.fatherLevel = fatherLevel;
  }
  
  public void addLevel(WrapperLevel level){
    if (level != null){
      childrenLevelList.add(level);
      level.setFatherLevel(this);
    }
  }
  
  public String getDisplayNameFromObject(FocObject wrapperFocObject){
  	return wrapperFocObject != null ? wrapperFocObject.getPropertyString(getDisplayField()) : null;
  }
  
  public Wrapper newWrapperObject(FocList wrapperList, Wrapper fatherObject, FocObject encapsulatedFocObject){
    Wrapper wrapper = (Wrapper) wrapperList.newEmptyItem();
    wrapper.setLevel(this);
    wrapper.setPropertyString(FField.FLD_NAME, getDisplayNameFromObject(encapsulatedFocObject));
    
    wrapper.setPropertyObject(getObjectFieldID(), encapsulatedFocObject);
    
    if(encapsulatedFocObject.getFatherObject() != null){
      Wrapper objectTreeWrapperFather = (Wrapper) wrapperList.searchByPropertyObjectValue(getObjectFieldID(), encapsulatedFocObject.getFatherObject());
      if(objectTreeWrapperFather == null){
        objectTreeWrapperFather = newWrapperObject(wrapperList, fatherObject, encapsulatedFocObject.getFatherObject());
      }
      wrapper.setPropertyObject(FField.FLD_FATHER_NODE_FIELD_ID, objectTreeWrapperFather);
    }else{
      wrapper.setPropertyObject(FField.FLD_FATHER_NODE_FIELD_ID, fatherObject);
    }
    
    return wrapper;
  }
  
  public void addLevelToFocList(FocList wrapperList, Wrapper fatherObject, FocList childrenListOfThisLevel){
    for(int i = 0; i < childrenListOfThisLevel.size(); i++){
      FocObject wrapperFocObj = childrenListOfThisLevel.getFocObject(i);
      
      Wrapper w = (Wrapper) wrapperList.searchByPropertyObjectValue(getObjectFieldID(), wrapperFocObj);
      if(w == null){
        w = newWrapperObject(wrapperList, fatherObject, wrapperFocObj);
      }
      
      for(int l=0; l<childrenLevelList.size(); l++){
        WrapperLevel level = childrenLevelList.get(l);
        FocList childrenList = w.getChildfocList(wrapperFocObj, level);
        if(childrenList != null){
	        level.addLevelToFocList(wrapperList, w, childrenList);
	        //NONONO
	        //childrenList.dispose();
	        //childrenList = null;
        }
      }
    }
  }
  
  public Wrapper getWrapper_createIfNeeded(FocList wrapperList, Wrapper fatherWrapper, FocObject wrapperFocObject){
  	Wrapper wrapper = null;
  	if(wrapperList != null){
  		for(int i = 0; i < wrapperList.size() && wrapper == null; i++){
  			Wrapper aWrapper = (Wrapper) wrapperList.getFocObject(i);
  			if(aWrapper != null && aWrapper.getPropertyObject(FField.FLD_FATHER_NODE_FIELD_ID) == fatherWrapper && aWrapper.getPropertyString(FField.FLD_NAME).equals(getDisplayNameFromObject(wrapperFocObject))){
  				wrapper = aWrapper;
  			}
  		}
  		if(wrapper == null){
  			wrapper = newWrapperObject(wrapperList, fatherWrapper, wrapperFocObject);
  		}
  	}
  	return wrapper;
  }
  
  public int getDisplayField(){
    return displayField;
  }
  public int getForeignKeyField(){
    return foreignKeyField;
  }
  public FocDesc getFocDesc() {
    return focDesc;
  }
  
  public ArrayList<WrapperLevel> getChildrenLevelList(){
    return childrenLevelList;
  }

  public int getObjectFieldID() {
    return objectFieldID;
  }

  public void setObjectFieldID(int objectFieldID) {
    this.objectFieldID = objectFieldID;
  }
}
