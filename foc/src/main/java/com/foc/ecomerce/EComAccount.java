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

import java.sql.Date;

import com.foc.admin.FocGroup;
import com.foc.admin.FocUser;
import com.foc.business.adrBook.AdrBookParty;
import com.foc.business.adrBook.AdrBookPartyDesc;
import com.foc.business.adrBook.Contact;
import com.foc.business.adrBook.ContactDesc;
import com.foc.business.country.Country;
import com.foc.business.workflow.WFSite;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.util.ASCII;
import com.foc.util.Encryptor;

@SuppressWarnings("serial")
public class EComAccount extends FocObject {

  public EComAccount(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  @Override
  public void dispose(){
  	super.dispose();
  }
  
  public String getActivationLink(){
  	return getPropertyString(EComAccountDesc.FLD_ACTIVATION_LINK);
  }
  
  public void setActivationLink(String actLink){
  	setPropertyString(EComAccountDesc.FLD_ACTIVATION_LINK, actLink);
  }
  
  public void generateActivationLink(){
  	String randomKeyStringForURL = ASCII.generateRandomString(50).trim();
  	if(getFatherSubject() instanceof FocList){
	  	FocList list = (FocList) getFatherSubject();
	  	for(int i=0;i<list.size();i++){
	  		EComAccount focPageLink = (EComAccount) list.getFocObject(i);
	  		if(focPageLink !=null){
	  			String key = focPageLink.getActivationLink().trim();
	  			if(key.equals(randomKeyStringForURL)){
	  				randomKeyStringForURL = ASCII.generateRandomString(50);
	  				i=0;
	  			}
	  		}
	  	}
	  	setActivationLink(randomKeyStringForURL);
  	}
  }
  
  public Date getActivationDate() {
    return getPropertyDate(EComAccountDesc.FLD_ACTIVATION_DATE);
  }
  
  public void setActivationDate(Date date) {
    setPropertyDate(EComAccountDesc.FLD_ACTIVATION_DATE, date);
  }
  
  public Date getCreationDate() {
    return getPropertyDate(EComAccountDesc.FLD_CREATION_DATE);
  }
  
  public void setCreationDate(Date date) {
    setPropertyDate(EComAccountDesc.FLD_CREATION_DATE, date);
  }
  
  public String getPassword(){
    return getPropertyString(EComAccountDesc.FLD_PASSWORD);
  }

  public void setPassword(String password){
  	String newPassword = Encryptor.encrypt_MD5(String.valueOf(password));
  	setPropertyString(EComAccountDesc.FLD_PASSWORD, newPassword);
  }
  
  public String getConfirmedPassword(){
    return getPropertyString(EComAccountDesc.FLD_CONFIRM_PASSWORD);
  }

  public void setConfirmedPassword(String password){
  	String newPassword = Encryptor.encrypt_MD5(String.valueOf(password));
  	setPropertyString(EComAccountDesc.FLD_CONFIRM_PASSWORD, newPassword);
  }

  public String getFirstName(){
    return getPropertyString(EComAccountDesc.FLD_FIRST_NAME);
  }

  public void setFirstName(String name){
    setPropertyString(EComAccountDesc.FLD_FIRST_NAME, name);
  }
  
  public String getLastName(){
    return getPropertyString(EComAccountDesc.FLD_LAST_NAME);
  }

  public void setLastName(String name){
    setPropertyString(EComAccountDesc.FLD_LAST_NAME, name);
  }
  
  public String getPhone(){
    return getPropertyString(EComAccountDesc.FLD_PHONE);
  }

  public void setPhone(String name){
    setPropertyString(EComAccountDesc.FLD_PHONE, name);
  }
 
  public void setEMail(String name){
    setPropertyString(EComAccountDesc.FLD_EMAIL, name);
  }
  
  public String getEMail(){
    return getPropertyString(EComAccountDesc.FLD_EMAIL);
  }

  public String getCurrency(){
    return getPropertyString(EComAccountDesc.FLD_CURRENCY_SYMBOL);
  }

  public void setCurrency(String currency){
    setPropertyString(EComAccountDesc.FLD_CURRENCY_SYMBOL, currency);
  }

  public Country getCountry(){
    return (Country) getPropertyObject(EComAccountDesc.FLD_COUNTRY);
  }

  public void setCountry(Country country){
    setPropertyObject(EComAccountDesc.FLD_COUNTRY, country);
  }

  public String getInvoiceAddress(){
    return getPropertyString(EComAccountDesc.FLD_INVOICE_ADDRESS);
  }

  public void setInvoiceAddress(String adr){
    setPropertyString(EComAccountDesc.FLD_INVOICE_ADDRESS, adr);
  }
  
  public String getCompanyName(){
    return getPropertyString(EComAccountDesc.FLD_COMPANY_NAME);
  }

  public void setCompanyName(String company){
    setPropertyString(EComAccountDesc.FLD_COMPANY_NAME, company);
  }
  
	public FocList getStoneSoukMaterialList(){
		FocList list = getPropertyList(EComAccountDesc.FLD_STONE_SOUK_MATERIAL_LIST);
		return list;
	}
	
	public void setFocUser(FocUser focUser){
		setPropertyObject(EComAccountDesc.FLD_USER, focUser);
	}
	
	public FocUser getFocUser(){
		return (FocUser) getPropertyObject(EComAccountDesc.FLD_USER);
	}
	
	public AdrBookParty getAdrBookParty(){
		return (AdrBookParty) getPropertyObject(EComAccountDesc.FLD_ADR_BOOK_PARTY);
	}
	
	public void setAdrBookParty(AdrBookParty adrBookParty){
		setPropertyObject(EComAccountDesc.FLD_ADR_BOOK_PARTY, adrBookParty);
	}
	
	public Contact newContact(){
		AdrBookParty adrBookParty = null;
		FocList partyList = AdrBookPartyDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
		adrBookParty = (AdrBookParty) partyList.newEmptyItem();
		adrBookParty.code_resetCode();
		adrBookParty.setName(getFirstName() +" "+ getLastName());
		adrBookParty.setEMail(getEMail());
		partyList.add(adrBookParty);
		adrBookParty.validate(true);
		
		FocList contactList = ContactDesc.getInstance().getFocList();
		Contact contact = (Contact) contactList.newEmptyItem();
		contact.setAdrBookParty(adrBookParty);
		contact.setFirstName(getFirstName());
		contact.setFamilyName(getLastName());
		contact.setEMail(getEMail());
		contact.validate(true);
		contactList.add(contact);
		contactList.validate(true);
		
		setAdrBookParty(adrBookParty);
		validate(true);
		return contact;
	}

	public void activate() {
		WFSite   site  = EComConfiguration.getInstance() != null ? EComConfiguration.getInstance().getGuestUserSite() : null;
		FocGroup group = EComConfiguration.getInstance() != null ? EComConfiguration.getInstance().getGuestUserGroup() : null;
		FocUser.createUserForContact(newContact(), getPassword(), group, site, null);
	}
}
