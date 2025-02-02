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
package com.foc.dragNDrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.property.FProperty;
import com.foc.tree.FNode;

public class FocTransferable implements Transferable {
  private static String KEY_SOURCE_FOC_OBJECT        = "srcFocObj";
  private static String KEY_SOURCE_FOC_LIST          = "srcFocList";
  private static String KEY_SOURCE_PROPERTY          = "srcProp";
  private static String KEY_SOURCE_COMPONENT         = "srcComp";
  private static String KEY_TABLE_SOURCE_ROW         = "focObjInitPos";
  private static String KEY_TABLE_SOURCE_COL         = "srcCol";
  private static String KEY_TABLE_TARGET_ROW         = "targetRow";
  private static String KEY_TABLE_TARGET_COL         = "targetCol";
  private static String KEY_TARGET_FOC_OBJECT        = "tarFocObj";
  private static String KEY_SOURCE_X                 = "sourceX";
  private static String KEY_SOURCE_Y                 = "sourceY";
  private static String KEY_TARGET_X                 = "TargerX";
  private static String KEY_TARGET_Y                 = "TargerY";
  private static String KEY_SOURCE_NODE              = "srcNode";
  
  private HashMap<String, Object> defaultObjectsMap    = null;
  private HashMap<String, Object> additionalObjectsMap = null;
  
  private boolean allowDropInCurrentComponent = true;
  
  public FocTransferable(){
    this.allowDropInCurrentComponent = true;
  }
  
  public void dispose(){
    if(defaultObjectsMap != null){
      defaultObjectsMap.clear();
      defaultObjectsMap = null;
    }
    if(additionalObjectsMap != null){
      additionalObjectsMap.clear();
      additionalObjectsMap = null;
    }
  }
  
  public Object duplicate() {
  	FocTransferable clonedObj = new FocTransferable();
  	clonedObj.setAllowDropInCurrentComponent(allowDropInCurrentComponent);
  	if(defaultObjectsMap != null){
      HashMap<String, Object> newMap    = new HashMap<String, Object>();
      Iterator<String> iter = defaultObjectsMap.keySet().iterator();
      while(iter != null && iter.hasNext()){
      	String key = iter.next();
      	clonedObj.addDefaultObject(key, defaultObjectsMap.get(key));
      }
      defaultObjectsMap = newMap;
  	}
  	if(additionalObjectsMap != null){
      HashMap<String, Object> newMap    = new HashMap<String, Object>();
      Iterator<String> iter = additionalObjectsMap.keySet().iterator();
      while(iter != null && iter.hasNext()){
      	String key = iter.next();
      	clonedObj.addAdditionalObject(key, additionalObjectsMap.get(key));
      }
      additionalObjectsMap = newMap;
  	}
  	return clonedObj;
  }
  
  //00000000000000000000000000000000000000000
  //00000000000000000000000000000000000000000
  //Allow drop in current component 
  //00000000000000000000000000000000000000000
  //00000000000000000000000000000000000000000
  
  public boolean isAllowDropInCurrentComponent(){
    return allowDropInCurrentComponent;
  }
  
  public void setAllowDropInCurrentComponent(boolean allow){
    allowDropInCurrentComponent = allow;
  }

  
  //00000000000000000000000000000000000000000
  //00000000000000000000000000000000000000000
  //Default objects
  //00000000000000000000000000000000000000000
  //00000000000000000000000000000000000000000
  private HashMap<String, Object> getDefaultObjectsMap(){
    if(this.defaultObjectsMap == null){
      this.defaultObjectsMap =  new HashMap<String, Object>();
    }
    return this.defaultObjectsMap;
  }
  
  private void addDefaultObject(String key, Object value){
    HashMap<String, Object> defaultObjectsMap = getDefaultObjectsMap();
    defaultObjectsMap.put(key, value);
  }
  
  private Object getDefaultObject(String key){
    HashMap<String, Object> defaultObjectsMap = getDefaultObjectsMap();
    return defaultObjectsMap.get(key);
  }
  
  
  public FocDragable getSourceComponent(){
    Object o = getDefaultObject(KEY_SOURCE_COMPONENT);
    return o != null ? (FocDragable)o : null;
  }
  
  public void setSourceComponent(FocDragable sourceComponent){
    addDefaultObject(KEY_SOURCE_COMPONENT, sourceComponent);
  }
  
  public FocObject getSourceFocObject(){
    Object o = getDefaultObject(KEY_SOURCE_FOC_OBJECT);
    return o != null ? (FocObject)o : null;
  }
  
  public void setSourceFocObject(FocObject sourceFocObject){
    addDefaultObject(KEY_SOURCE_FOC_OBJECT, sourceFocObject);
  }

  public FocObject getTargetFocObject(){
    Object o = getDefaultObject(KEY_TARGET_FOC_OBJECT);
    return o != null ? (FocObject)o : null;
  }
  
  public void setTargetFocObject(FocObject targetFocObject){
    addDefaultObject(KEY_TARGET_FOC_OBJECT, targetFocObject);
  }

