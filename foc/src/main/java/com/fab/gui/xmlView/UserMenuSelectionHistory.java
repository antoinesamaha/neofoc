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

public class UserMenuSelectionHistory extends FocObject{

  public UserMenuSelectionHistory(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public void setUser(FocUser user){
    setPropertyObject(UserMenuSelectionHistoryDesc.FLD_USER, user);
  }
  
  public FocUser getUser(){
    return (FocUser) getPropertyObject(UserMenuSelectionHistoryDesc.FLD_USER);
  }
  
  public void setMenuCode(String menuCode){
    setPropertyString(UserMenuSelectionHistoryDesc.FLD_MENU_CODE, menuCode);
  }
  
  public String getMenuCode(){
    return getPropertyString(UserMenuSelectionHistoryDesc.FLD_MENU_CODE);
  }
  
  public void setMenuOrder(int menuOrder){
    setPropertyInteger(UserMenuSelectionHistoryDesc.FLD_MENU_ORDER, menuOrder);
  }
  
  public int getMenuOrder(){
    return getPropertyInteger(UserMenuSelectionHistoryDesc.FLD_MENU_ORDER);
  }
}
