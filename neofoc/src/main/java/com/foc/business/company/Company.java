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

import java.awt.Image;
import java.awt.image.BufferedImage;

import com.foc.business.adrBook.AdrBookParty;
import com.foc.business.adrBook.AdrBookPartyDesc;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.dataWrapper.FocListWrapper;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.desc.field.FCompanyField;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.property.FImageProperty;
import com.foc.property.FProperty;

@SuppressWarnings("serial")
public class Company extends FocObject {

	private FocObject supplierRelatedToThisCompany = null;
	
	public Company(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
	
	public void dispose(){
		super.dispose();
		supplierRelatedToThisCompany = null;
	}

  @Override
	public FProperty getFocProperty(int fieldID) {
		FProperty prop = super.getFocProperty(fieldID);
		if(fieldID == CompanyDesc.FLD_OFFICIAL_ADDRESS){
			FocObject focObj = getAdrBookParty();
			prop = focObj != null ? focObj.getFocProperty(AdrBookPartyDesc.FLD_INVOICE_ADDRESS) : null;
		}
		return prop;
  }

  public void copyNameFromAdressBook() {
  	if(getAdrBookParty() != null) setName(getAdrBookParty().getCode());
  }
  
  public void copyDescriptionFromAdressBook() {
  	if(getAdrBookParty() != null) setDescription(getAdrBookParty().getName());
  }
  
	public String getName(){
  	return getPropertyString(FField.FLD_NAME);
  }
  
  public String getDescription(){
  	return getPropertyString(CompanyDesc.FLD_DESCRIPTION);
  }
  
  public FocObject getVATRule(){
  	return getPropertyObject(CompanyDesc.FLD_VAT_RULE);
  }

  public AdrBookParty getAdrBookParty(){
  	return (AdrBookParty) getPropertyObject(CompanyDesc.FLD_ADR_BOOK_PARTY);
  }
  
  public void setAdrBookParty(AdrBookParty adrBookParty){
  	setPropertyObject(CompanyDesc.FLD_ADR_BOOK_PARTY, adrBookParty);
  }

  public BufferedImage getLogo(){
  	FImageProperty imageProp = (FImageProperty) getFocProperty(CompanyDesc.FLD_LOGO_IMAGE);
  	return imageProp != null ? imageProp.getImageValue() : null;
  }

  //20160107
  /*
  public FocList getSiteList(){
  	FocList list = getPropertyList(CompanyDesc.FLD_SITE_LIST);
  	if(list != null){
  		list.setDirectImpactOnDatabase(true);
  		list.setDirectlyEditable(false);
  	}
  	return list;
  }
  */

  //USERRIGHTS
  /*
  public FocList getUserRightsList(){
  	FocList list = getPropertyList(CompanyDesc.FLD_USER_RIGHTS_LIST);
  	if(list != null){
  		list.setDirectImpactOnDatabase(true);
  		list.setDirectlyEditable(false);
  	}
  	return list;
  }

  public UserCompanyRights getUserRightsObject(FocUser user){
		UserCompanyRights foundUsercompRights = null;
  	FocList           list                = getUserRightsList();
  	
  	if(list != null){
  		list.loadIfNotLoadedFromDB();
 
  		for(int i=0; i<list.size() && foundUsercompRights == null; i++){
  			UserCompanyRights usercompRights = (UserCompanyRights) list.getFocObject(i);
  			if(usercompRights != null){
  				if(usercompRights.getUser().getName().equals(user.getName())){
  					foundUsercompRights = usercompRights;
  				}
  			}
  		}
  	}
  	return foundUsercompRights;
  }
  
  public int getUserRights(FocUser user){
  	UserCompanyRights foundUsercompRights = getUserRightsObject(user);
  	int userRights = foundUsercompRights != null ? foundUsercompRights.getAccessRight() : UserCompanyRightsDesc.ACCESS_RIGHT_NONE;
  	return userRights;
  }

  public int getUserRights(){
  	return getUserRights(Globals.getApp().getUser());
  }
	*/
  
  public Image getLogoImage(){
  	return getLogo();
  	//return Globals.getIcons().getClientLogoImage();
  }
  
  public String getLeagalAddress(){
  	return getPropertyString(CompanyDesc.FLD_OFFICIAL_ADDRESS);
  }
  
  public void setLeagalAddress(String address){
  	setPropertyString(CompanyDesc.FLD_OFFICIAL_ADDRESS, address);
  }
  
  public FocObject getSupplierRelatedToThisCompany() {
		return supplierRelatedToThisCompany;
	}

	public void setSupplierRelatedToThisCompany(FocObject supplierRelatedToThisCompany) {
		this.supplierRelatedToThisCompany = supplierRelatedToThisCompany;
	}
	
	public WFSite findOrCreateSite(String siteName){
		WFSite site = findSite(siteName);
		if(site == null){
			site = newSite(siteName);
		}
		return site;
	}
	
	public WFSite findSite(String siteName){
		return WFSiteDesc.getSiteByName(this, siteName);
//		WFSite site = null;
//  	FocList list = getSiteList();
//  	if(list != null){
//	  	list.loadIfNotLoadedFromDB();
//	  	site = (WFSite) list.searchByPropertyStringValue(WFSiteDesc.FLD_NAME, siteName);
//  	}
//  	return site;
	}
	
  public WFSite newSite(String siteName){
  	WFSite site = null;
  	//20160107
//  	FocList list = getSiteList();
  	FocList list = WFSiteDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
  	//--------
  	if(list != null){
	  	site = (WFSite) list.newEmptyItem();
	  	site.setCompany(this);//20160107
			site.setName(siteName);
			site.validate(true);
			list.validate(true);
  	}
		return site;
  }

  public int getSiteListSize(){
  	int size = 0;
  	FocList list = WFSiteDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
  	if(list != null){
	  	for(int i=0; i<list.size(); i++){
	  		WFSite currSite = (WFSite) list.getFocObject(i);
	  		if(currSite != null && FocObject.equal(currSite.getCompany(), this)){
	  			size++;
	  		}
	  	}
  	}
  	return size;
  }
  
  public WFSite getAnySite(){
  	WFSite site = null;
  	FocList list = WFSiteDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
  	if(list != null){
	  	for(int i=0; i<list.size() && site == null; i++){
	  		WFSite currSite = (WFSite) list.getFocObject(i);
	  		if(currSite != null && FocObject.equal(currSite.getCompany(), this)){
	  			site = currSite;
	  		}
	  	}
  	}
  	return site;
  }
  
  public FocListWrapper newFocListWrapperForCurrentCompany(){
  	FocList allSites = WFSiteDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
		FocListWrapper wrapper = new FocListWrapper(allSites);
		wrapper.addFilterByPropertyValue(FCompanyField.FNAME_COMPANY, this);
		return wrapper;
  }
  
  //USERRIGHTS
  /*
	public UserCompanyRights addUserRights(FocUser user, int rights) {
		UserCompanyRights userCompany = null;
		FocList list = getUserRightsList();
		if(list != null){
			userCompany = (UserCompanyRights) list.searchByPropertyObjectValue(UserCompanyRightsDesc.FLD_USER, user);
			if(userCompany == null){
				userCompany = (UserCompanyRights) list.newEmptyItem();
				userCompany.setUser(user);
			}
			if(userCompany != null){
				userCompany.setAccessRight(rights);
				userCompany.validate(true);
			}
			list.validate(true);
		}
		return userCompany;
	}
	*/
}
