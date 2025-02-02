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
package com.foc.saas.manager;

import java.util.ArrayList;

import com.foc.Globals;
import com.foc.admin.FocGroup;
import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.admin.FocUserHistory;
import com.foc.admin.FocUserHistoryConst;
import com.foc.admin.FocUserHistoryDesc;
import com.foc.admin.FocUserHistoryList;
import com.foc.admin.GrpWebModuleRights;
import com.foc.admin.GrpWebModuleRightsDesc;
import com.foc.business.company.UserCompanyRights;
import com.foc.business.company.UserCompanyRightsDesc;
import com.foc.business.workflow.WFOperator;
import com.foc.business.workflow.WFSite;
import com.foc.list.FocList;
import com.foc.shared.xmlView.XMLViewKey;

public abstract class SaaSRoleAdaptor {

	public abstract void adaptGroup();
	public abstract void adaptUser(FocUser user);
	public abstract ArrayList<String> getWebModuleNamesArray();
	public abstract XMLViewKey getRoleSpecificXMLViewKey();
	
	private int id = 0;
	private SaaSApplicationAdaptor applicationAdaptor = null;
	
	public SaaSRoleAdaptor(SaaSApplicationAdaptor applicationAdaptor, int id){
		this.id = id;
		this.applicationAdaptor = applicationAdaptor;
	}
	
	public void dispose(){
		applicationAdaptor = null;
	}
	
	public int getID(){
		return id;
	}
	
	public SaaSApplicationAdaptor getApplicationAdaptor(){
		return applicationAdaptor;
	}
	
	public WFSite getSite(){
		return getApplicationAdaptor() != null ? getApplicationAdaptor().getSite() : null;
	}
	
	public static SaaSRoleAdaptor getAppRoleAdaptor() {
		FocUser user = Globals.getApp().getUser_ForThisSession();
		return getAppRoleAdaptor(user);
	}
	
	public static SaaSRoleAdaptor getAppRoleAdaptor(FocUser user) {
		int applicationRole = user != null ? user.getSaasApplicationRole() : FocUserDesc.APPLICATION_ROLE_NONE;
		SaaSApplicationAdaptor appAdaptor = SaaSConfig.getInstance().getSaasApplicationAdaptor();
		return appAdaptor != null ? appAdaptor.getRoleAdaptor(applicationRole) : null;
	}
	
	public void setUserTitle(FocUser user, String title){
		if(getApplicationAdaptor() != null){
			WFSite site = getSite();
			if(site != null){
				boolean found = false;
				for(int k=0;k<site.getOperatorList().size();k++){
					WFOperator operator = (WFOperator) site.getOperatorList().getFocObject(k);
					if(operator.getUser().equalsRef(user)){
						if(found){
							operator.delete();
						}else{
							operator.setTitle(getApplicationAdaptor().getTitle(title, false));
							found = true;
						}
					}
				}

				if(!found){
					//Set the User to That Title
					WFOperator operator = (WFOperator) site.getOperatorList().newEmptyItem();
					operator.setUser(user);
					operator.setTitle(getApplicationAdaptor().getTitle(title, false));
					operator.validate(true);
					site.getOperatorList().add(operator);
				}
				site.getOperatorList().validate(true);
				site.validate(true);
				addUserCompanyRights(user);
			}
		}
	}
	
	private void addUserCompanyRights(FocUser user){
		FocList userCompanyRightsList = user.getCompanyRightsList();
		UserCompanyRights companyRights = (UserCompanyRights) userCompanyRightsList.newEmptyItem();
		companyRights.setCompany(getApplicationAdaptor().getCompany());
		companyRights.setUser(user);
		companyRights.setAccessRight(UserCompanyRightsDesc.ACCESS_RIGHT_READ_WRITE);
		companyRights.validate(true);
		userCompanyRightsList.add(companyRights);
		userCompanyRightsList.validate(true);
		user.validate(true);
	}
	
	public void configureWebModuleRights(FocGroup group, ArrayList<String> webModulesArrayList){
		GrpWebModuleRights webModuleRight = null;
		if(group != null){
			FocList webModuleRightsList = group.getWebModuleRightsList();
			webModuleRightsList.reloadFromDB();
			for(int i=0; i<webModuleRightsList.size(); i++){
				webModuleRight = (GrpWebModuleRights) webModuleRightsList.getFocObject(i);
				
				boolean isFound = false;
				for(int j=0; j<webModulesArrayList.size() && !isFound; j++){
					String webModuleName = webModulesArrayList.get(j);
					isFound = webModuleName.equalsIgnoreCase(webModuleRight.getModuleName());
				}
				
				if(isFound){
					webModuleRight.setRight(GrpWebModuleRightsDesc.ACCESS_FULL);
				}else{
					webModuleRight.setRight(GrpWebModuleRightsDesc.ACCESS_NONE);
				}
			}
			webModuleRightsList.validate(true);
			group.getWebModuleRightsList().validate(true);
			group.validate(true);
			FocList listOfGroups = FocGroup.getList(FocList.LOAD_IF_NEEDED);
			listOfGroups.validate(true);
		}
	}
	
	protected void setUserOnFullScreenMode(FocUser user) {
		FocUserHistoryList historyList = (FocUserHistoryList) FocUserHistoryDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
		FocUserHistory focUserHistory = (FocUserHistory) historyList.findHistory(user);
		if(focUserHistory == null){
			focUserHistory = (FocUserHistory) historyList.newEmptyItem();
			focUserHistory.setUser(user);
			focUserHistory.setCompany(getApplicationAdaptor().getCompany());
			focUserHistory.setFullscreen(FocUserHistoryConst.MODE_FULLSCREEN);
			focUserHistory.validate(true);
		}else{
			historyList.saveFullscreenSettings(focUserHistory, user, FocUserHistory.MODE_FULLSCREEN);
		}
	}
}
