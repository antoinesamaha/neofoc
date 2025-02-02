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
// EXTERNAL PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// LIST
// DESCRIPTION

/*
 * Created on 20-May-2005
 */
package com.foc.admin.userModuleAccess;

import com.foc.admin.FocGroup;
import com.foc.admin.FocUser;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class UserModuleAccess extends FocObject{
  
  public static final int VIEW_READ_ONLY = 2;  
  
  
  public UserModuleAccess(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public void dispose(){
    super.dispose();
  }
  
  public FocUser getUser(){
    return (FocUser) getPropertyObject(UserModuleAccessDesc.FLD_USER);
  }
  
  public void setUser(FocUser user){
  	setPropertyObject(UserModuleAccessDesc.FLD_USER, user);
  }

  public FocGroup getGroup(){
    return (FocGroup) getPropertyObject(UserModuleAccessDesc.FLD_GROUP);
  }
  
  public void setGroup(FocGroup user){
  	setPropertyObject(UserModuleAccessDesc.FLD_GROUP, user);
  }

  public String getModule(){
    return getPropertyString(UserModuleAccessDesc.FLD_MODULE);
  }
  
  public void setModule(String module){
  	setPropertyString(UserModuleAccessDesc.FLD_MODULE, module);
  }
}
