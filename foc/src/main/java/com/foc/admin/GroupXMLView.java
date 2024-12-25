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
package com.foc.admin;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

@SuppressWarnings("serial")
public class GroupXMLView extends FocObject{
	
  public GroupXMLView(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
    
  public FocGroup getGroup(){
    return (FocGroup) getPropertyObject(GroupXMLViewDesc.FLD_GROUP);
  }
  
  public void setGroup(FocGroup group){
    setPropertyObject(GroupXMLViewDesc.FLD_GROUP, group);
  }
  
  public int getRight(){
    return getPropertyMultiChoice(GroupXMLViewDesc.FLD_VIEW_RIGHT);
  }

  public void setRight(int right){
    setPropertyMultiChoice(GroupXMLViewDesc.FLD_VIEW_RIGHT, right);
  }
  
  public void setStorageName(String storageName){
    setPropertyString(GroupXMLViewDesc.FLD_STORAGE_NAME, storageName);
  }
  
  public String getStorageName(){
    return getPropertyString(GroupXMLViewDesc.FLD_STORAGE_NAME);
  }
  
  public void setType(int type){
    setPropertyInteger(GroupXMLViewDesc.FLD_TYPE, type);
  }
  
  public int getType(){
    return getPropertyInteger(GroupXMLViewDesc.FLD_TYPE);
  }
  
  public void setView(String userView){
    setPropertyString(GroupXMLViewDesc.FLD_VIEW, userView);
  }
  
  public String getView(){
    return getPropertyString(GroupXMLViewDesc.FLD_VIEW);
  }
  
  public void setContext(String xmlContexr){
    setPropertyString(GroupXMLViewDesc.FLD_CONTEXT, xmlContexr);
  }
  
  public String getContext(){
    return getPropertyString(GroupXMLViewDesc.FLD_CONTEXT);
  }
  
  public void setXMLViewsKey(String storageName, int type, String xmlContext){
    setStorageName(storageName);
    setType(type);
    setContext(xmlContext);
  }
}
