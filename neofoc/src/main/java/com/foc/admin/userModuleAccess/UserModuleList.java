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
package com.foc.admin.userModuleAccess;

import com.foc.admin.FocGroup;
import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.admin.GrpWebModuleRights;
import com.foc.admin.GrpWebModuleRightsDesc;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class UserModuleList extends FocList {

	public UserModuleList() {
		super(new FocLinkSimple(UserModuleAccessDesc.getInstance()));
		setDbResident(false);
	}
	
	public void dispose(){
		super.dispose();
	}
	
	public void fill(){
		FocList userList = FocUserDesc.getList();
		for(int i=0; i<userList.size(); i++){
			FocUser user = (FocUser) userList.getFocObject(i);
			if(!user.isSuspended() && !user.getName().equals("01BARMAJA")){
				FocGroup group = user.getGroup();
				FocList webModuleList = group.getWebModuleRightsList();
				for(int m=0; m<webModuleList.size(); m++){
					GrpWebModuleRights moduleRight = (GrpWebModuleRights) webModuleList.getFocObject(m);
					if(moduleRight != null && !moduleRight.isAdminModule() && moduleRight.getRight() == GrpWebModuleRightsDesc.ACCESS_FULL && !moduleRight.getModuleTitle().isEmpty()){
						UserModuleAccess uma = (UserModuleAccess) newEmptyItem();
						uma.setUser(user);
						uma.setGroup(group);
						uma.setModule(moduleRight.getModuleName());
						add(uma);
					}
				}
			}
		}
	}
}