  public FocList getSourceFocList(){
    Object o = getDefaultObject(KEY_SOURCE_FOC_LIST);
    return o != null ? (FocList)o : null;
  }
  
  public void setSourceFocList(FocList sourceFocList){
    addDefaultObject(KEY_SOURCE_FOC_LIST, sourceFocList);
  }
  
  public FProperty getSourceProperty(){
    Object o = getDefaultObject(KEY_SOURCE_PROPERTY);;
    return o != null ?(FProperty)o : null;
  }
  
  public void setSourceProperty(FProperty sourceProperty){
    addDefaultObject(KEY_SOURCE_PROPERTY, sourceProperty);
  }

  public int getTableSourceRow(){
    Object o = getDefaultObject(KEY_TABLE_SOURCE_ROW);
    return o != null ? (Integer)o : -1;
  }
  
  public void setTableSourceRow(int firstPosition){
    addDefaultObject(KEY_TABLE_SOURCE_ROW, firstPosition);
  }
  
  public int getTableSourceColumn(){
    Object o = getDefaultObject(KEY_TABLE_SOURCE_COL);
    return o != null ? (Integer)o : -1;
  }
  
  public void setTableSourceColumn(int sourceColumn){
    addDefaultObject(KEY_TABLE_SOURCE_COL, sourceColumn);
  }
  
  
  public int getTableTargetRow(){
    Object o = getDefaultObject(KEY_TABLE_TARGET_ROW);
    return o != null ? (Integer)o : -1;
  }
  
  public void setTableTargetRow(int targetRow){
    addDefaultObject(KEY_TABLE_TARGET_ROW, targetRow);
  }
  
  public int getTableTargetColumn(){
    Object o = getDefaultObject(KEY_TABLE_TARGET_COL);
    return o != null ? (Integer)o : -1;
  }
  
  public void setTableTargetColumn(int targetColumn){
    addDefaultObject(KEY_TABLE_TARGET_COL, targetColumn);
  }
  
  public int getSourceX(){
  	Object o = getDefaultObject(KEY_SOURCE_X);
    return o != null ? (Integer)o : -1;
  }
  
  public void setSourceX(int sourceX){
  	addDefaultObject(KEY_SOURCE_X, sourceX);
  }
  
  public int getSourceY(){
  	Object o = getDefaultObject(KEY_SOURCE_Y);
    return o != null ? (Integer)o : -1;
  }
  
  public void setSourceY(int sourceY){
  	addDefaultObject(KEY_SOURCE_Y, sourceY);
  }
  
  public int getTargetX(){
  	Object o = getDefaultObject(KEY_TARGET_X);
    return o != null ? (Integer)o : -1;
  }
  
  public void setTargetX(int targetX){
  	addDefaultObject(KEY_TARGET_X, targetX);
  }
  
  public int getTargetY(){
  	Object o = getDefaultObject(KEY_TARGET_Y);
    return o != null ? (Integer)o : -1;
  }
  
  public void setTargetY(int targetY){
  	addDefaultObject(KEY_TARGET_Y, targetY);
  }
  
  public FNode getSourceNode(){
  	Object o = getDefaultObject(KEY_SOURCE_NODE);
    return o != null ? (FNode)o : null; 
  }
  
  public void setSourceNode(FNode sourceNode){
  	addDefaultObject(KEY_SOURCE_NODE, sourceNode);
  }
  
  
  //00000000000000000000000000000000000000000
  //00000000000000000000000000000000000000000
  //Additional objects
  //00000000000000000000000000000000000000000
  //00000000000000000000000000000000000000000
  private HashMap<String, Object> getAdditionalObjectsMap(){
    if(this.additionalObjectsMap == null){
      this.additionalObjectsMap = new HashMap<String, Object>();
    }
    return this.additionalObjectsMap;
  }
  
  public void addAdditionalObject(String key, Object value){
    HashMap<String, Object> additionalObjectsMap = getAdditionalObjectsMap();
    additionalObjectsMap.put(key, value);
  }
  
  public Object getAdditionalObject(String key){
    HashMap<String, Object> additionalObjectsMap = getAdditionalObjectsMap();
    return additionalObjectsMap.get(key);
  }

  
  //00000000000000000000000000000000000000000
  //00000000000000000000000000000000000000000
  //Transferable implementaion
  //00000000000000000000000000000000000000000
  //00000000000000000000000000000000000000000
  
  private static DataFlavor focDataFlavor = null;
  
  public static DataFlavor getFocDataFlavor(){
    try{
      if(focDataFlavor == null){
        String MIME= DataFlavor.javaJVMLocalObjectMimeType + ";class=" + FocObject.class.getName();
        focDataFlavor = new DataFlavor(MIME);
      }
    }catch(Exception e){
      Globals.logException(e);
    }
    return focDataFlavor;
  }

  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    return this;
  }

  public DataFlavor[] getTransferDataFlavors() {
    DataFlavor[] dataFlavor = new DataFlavor[1];
    dataFlavor[0] = FocTransferable.getFocDataFlavor();
    return dataFlavor;
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return true;
  }
}
