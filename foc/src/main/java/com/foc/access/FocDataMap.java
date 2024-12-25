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
package com.foc.access;

import java.util.HashMap;
import java.util.Iterator;

import com.foc.list.FocList;
import com.foc.shared.dataStore.IFocData;
import com.foc.util.FocMath;

@SuppressWarnings("serial")
public class FocDataMap extends HashMap<String, IFocData> implements IFocData {

  private IFocData focData = null;
  
  public FocDataMap(){
    
  }

  public FocDataMap(IFocData focData){
    this();
    this.focData = focData;
  }
  
	@Override
	public void dispose() {
		if(focData != null){
			focData.dispose();
			focData = null;
		}
		clear();
	}

  public void putString(String key, String value){
    put(key, new FocDataConstant(value));
  }
  
  @Override
  public boolean iFocData_isValid() {
  	boolean valid = focData != null ? focData.iFocData_isValid() : true;
    Iterator<IFocData> iter = values().iterator();
    while(iter != null && iter.hasNext()){
      IFocData data = iter.next();
      if(data != null) valid = valid && data.iFocData_isValid();
    }
    return valid;
  }

  @Override
  public boolean iFocData_validate() {
    boolean validate = focData != null ? focData.iFocData_validate() : false;
    Iterator<IFocData> iter = values().iterator();
    while(iter != null && iter.hasNext()){
      IFocData data = iter.next();
      if(data != null) validate = validate || data.iFocData_validate();
    }
    return validate;
  }

  @Override
  public void iFocData_cancel() {
    if(focData != null){
      focData.iFocData_cancel();
    }
    Iterator<IFocData> iter = values().iterator();
    while(iter != null && iter.hasNext()){
      IFocData data = iter.next();
      if(data != null) data.iFocData_cancel();
    }    
  }

  @Override
  public IFocData iFocData_getDataByPath(String path) {
    IFocData data = focData != null ? focData.iFocData_getDataByPath(path) : null;

    if(data == null){
      String    propertyName = path;
      String    nextPath     = null;
      
      int idx = path.indexOf(".");
      if(idx > 0){
        propertyName = path.substring(0, idx);
        nextPath     = path.substring(idx+1, path.length());
      }

      int idxCroche = propertyName.indexOf("[");
      int itemIndex = -1;
      if(idxCroche > 0){
      	String itemIndexStr = propertyName.substring(idxCroche+1, propertyName.length()-1);
        propertyName = propertyName.substring(0, idxCroche);
        if(itemIndexStr != null && !itemIndexStr.isEmpty()){
        	itemIndex = FocMath.parseInteger(itemIndexStr);
        }
      }

      if(data == null){
      	data = get(propertyName);
      }
      if(data instanceof FocList){
        if(data != null && itemIndex >= 0){
        	FocList focList = (FocList)data;
        	if(itemIndex < focList.size()){
        		data = focList.getFocObject(itemIndex);
        	}
        }
      }

      if(data != null && nextPath != null && !nextPath.isEmpty()){
        data = data.iFocData_getDataByPath(nextPath);
      }      
//      
//      
//    	
//    	
//    	
//      int idx = path.indexOf(".");
//      if(idx > 0){
//        String key      = path;
//        String nextPath = null;
//        key      = path.substring(0, idx);
//        nextPath = path.substring(idx+1, path.length());
//        
//        IFocData focData = get(key);
//        if(focData != null){ 
//        	data = focData.iFocData_getDataByPath(nextPath);
//        }else{
//        	Globals.logString("Could not find DATA : "+path);
//        }
//      }else{
//        data = get(path);
//      }
    }

    return data;
  }

  @Override
  public Object iFocData_getValue() {
    return null;
  }

  public IFocData getMainFocData() {
    return focData;
  }
  
  public void setMainFocData(IFocData focData) {
    this.focData = focData;
  }
}
