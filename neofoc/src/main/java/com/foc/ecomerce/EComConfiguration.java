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
package com.foc.ecomerce;

import com.foc.Globals;
import com.foc.admin.FocGroup;
import com.foc.business.company.Company;
import com.foc.business.company.CompanyDesc;
import com.foc.business.config.BusinessConfig;
import com.foc.business.config.BusinessConfigDesc;
import com.foc.business.notifier.FocNotificationEmailTemplate;
import com.foc.business.workflow.WFSite;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class EComConfiguration extends FocObject {
  public EComConfiguration(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public FocNotificationEmailTemplate getEmailTemplate_UserCreation() {
    return (FocNotificationEmailTemplate) getPropertyObject(EComConfigurationDesc.FLD_TEMPLATE_USER_CREATION);
  }

  public void setEmailTemplate_UserCreation(FocNotificationEmailTemplate template) {
    setPropertyObject(EComConfigurationDesc.FLD_TEMPLATE_USER_CREATION, template);
  }
  
  public FocNotificationEmailTemplate getEmailTemplate_PasswordReset() {
    return (FocNotificationEmailTemplate) getPropertyObject(EComConfigurationDesc.FLD_TEMPLATE_PASSWORD_RESET);
  }

  public void setEmailTemplate_PasswordReset(FocNotificationEmailTemplate template) {
    setPropertyObject(EComConfigurationDesc.FLD_TEMPLATE_PASSWORD_RESET, template);
  }

  public FocNotificationEmailTemplate getTemplateBeforeActivation() {
    return (FocNotificationEmailTemplate) getPropertyObject(EComConfigurationDesc.FLD_TEMPLATE_BEFORE_ACTIVATION);
  }

  public void setTemplateBeforeActivation(FocNotificationEmailTemplate template) {
    setPropertyObject(EComConfigurationDesc.FLD_TEMPLATE_BEFORE_ACTIVATION, template);
  }
  
  public FocGroup getGuestUserGroup() {
    return (FocGroup) getPropertyObject(EComConfigurationDesc.FLD_GUEST_USER_GROUP);
  }
  
  public FocNotificationEmailTemplate getTemplateAfterActivation() {
    return (FocNotificationEmailTemplate) getPropertyObject(EComConfigurationDesc.FLD_TEMPLATE_AFTER_ACTIVATION);
  }

  public void setTemplateAfterActivation(FocNotificationEmailTemplate template) {
    setPropertyObject(EComConfigurationDesc.FLD_TEMPLATE_AFTER_ACTIVATION, template);
  }
        
  public WFSite getGuestUserSite(){
  	return (WFSite) getPropertyObject(EComConfigurationDesc.FLD_GUEST_USER_SITE);
  }
  
	public static EComConfiguration getOrCreateForCompany(Company company){
		EComConfiguration foundConfig = null; 
				
    FocList basicsConfigList = EComConfigurationDesc.getList(FocList.LOAD_IF_NEEDED);
    for(int i=0; i<basicsConfigList.size() && foundConfig == null; i++){
    	EComConfiguration cfg = (EComConfiguration) basicsConfigList.getFocObject(i);
    	if(cfg != null && cfg.isForCompany(company)){
    		foundConfig = cfg;
    	}
    }
    if(foundConfig == null){
    	foundConfig = (EComConfiguration) basicsConfigList.newEmptyItem();
    	foundConfig.setCompany(company);
    	basicsConfigList.add(foundConfig);
    }
    
    return foundConfig;
	}
	
  private static EComConfiguration generalConfig = null;
  public synchronized static EComConfiguration getInstance(){
  	if(generalConfig == null || !generalConfig.isForCurrentCompany()){
  		generalConfig = getOrCreateForCompany(Globals.getApp().getCurrentCompany());
  	}
  	return generalConfig;
  }
}



















