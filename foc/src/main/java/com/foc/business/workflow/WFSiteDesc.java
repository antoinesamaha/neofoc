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
package com.foc.business.workflow;

import java.util.ArrayList;

import com.foc.business.company.Company;
import com.foc.business.company.CompanyDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FCompanyField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FStringField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class WFSiteDesc extends FocDesc {
	public static final int FLD_NAME                         = FField.FLD_NAME;
	public static final int FLD_DESCRIPTION                  = FField.FLD_DESCRIPTION;
	public static final int FLD_TRANSACTION_PREFIX           = 1;
	public static final int FLD_ADDRESS                      = 2;
	public static final int FLD_OPERATOR_LIST                = 10;
	public static final int FLD_USER_TRANSACTION_RIGHTS_LIST = 11;
	public static final int FLD_SUPER_USER_LIST              = 12;
  
	public static final int FLD_MAX_FLD_ID                   = 100;
	
	public static final int LEN_TRANSACTION_PREFIX           = 4;
	
	public static final String DB_TABLE_NAME = "WF_AREA";
	
	public static final String FNAME_ADDRESS = "ADDRESS";
	public static final String FNAME_TRANSACTION_PREFIX = "TRANSACTION_PREFIX";
	
	public WFSiteDesc(){
		super(WFSite.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
		setGuiBrowsePanelClass(WFSiteGuiTreePanel.class);
		setGuiDetailsPanelClass(WFSiteGuiDetailsPanel.class);
		setWithObjectTree();
		addReferenceField();
		
		FField fld = addNameField();
		fld.setSize(60);
		addDescriptionField();
		
    FCompanyField compField = new FCompanyField(false, true);
    compField.setListFieldInMaster(CompanyDesc.getInstance(), CompanyDesc.FLD_SITE_LIST, this, null);
    addField(compField);

    FStringField cFld = new FStringField(FNAME_TRANSACTION_PREFIX, "Transaction Prefix", FLD_TRANSACTION_PREFIX, false, LEN_TRANSACTION_PREFIX);
    addField(cFld);
		
		FBlobStringField fField = new FBlobStringField(FNAME_ADDRESS, "Address", FLD_ADDRESS, false, 4, 30);
    addField(fField);

		//FBoolField bFld = new FBoolField("MAIN_OFFICE", "Main Office", FLD_MAIN_OFFICE, false);
		//addField(bFld);
  }
	
	public static FObjectField newSiteField(int fieldID, boolean key){
		return newSiteField("WF_AREA", fieldID, key);
	}
	
	public static FObjectField newSiteField(String fldName, int fieldID, boolean key){
		FObjectField objFld = new FObjectField(fldName, "Workflow Site", fieldID, WFSiteDesc.getInstance());
	  objFld.setComboBoxCellEditor(WFSiteDesc.FLD_NAME);
	  return objFld;
	}
	
  public static FObjectField newSiteField(FocDesc focDesc, String dbName, int fldID, int listFieldID){
  	return newSiteField(focDesc, dbName, fldID, listFieldID, true);
  }
  
  public static FObjectField newSiteField(FocDesc focDesc, String dbName, int fldID, int listFieldID, boolean key){
  	FObjectField objFld = null;
  	if(listFieldID > 0){
  		objFld = new FObjectField(dbName, "Site", fldID, key, WFSiteDesc.getInstance(), dbName+"_", focDesc, listFieldID);
  	}else{
  		objFld = new FObjectField(dbName, "Site", fldID, key, WFSiteDesc.getInstance(), dbName+"_");
  	}
	  objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
	  objFld.setComboBoxCellEditor(WFSiteDesc.FLD_NAME);
	  //20160104-The Application.getCurrentSite() will return the one from the UserSession filled from the Company Slave list of Sites. 
	  //Which means it is a different instance of object site.
	  //20160107-We changed our MINd :) we will make the Company slave tables get the full site list but with a wrapper filter.
	  objFld.setSelectionList(WFSiteDesc.getList(FocList.NONE));
//	  objFld.setWithList(false);
	  //20160104-End
	  
	  objFld.setLockValueAfterCreation(true);
	  objFld.setMandatory(true);
	  return objFld;
  }
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_NAME);
      list.setListOrder(order);
    }
    return list;
  }
	/*
	private static WFArea mainOfficeArea = null;
	public static WFArea getMainOfficeArea(){
		if(mainOfficeArea == null || !mainOfficeArea.isMainOffice()){
			mainOfficeArea = null;
			FocList list = getList(FocList.FORCE_RELOAD);
			if(list != null){
				for(int i=0; i<list.size() && mainOfficeArea == null; i++){
					WFArea area = (WFArea) list.getFocObject(i);
					if(area.isMainOffice()){
						mainOfficeArea = area;
					}
				}
			}
		}
		return mainOfficeArea;
	}
	*/
	
	public static WFSite getArea_ForName(String name){
	  WFSite site = null;
	  FocList allSiteList = WFSiteDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
	  site = (WFSite) (allSiteList != null ? allSiteList.searchByPropertyStringValue(WFSiteDesc.FLD_NAME, name) : null);
		return site;
	}
	
  public static WFSite getSiteByName(Company company, String siteName){
  	WFSite foundSite = null; 
  	FocList list = getList(FocList.LOAD_IF_NEEDED);
  	for(int i=0; i<list.size(); i++){
  		WFSite site = (WFSite) list.getFocObject(i);
  		if(site.getCompany().equalsRef(company) && site.getName().equals(siteName)){
  			foundSite = site;
  		}
  	}
  	return foundSite;
  }
  
  public static ArrayList<WFSite> getSiteListForCompany(Company company){
  	ArrayList<WFSite> siteList = new ArrayList<WFSite>();
  	if(company != null) {
	  	FocList list = getList(FocList.LOAD_IF_NEEDED);
	  	for(int i=0; i<list.size(); i++){
	  		WFSite site = (WFSite) list.getFocObject(i);
	  		if(site.getCompany().equalsRef(company)) siteList.add(site);
	  	}
  	}
  	return siteList;
  }
  
  public static WFSite getSiteByName(String name){
  	FocList list = getList(FocList.LOAD_IF_NEEDED);
  	return (WFSite) list.searchByPropertyStringValue(FLD_NAME, name);
  }
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, WFSiteDesc.class);  
  }
}
