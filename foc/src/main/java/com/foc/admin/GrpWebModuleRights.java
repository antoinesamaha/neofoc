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
public class GrpWebModuleRights extends FocObject {

  public GrpWebModuleRights(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public FocGroup getGroup(){
    return (FocGroup) getPropertyObject(GrpWebModuleRightsDesc.FLD_GROUP);
  }
  
  public void setGroup(FocGroup group){
    setPropertyObject(GrpWebModuleRightsDesc.FLD_GROUP, group);
  }
   
  public String getModuleName(){
    return getPropertyString(GrpWebModuleRightsDesc.FLD_MODULE_NAME);
  }

  public void setModuleName(String key){
    setPropertyString(GrpWebModuleRightsDesc.FLD_MODULE_NAME, key);
  }

  public String getModuleTitle(){
    return getPropertyString(GrpWebModuleRightsDesc.FLD_MODULE_TITLE);
  }

  public void setModuleTitle(String key){
    setPropertyString(GrpWebModuleRightsDesc.FLD_MODULE_TITLE, key);
  }

  public boolean isAdminModule(){
    return getPropertyBoolean(GrpWebModuleRightsDesc.FLD_IS_ADMIN);
  }

  public void setAdminModule(boolean admin){
    setPropertyBoolean(GrpWebModuleRightsDesc.FLD_IS_ADMIN, admin);
  }
  
  public int getRight(){
    return getPropertyMultiChoice(GrpWebModuleRightsDesc.FLD_ACCESS_RIGHT);
  }
  
  public void setRight(int right){
    setPropertyMultiChoice(GrpWebModuleRightsDesc.FLD_ACCESS_RIGHT, right);
  }
}
