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
package com.foc.business.company;

import java.util.Iterator;

import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.list.DisplayList;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class UserCompanyRightsDisplayList extends DisplayList {

  public UserCompanyRightsDisplayList(FocList realList) {
    super(realList);
    setDoNotRemoveRealItems(false);
    construct();
  }

  @SuppressWarnings("unchecked")
	@Override
  public void completeTheDisplayList(FocList realList, FocList displayList) {
    FocListOrder focListOrder = new FocListOrder();
    focListOrder.addField(FFieldPath.newFieldPath(UserCompanyRightsDesc.FLD_USER, FocUserDesc.FLD_NAME));
    
    Company  company  = (Company) realList.getFatherSubject();
    FocList  userList = FocUserDesc.getList();
    Iterator<FocUser> iter = userList.newSubjectIterator();
    
    while(iter != null && iter.hasNext()){
      FocUser user = (FocUser)iter.next();
      if(user != null){
        UserCompanyRights userCompanyRights = (UserCompanyRights) displayList.searchByPropertyObjectValue(UserCompanyRightsDesc.FLD_USER, user);
        if(userCompanyRights == null){
        	userCompanyRights = (UserCompanyRights) displayList.newEmptyItem();
        	userCompanyRights.setUser(user);
        	userCompanyRights.setCompany(company);
        	userCompanyRights.setAccessRight(UserCompanyRightsDesc.ACCESS_RIGHT_NONE);
          displayList.add(userCompanyRights);
        }
      }
    }
    displayList.setListOrder(focListOrder);
  }

  @Override
  public void copyFromObjectToObject(FocObject target, FocObject source) {
    UserCompanyRights tar = (UserCompanyRights) target;
    UserCompanyRights src = (UserCompanyRights) source;
    
    tar.copy(src);
  }

  @Override
  public FocObject findObjectInList(FocList focList, FocObject object) {
    FocUser user = (FocUser) object.getPropertyObject(UserCompanyRightsDesc.FLD_USER);
    return user != null ? focList.searchByPropertyObjectValue(UserCompanyRightsDesc.FLD_USER, user) : null;
  }

  @Override
  public boolean isDisplayItemToBeSaved(FocObject object) {
    UserCompanyRights companyUserRight = (UserCompanyRights)object;
    boolean yes = companyUserRight.getAccessRight() != UserCompanyRightsDesc.ACCESS_RIGHT_NONE;
    return yes;
  }
}
