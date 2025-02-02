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
package com.fab.gui.xmlView;

import com.foc.admin.FocUser;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

@SuppressWarnings("serial")
public class UserXMLView extends FocObject{

  public UserXMLView(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public void setStorageName(String storageName){
    setPropertyString(UserXMLViewDesc.FLD_STORAGE_NAME, storageName);
  }
  
  public String getStorageName(){
    return getPropertyString(UserXMLViewDesc.FLD_STORAGE_NAME);
  }
  
  public void setView(String userView){
    setPropertyString(UserXMLViewDesc.FLD_VIEW, userView);
  }
  
  public String getView(){
    return getPropertyString(UserXMLViewDesc.FLD_VIEW);
  }
  
  public void setPrintingView(String printingView){
    setPropertyString(UserXMLViewDesc.FLD_PRINTING_VIEW, printingView);
  }
  
  public String getPrintingView(){
    return getPropertyString(UserXMLViewDesc.FLD_PRINTING_VIEW);
  }
  
  public FocUser getUser(){
    return (FocUser) getPropertyObject(UserXMLViewDesc.FLD_USER);
  }

  public void setUser(FocUser user){
    setPropertyObject(UserXMLViewDesc.FLD_USER, user);
  }
  
  public void setContext(String xmlContext){
    setPropertyString(UserXMLViewDesc.FLD_CONTEXT, xmlContext);
  }
  
  public String getContext(){
    return getPropertyString(UserXMLViewDesc.FLD_CONTEXT);
  }
  
  public void setType(int type){
    setPropertyInteger(UserXMLViewDesc.FLD_TYPE, type);
  }
  
  public int getType(){
    return getPropertyInteger(UserXMLViewDesc.FLD_TYPE);
  }
}
