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
package com.foc.gui.findObject;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.list.FocList;

public class FindObject extends FocObject {

  public FindObject(FocConstructor constr) {
    super(constr);
    newFocProperties();
    
    if(!isStartsWith() && !isContains()){
      setStartsWith(false);
      setContains(true);
    }
  }

  public FindObject() {
  	this(new FocConstructor(FindObjectDesc.getInstance(), null));
  }
  
  public String getFindExpression(){
    return getPropertyString(FindObjectDesc.FLD_FIND);
  }

  public void setFindExpression(String expression){
    setPropertyString(FindObjectDesc.FLD_FIND, expression);
  }

  public boolean isStartsWith(){
    return getPropertyBoolean(FindObjectDesc.FLD_STARTS_WITH);
  }
  
  public boolean isContains(){
    return getPropertyBoolean(FindObjectDesc.FLD_CONTAINS);
  }
  
  public void setStartsWith(boolean val){
    setPropertyBoolean(FindObjectDesc.FLD_STARTS_WITH, val);
  }
  
  public void setContains(boolean val){
    setPropertyBoolean(FindObjectDesc.FLD_CONTAINS, val);
  }
  
  public static FindObject getFindObject(){
    FocList findObjectList = FindObjectDesc.getList(FocList.LOAD_IF_NEEDED);
    return (FindObject)findObjectList.getOrInsertAnItem();
  }
  
}
