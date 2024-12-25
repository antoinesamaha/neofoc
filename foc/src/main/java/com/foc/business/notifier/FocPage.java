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
package com.foc.business.notifier;

import java.sql.Date;

import com.foc.Globals;
import com.foc.admin.FocUser;
import com.foc.business.company.Company;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFTitle;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.shared.xmlView.XMLViewKey;

@SuppressWarnings("serial")
public class FocPage extends FocObject{
 
	public FocPage(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
	
	public String getSerialisation() {
    return getPropertyString(FocPageDesc.FLD_SERIALISATION);
  }
  
  public void setSerialisation(String serialisation) {
    setPropertyString(FocPageDesc.FLD_SERIALISATION, serialisation);
  }
  
  public String getTableName() {
    return getPropertyString(FocPageDesc.FLD_TABLE_NAME);
  }
  
  public void setTableName(String tableName) {
    setPropertyString(FocPageDesc.FLD_TABLE_NAME, tableName);
  }
  
  public int getTableRefernce() {
    return getPropertyInteger(FocPageDesc.FLD_TABLE_REF);
  }
  
  public void setTableRefernce(int tableRef) {
    setPropertyInteger(FocPageDesc.FLD_TABLE_REF, tableRef);
  }
  
  public String getViewContext() {
    return getPropertyString(FocPageDesc.FLD_VIEW_CONTEXT);
  }
  
  public void setViewContext(String viewContext) {
    setPropertyString(FocPageDesc.FLD_VIEW_CONTEXT, viewContext);
  }
  
  public int getViewType() {
    return getPropertyInteger(FocPageDesc.FLD_VIEW_TYPE);
  }
  
  public void setViewType(int viewType) {
    setPropertyInteger(FocPageDesc.FLD_VIEW_TYPE, viewType);
  }
  
  public String getUserView() {
    return getPropertyString(FocPageDesc.FLD_USER_VIEW);
  }
  
  public void setUserView(String userView) {
    setPropertyString(FocPageDesc.FLD_USER_VIEW, userView);
  }
  
  public String getViewStorageName() {
    return getPropertyString(FocPageDesc.FLD_VIEW_STORAGE_NAME);
  }
  
  public void setViewStorageName(String storageName) {
    setPropertyString(FocPageDesc.FLD_VIEW_STORAGE_NAME, storageName);
  }  
  
  public String getFocObjectClassName() {
    return getPropertyString(FocPageDesc.FLD_FOC_OBJ_CLASSNAME);
  }
  
  public void setFocObjectClassName(String storageName) {
    setPropertyString(FocPageDesc.FLD_FOC_OBJ_CLASSNAME, storageName);
  }
  
  public WFTitle getTitle(){
		return (WFTitle) getPropertyObject(FocPageDesc.FLD_TITLE);
	}

	public void setTitle(WFTitle title){
		setPropertyObject(FocPageDesc.FLD_TITLE, title);
	}

  public FocUser getUser(){
    return (FocUser) getPropertyObject(FocPageDesc.FLD_USER);
  }

  public void setUser(FocUser user){
    setPropertyObject(FocPageDesc.FLD_USER, user);
  }
  
  public Company getCompany() {
    return (Company) getPropertyObject(FocPageDesc.FLD_COMPANY);
  }
  
  public void setCompany(Company company) {
    setPropertyObject(FocPageDesc.FLD_COMPANY, company);
  }
  
  public WFSite getSite(){
		return (WFSite) getPropertyObject(FocPageDesc.FLD_SITE);
	}
  
  public void setSite(WFSite site) {
    setPropertyObject(FocPageDesc.FLD_SITE, site);
  }
  
  public Date getCreationDate(){
  	return getPropertyDate(FocPageDesc.FLD_CREATION_DATE);
  }
  
  public void setCreationDate(Date creationDate){
  	setPropertyDate(FocPageDesc.FLD_CREATION_DATE, creationDate);
  }
  
  public FocUser getCreatorUser(){
  	return (FocUser) getPropertyObject(FocPageDesc.FLD_CREATOR_USER);
  }
  
  public void setCreatorUser(FocUser focUser){
  	setPropertyObject(FocPageDesc.FLD_CREATOR_USER, focUser);
  }
  
  public void fill(FocObject focObj, XMLViewKey xmlViewKey, String serialisation){
		String  tableName = focObj != null ? focObj.getThisFocDesc().getStorageName() : "";
		
		int     ref       = focObj != null ? focObj.getReference().getInteger() : 0;
		FocUser user      = Globals.getApp().getUser_ForThisSession();
		Company company   = Globals.getApp().getCurrentCompany();
		WFTitle title     = Globals.getApp().getCurrentTitle();
		WFSite  site      = Globals.getApp().getCurrentSite();
		
		if(xmlViewKey != null){
  		int    type        = xmlViewKey.getType();
  		String context     = xmlViewKey.getContext() != null ? xmlViewKey.getContext() : XMLViewKey.CONTEXT_DEFAULT;
  		String userView    = xmlViewKey.getUserView() != null ? xmlViewKey.getUserView() : XMLViewKey.VIEW_DEFAULT;
  		String storageName = xmlViewKey.getStorageName();
  		
  		setViewType(type);
  		setViewContext(context);
  		setUserView(userView);
  		setViewStorageName(storageName);
  		setFocObjectClassName(focObj != null ? focObj.getClass().getName() : "");
  		setTableName(tableName);
  		setTableRefernce(ref);
  		setTitle(title);
  		setCompany(company);
  		setSite(site);
  		setUser(user);
  		setSerialisation(serialisation);
  	}
  }
}
