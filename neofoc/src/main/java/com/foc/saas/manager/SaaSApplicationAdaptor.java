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
import java.util.HashMap;
import java.util.Iterator;

import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.business.company.Company;
import com.foc.business.company.CompanyDesc;
import com.foc.business.config.BusinessConfig;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.business.workflow.WFTitle;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.business.workflow.rights.RightLevel;
import com.foc.business.workflow.rights.RightLevelDesc;
import com.foc.business.workflow.rights.UserTransactionRight;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.util.Utils;

public abstract class SaaSApplicationAdaptor {

	public abstract void adaptTitleRightsOnTransactions();
	
	private HashMap<Integer, SaaSRoleAdaptor> roleAdaptorMap = null;
	private boolean applicationAdapted = false;
	
	public final static String SUPPLIER_GUEST_GROUP_NAME = "Supplier Guest Group";
	
	public SaaSApplicationAdaptor(){
	}
	
	public void dispose(){
	}
	
  public void putRoleAdaptor(SaaSRoleAdaptor adaptor){
  	if(roleAdaptorMap == null) roleAdaptorMap = new HashMap<Integer, SaaSRoleAdaptor>();
  	roleAdaptorMap.put(adaptor.getID(), adaptor);
  }
  
  public SaaSRoleAdaptor getRoleAdaptor(int roleId){
  	SaaSRoleAdaptor adaptor = null;
  	if(roleAdaptorMap != null) adaptor = roleAdaptorMap.get(roleId);
  	return adaptor;
  }

  public void adaptApplication(boolean forceAdapt){
  	if(!isApplicationAdapted() || forceAdapt){
  		
//  		BusinessConfig.getInstance().setAddressBookParty121(true);
//  		BusinessConfig.getInstance().setContactInPartyMandatory(true);
  		FocList companiesList = CompanyDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
  		if(companiesList != null){
  			for(int i=0; i<companiesList.size(); i++){
  				Company company = (Company) companiesList.getFocObject(i);
  				if(company != null){
	  				BusinessConfig businessConfig = BusinessConfig.getOrCreateForCompany(company);
	  				if(businessConfig != null){
	  					businessConfig.setAddressBookParty121(true);
	  					businessConfig.setContactInPartyMandatory(true);
	  				}
  				}
  			}
  		}
  		
  		if(roleAdaptorMap != null){
  			Iterator<SaaSRoleAdaptor> iter = roleAdaptorMap.values().iterator();
  			while(iter != null && iter.hasNext()){
  				SaaSRoleAdaptor adaptor = iter.next();
  				adaptor.adaptGroup();
  			}
  		}
  		
  		adaptTitleRightsOnTransactions();
  	}
  }
  
	public void adaptUserRights(){
  	FocList userList = FocUserDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
  	for(int i=0; i<userList.size();i++){
  		FocUser user = (FocUser) userList.getFocObject(i);
  		adaptUserRights(user);
  		
  	}
  }

  private boolean isSaaS(){
  	return SaaSConfig.getInstance().getApplicationType() != SaaSConfigDesc.APPLICATION_TYPE_NONE; 
  }
  
  public void adaptUserRights(FocUser user){
		if(isSaaS() && user != null && user.getSaasApplicationRole() != FocUserDesc.APPLICATION_ROLE_NONE){
			adaptApplication(false);
			SaaSRoleAdaptor roleAdaptor = SaaSRoleAdaptor.getAppRoleAdaptor(user);
	  	roleAdaptor.adaptUser(user);
		}
  }
  
	public Company getCompany(){
		Company company = null;
		FocList companyList = CompanyDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
		if(companyList != null){
			company = (Company) companyList.getAnyItem();
		}
		return company;
	}
	
	public WFSite getSite(){
		WFSite site = null;
		Company company = getCompany();
		if(company != null){
			//20160107
			FocList list = WFSiteDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
//			FocList list = company.getSiteList();
			if(list != null){
		    Iterator iter = list.focObjectIterator();
		    while(iter != null && iter.hasNext()){
		    	WFSite wfSite = (WFSite) iter.next();
		    	if(FocObject.equal(wfSite.getCompany(), company)){
		    		site = wfSite;
		    		break;
		    	}
		    }
			}
			//---------
		}
		return site;
	}
	
	public WFTitle getTitle(String titleName, boolean create){
		WFTitle title = null;
		title = WFTitleDesc.getInstance().findWFTitleAndCreateIfNotExist(titleName, create);
		return title;
	}
	
	public RightLevel getRightLevel(String rightLevelName, boolean create){
		RightLevel rightLevel = RightLevelDesc.getRightLevelByName(rightLevelName);
		if(create && rightLevel == null){
			rightLevel = (RightLevel) RightLevelDesc.getInstance().getFocList().newEmptyItem();
			rightLevel.setName(rightLevelName);
			rightLevel.validate(true);
		}
		return rightLevel;
	}

	protected ArrayList<UserTransactionRight> newUSerRightsToDeleteArray(WFTitle title){
		ArrayList<UserTransactionRight> userRightsToDelete = new ArrayList<UserTransactionRight>();
		
		UserTransactionRight foundUserTransactionRight = null;
		for(int i=0; i<getSite().getUserTransactionRightsList().size() && foundUserTransactionRight == null; i++){
			UserTransactionRight transactionRight = (UserTransactionRight) getSite().getUserTransactionRightsList().getFocObject(i);
			if(transactionRight.getTitle().equalsRef(title)){
				userRightsToDelete.add(transactionRight);
			}
		}
		
		return userRightsToDelete;
	}
	
	protected void deleteRemainingUserRights(ArrayList<UserTransactionRight> userRightsToDelete){
		if(userRightsToDelete != null){
			for(int i=0; i<userRightsToDelete.size(); i++){
				userRightsToDelete.get(i).delete();
			}
		}		
	}

	/**
	 * Set Right Level For TransactionType and Title
	 */
	public UserTransactionRight adjustRightLevelForTransactionAndTitle(WFTitle title, String transactionDBTitle, RightLevel rightLevel){
		WFSite site = getSite();
			
		UserTransactionRight foundUserTransactionRight = null;
		for(int i=0; i<site.getUserTransactionRightsList().size() && foundUserTransactionRight == null; i++){
			UserTransactionRight transactionRight = (UserTransactionRight) site.getUserTransactionRightsList().getFocObject(i);
			if(FocObject.equal(title, transactionRight.getTitle()) && Utils.isEqual_String(transactionDBTitle, transactionRight.getTransactionDBTitle())){
				foundUserTransactionRight = transactionRight;
			}
		}
		
		if(foundUserTransactionRight == null){
			foundUserTransactionRight = (UserTransactionRight) site.getUserTransactionRightsList().newEmptyItem();
			foundUserTransactionRight.setTitle(title);
			foundUserTransactionRight.setTransactionDBTitle(transactionDBTitle);
			foundUserTransactionRight.setWFRightsLevel(rightLevel);
			site.getUserTransactionRightsList().add(foundUserTransactionRight);
		}else{
			foundUserTransactionRight.setWFRightsLevel(rightLevel);
		}
		foundUserTransactionRight.validate(true);
		
		return foundUserTransactionRight;
	}
  
  
  

	public boolean isApplicationAdapted() {
		return applicationAdapted;
	}

	public void setApplicationAdapted(boolean applicationAdapted) {
		this.applicationAdapted = applicationAdapted;
	}
}
