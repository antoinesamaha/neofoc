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

import java.sql.Date;

import com.foc.Globals;
import com.foc.admin.FocUser;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.property.FProperty;

@SuppressWarnings("serial")
public class SaaSConfig extends FocObject {

	private SaaSApplicationAdaptor saasApplicationAdaptor = null;
	
	public SaaSConfig(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
	
  public void dispose(){
  	super.dispose();
  	if(saasApplicationAdaptor != null){
  		saasApplicationAdaptor.dispose();
  		saasApplicationAdaptor = null;
  	}
  }

	public int getPlan(){
  	return getPropertyInteger(SaaSConfigDesc.FLD_PLAN);
  }
  
  public void setPlan(int plan){
  	setPropertyInteger(SaaSConfigDesc.FLD_PLAN, plan);
  }

  public void setPlan(String plan){
  	setPropertyString(SaaSConfigDesc.FLD_PLAN, plan);
  }

  public int getApplicationType(){
  	return getPropertyInteger(SaaSConfigDesc.FLD_APPLICATION_TYPE);
  }
  
  public void setApplicationType(String applicationType){
  	FProperty prop = getFocProperty(SaaSConfigDesc.FLD_APPLICATION_TYPE);
  	if(prop != null){
  		prop.setString(applicationType);
  	}
  }
  
  public void setApplicationType(int applicationType){
  	setPropertyInteger(SaaSConfigDesc.FLD_APPLICATION_TYPE, applicationType);
  }
  
  public void setRenewedUntilDate(Date renewedUntilDate){
    setPropertyDate(SaaSConfigDesc.FLD_RENEWED_UNTIL, renewedUntilDate);
  }

  public Date getRenewedUntilDate(){
    return getPropertyDate(SaaSConfigDesc.FLD_RENEWED_UNTIL);
  }

  public static SaaSConfig getInstance(){
  	return Globals.getApp().getSaaSConfig();
  }
  
  public static SaaSConfig loadInstance(){
  	SaaSConfig appConfig = null;
  	
    FocList appConfigList = SaaSConfigDesc.getList(FocList.LOAD_IF_NEEDED);
    if(appConfigList.size() > 0){
    	appConfig = (SaaSConfig) appConfigList.getFocObject(0);
    }
    
    if(appConfig == null){
    	appConfig = (SaaSConfig) appConfigList.newEmptyItem();
    	appConfigList.add(appConfig);
    	appConfig.validate(true);
    }
  	
  	return appConfig;
  }
  
	public SaaSApplicationAdaptor getSaasApplicationAdaptor() {
		return saasApplicationAdaptor;
	}

	public void setSaasApplicationAdaptor(SaaSApplicationAdaptor saasApplicationAdaptor) {
		this.saasApplicationAdaptor = saasApplicationAdaptor;
	}
	
  public void adaptUserRights(){
  	if(getSaasApplicationAdaptor() != null) getSaasApplicationAdaptor().adaptUserRights();
  }

  public void adaptUserRights(FocUser user){
  	if(getSaasApplicationAdaptor() != null) getSaasApplicationAdaptor().adaptUserRights(user);
  }
  
  public static boolean isFreePlanUser(){
  	SaaSConfig saaSConfig = SaaSConfig.getInstance();
		return saaSConfig != null && saaSConfig.getPlan() == SaaSConfigDesc.PLAN_FREE;
	}
}
