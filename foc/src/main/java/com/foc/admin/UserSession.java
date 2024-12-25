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

import java.io.Serializable;
import java.util.HashMap;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.business.company.Company;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFTitle;
import com.foc.dataSource.store.DataStore;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class UserSession implements Serializable {
  private FocUser user             = null;
  private int     multiCompanyMode = FocUserDesc.COMPANY_MODE_SEE_ONLY_CURRENT;
  private Company company          = null;
  private WFTitle title            = null;
  private WFSite  site             = null;
  private boolean linkPageOnly     = false;
  private DataStore currentDataStore = null;
  private boolean simulation       = false;
  private HashMap<String, Object> sessionParameters = null;
  
	public UserSession(){
  }

  public void dispose(){
//  	Globals.logString("DEBUG_SESSION_NOT_VALID UserSession.dispose()");
    clear();
    if(currentDataStore != null){
    	currentDataStore.dispose();
    	currentDataStore = null;
    }
  }
  
  public void clear(){
//  	Globals.logString("DEBUG_SESSION_NOT_VALID UserSession.clear() maybe after dispose or after close session");  	
  	user = null;
  }
  
  public Object getSessionParameter(String key){
  	Object obj = null;
  	if(sessionParameters != null){
  		obj = sessionParameters.get(key);
  	}
  	return obj;
  }
  
  public void putSessionParameter(String key, Object obj){
  	if(sessionParameters == null){
  		sessionParameters = new HashMap<String, Object>();
  	}
  	if(sessionParameters != null){
  		sessionParameters.put(key, obj);
  	}
  }
  
  public void clearSessionParameters() {
  	if(sessionParameters != null){
  		sessionParameters.clear();
  	}
	}

  public static Object getParameter(String key){
  	Object obj = null;
  	
  	UserSession userSession = getInstanceForThread();
  	if(userSession != null){
  		obj = userSession.getSessionParameter(key);
  	}
  	
  	return obj;
  }
  
  public static void putParameter(String key, Object obj){
  	UserSession userSession = getInstanceForThread();
  	if(userSession != null){
  		userSession.putSessionParameter(key, obj);
  	}
  }
  
  public FocUser getUser() {
    return user;
  }

  public void setUser(FocUser user) {
  	if(user == null){
  		Globals.logString("DEBUG_SESSION_NOT_VALID: UserSession.setUser( NULL ) SETTING TO NULL!!!");
  	}
    this.user = user;
    if(user != null){
      setMultiCompanyMode(FocUserDesc.COMPANY_MODE_SEE_ONLY_CURRENT/*user.getMultiCompanyMode()*/);
      copyCredentialsFromUser();
    }
  }

  public void copyCompanyFromUser(){
  	if(user != null){
  		//USERREFACTOR
  		user.copyCompanySelectionFromUser(this);
  		/*
  		user.re_buildCompanyList();
//	    user.dispose_CompanyList();
  		//We copy the company only if we still have the rights for it
	    if(user.getCompanyList() != null){
		    Company previousCompany = user.getCurrentCompany();	    	
	    	if(previousCompany != null && user.getCompanyList().searchByReference(previousCompany.getReference().getInteger()) != null){
	    		company = previousCompany;	
	    	}else{
	    		company = (Company) user.getCompanyList().getAnyItem();
	    		user.setCurrentCompany(company);
	    	}
	    }
//	    user.dispose_SitesList();
//	    user.dispose_TitlesList();
	    user.re_buildSitesList();
	    user.re_buildTitlesList();

	    //We keep the sites as it only if we still have the rights for them otherwize we set to any site we have rights on
	    //or null if no sites with rights at all.
	    if(user.getSitesList() != null){
		    WFSite previousSite = user.getCurrentSite();	    	
	    	if(previousSite != null && user.getSitesList().searchByReference(previousSite.getReference().getInteger()) == null){
	    		user.setCurrentSite(null);
	    	}
	    	if(user.getCurrentSite() == null){
	    		user.setCurrentSite((WFSite) user.getSitesList().getAnyItem());
	    	}
	    }else{
	    	user.setCurrentSite(null);
	    }
	    site = user.getCurrentSite();
	    
	    //We keep the title as it only if we still have the rights for them otherwize we set to any title we have rights on
	    //or null if no title with rights at all.
	    if(user.getTitlesList() != null){
		    WFTitle previousTitle = user.getCurrentTitle();	    	
	    	if(previousTitle != null && user.getTitlesList().searchByReference(previousTitle.getReference().getInteger()) == null){
	    		user.setCurrentTitle(null);	
	    	}
	    	if(user.getCurrentTitle() == null){
	    		user.setCurrentTitle((WFTitle) user.getTitlesList().getAnyItem());
	    	}
	    }else{
	    	user.setCurrentTitle(null);
	    }
	    title = user.getCurrentTitle();
	    setSimulation(user.isSimulationActive());
  		 */
  	}
  }

  public void copyCredentialsFromUser(){
  	if(user != null){
  		copyCompanyFromUser();
  		//Done in the company copy
//	    site = user.getCurrentSite();
//	    title = user.getCurrentTitle(); 
  	}
  }
  
  public Company getCompany() {
    return company;
  }
  
  public void setCompany(Company company) {
    this.company = company;
  }

  public int getMultiCompanyMode() {
    return multiCompanyMode;
  }

  public void setMultiCompanyMode(int multiCompanyMode) {
    this.multiCompanyMode = multiCompanyMode;
  }

  public WFSite getSite() {
    return site;
  }
  
  public void setSite(WFSite site) {
    this.site = site;
  }

  public WFTitle getTitle() {
    return title;
  }
  
  public void setTitle(WFTitle title) {
    this.title = title;
  }

  public String getLanguage() {
		return ConfigInfo.getLanguage();
	}

  //--------------------------------
  // STATIC THREAD LOCAL
  //--------------------------------
//  private static ThreadLocal<UserSession> threadLocalUserSession = new ThreadLocal<UserSession>();
//  
//  public static void setInstanceForThread(UserSession userSession){
//    threadLocalUserSession.set(userSession);
//  }
  
  public static UserSession getInstanceForThread(){
    UserSession userSession = null;
    if(Globals.getIFocNotification() != null){
      userSession = Globals.getIFocNotification().getUserSession();
    }else if(!Globals.getApp().isWebServer()){
    	userSession = Globals.getApp().getUserSession_Swing();
    }
    return userSession;
  }
  //--------------------------------

	public boolean isLinkPageOnly() {
		return linkPageOnly;
	}

	public void setLinkPageOnly(boolean linkPageOnly) {
		this.linkPageOnly = linkPageOnly;
	}
	
	public FocList getCustomFocList_ForMobile(String key){
		FocList focList = null;
		if(currentDataStore != null){
			focList = (FocList) currentDataStore.getData(key); 
		}
		return focList;
	}
	
	public void setCustomFocList_ForMobile(FocList focList, String key){
		if(currentDataStore == null){
			currentDataStore = new DataStore();
		}
		currentDataStore.putData(key, focList);
	}

	public boolean isSimulation() {
		return simulation;
	}

	public void setSimulation(boolean simulation) {
		this.simulation = simulation;
	}
}
