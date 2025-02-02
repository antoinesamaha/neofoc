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
package com.foc.business.workflow.implementation;

import java.sql.Date;

import com.foc.business.adrBook.AdrBookParty;
import com.foc.business.company.Company;
import com.foc.business.company.CompanyDesc;
import com.foc.business.status.IStatusHolder;
import com.foc.business.status.StatusHolder;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.map.WFSignature;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.list.FocList;

public abstract class FocWorkflowObject extends FocObject implements IWorkflow, IStatusHolder {

  private StatusHolder     statusHolder     = null;
  private Workflow         workflow         = null;

	public FocWorkflowObject(FocConstructor constr) {
		super(constr);
		if(isWorkflowActivatedInFocDesc()){
			workflow = new Workflow(this);
		}
	}

  public void dispose(){
    super.dispose();
    if(statusHolder != null){
      statusHolder.dispose();
      statusHolder = null;
    }
    if(workflow != null){
      workflow.dispose();
      workflow = null;
    }
  }
  
  public boolean hasAdrBookParty() {
  	return false;
  }
  
  public boolean isWorkflowActivatedInFocDesc(){
  	return workflow_IsWorkflowSubject();
  }
  
  public WFSite getSite(){
  	WFSite site = null;
  	if(workflow != null){
  		site = workflow.getSite_1();
  	}
  	return site;
  }

  public void setSite(WFSite site){
  	if(workflow != null){
  		workflow.setSite_1(site);
  	}
  }
  
  //----------------------------------
  //IStatusHolder
  //----------------------------------
  
  @Override
  public StatusHolder getStatusHolder() {
    if(statusHolder == null && (isWorkflowActivatedInFocDesc())){
      statusHolder = new StatusHolder(this);
    }
    return statusHolder;
  }

  @Override
  public boolean allowSettingStatusTo(int newStatus) {
    return true;
  }

  @Override
  public void afterSettingStatusTo(int newStatus) {
  }
  
  //----------------------------------
  //IWorkflow
  //----------------------------------
  
  @Override
  public Workflow iWorkflow_getWorkflow() {
    return workflow;
  }

  @Override
  public WFSite iWorkflow_getComputedSite() {
    return WFTransactionConfigDesc.getDefaultArea_ForTransaction(this);
  }

  @Override
  public String iWorkflow_getCode() {
    return code_getCode();
  }

  @Override
  public Date iWorkflow_getDate() {
    return getDate();
  }

  @Override
  public String iWorkflow_getDescription() {
    return "";
  }

  @Override
  public AdrBookParty iWorkflow_getAdrBookParty() {
    return null;
  }

  @Override
  public FPanel iWorkflow_newDetailsPanel() {
    return newDetailsPanel(FocObject.DEFAULT_VIEW_ID);
  }

  @Override
  public boolean iWorkflow_allowSignature(WFSignature signature) {
    return true;
  }
  
  public void setSiteToAnyValueIfEmpty() {
		if(getSite() == null) {
			FocList companyList = CompanyDesc.getInstance().getFocList();
			if(companyList != null) {
				companyList.loadIfNotLoadedFromDB();
				if(companyList.size() > 0) {
					Company company = (Company) companyList.getFocObject(0);
					WFSite  site    = company != null ? company.getAnySite() : null;
					if(site != null){
						setSite(site);
					}
				}
			}
		}
  }

  public void setCompanyAndSiteToAnyValueIfEmpty() {
  	Company company = getCompany();
  	if(company == null || getSite() == null) {
  		if(company == null) {
				FocList companyList = CompanyDesc.getInstance().getFocList();
				if(companyList != null) {
					companyList.loadIfNotLoadedFromDB();
					if(companyList.size() > 0) {
						company = (Company) companyList.getFocObject(0);
						setCompany(company);
					}
				}
  		}
  		
  		if (company != null && getSite() == null) {
				WFSite site = company != null ? company.getAnySite() : null;
				if(site != null){
					setSite(site);
				}
  		}
			
  	}
  }
}
