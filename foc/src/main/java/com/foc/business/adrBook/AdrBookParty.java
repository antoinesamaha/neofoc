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
package com.foc.business.adrBook;

import com.foc.admin.FocGroup;
import com.foc.business.config.BusinessConfig;
import com.foc.business.country.Country;
import com.foc.business.country.city.City;
import com.foc.business.country.region.Region;
import com.foc.business.currency.Currencies;
import com.foc.business.currency.Currency;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.util.ASCII;

@SuppressWarnings("serial")
public class AdrBookParty extends FocObject{

	public static final String EMPTY_PARTY = "EMPTY";
  public AdrBookParty(FocConstructor constr) {
    super(constr);
    newFocProperties();
    setCurrency(Currencies.getCurrencies().getDefaultViewCurrency());
  }
  
  @Override
  public void dispose(){
  	super.dispose();
  }
  
  @Override
	public FocList getObjectPropertySelectionList(int fieldID) {
		FocList list = super.getObjectPropertySelectionList(fieldID);
		if(fieldID == AdrBookPartyDesc.FLD_REGION){
			list = getCountry() != null ? getCountry().getRegionList() : null;
		}else if(fieldID == AdrBookPartyDesc.FLD_CITY){
			list = getRegion() != null ? getRegion().getCityList() : null;
		}
		return list;
	}

  @Override
	public String getSelectionFilterExpressionFor_ObjectProperty(int fieldID) {
  	String filterExpression = super.getSelectionFilterExpressionFor_ObjectProperty(fieldID);
  	if(fieldID == AdrBookPartyDesc.FLD_DEFAULT_CONTACT){
  		if(getReferenceInt() < 0){
  			filterExpression = "false";
  		}else{
  			FField fField = ContactDesc.getInstance().getFieldByID(ContactDesc.FLD_ADR_BOOK_PARTY);
  			if(fField != null && fField.getName() != null && !fField.getName().isEmpty()){
  				String fieldname = fField.getName();
  				filterExpression = fieldname+"."+AdrBookPartyDesc.getInstance().getRefFieldName()+ "="+getReferenceInt();
  			}
  		}
  	}
		return filterExpression; 
	}

  public FocGroup getFocGroupForContactUsers() {
  	return BusinessConfig.getInstance() != null ? BusinessConfig.getInstance().getGuestGroup() : null;
  }
  
	@Override
  public int code_getNumberOfDigits(){
  	return BusinessConfig.getInstance().getPartyNbrDigits();
  }
  
  @Override
  public String code_getPrefix(){
  	return BusinessConfig.getInstance().getPartyPrefix();
  }

  @Override
  public boolean code_isResetDigitsWhenPrefixChanges(){
  	return BusinessConfig.getInstance().getPartyResetCode();
  }

  @Override
  public String code_getSeperator(){
  	return BusinessConfig.getInstance().getPartySeparator();
  }

	public String getCode(){
    return getPropertyString(AdrBookPartyDesc.FLD_CODE);
  }

  public void setCode(String code){
    setPropertyString(AdrBookPartyDesc.FLD_CODE, code);
  }

  public String getName(){
    return getPropertyString(AdrBookPartyDesc.FLD_NAME);
  }

  public void setName(String name){
    setPropertyString(AdrBookPartyDesc.FLD_NAME, name);
  }
  
  public boolean isDifferentAddresses(){
  	return getPropertyBoolean(AdrBookPartyDesc.FLD_DIFFERENT_ADDRESSES);
  }

  public String getExtention1(){
    return getPropertyString(AdrBookPartyDesc.FLD_EXTENTION_1);
  }

  public void setExtention1(String name){
    setPropertyString(AdrBookPartyDesc.FLD_EXTENTION_1, name);
  }
  
  public String getExtention2(){
    return getPropertyString(AdrBookPartyDesc.FLD_EXTENTION_2);
  }

  public void setExtention2(String name){
    setPropertyString(AdrBookPartyDesc.FLD_EXTENTION_2, name);
  }
  
  public String getPhone1(){
    return getPropertyString(AdrBookPartyDesc.FLD_PHONE_1);
  }

  public void setPhone1(String name){
    setPropertyString(AdrBookPartyDesc.FLD_PHONE_1, name);
  }
 
  public String getPhone2(){
    return getPropertyString(AdrBookPartyDesc.FLD_PHONE_2);
  }

  public void setPhone2(String name){
    setPropertyString(AdrBookPartyDesc.FLD_PHONE_2, name);
  }
  
  public String getFax(){
    return getPropertyString(AdrBookPartyDesc.FLD_FAX);
  }

  public void setFax(String name){
    setPropertyString(AdrBookPartyDesc.FLD_FAX, name);
  }

  public String getPOBox(){
    return getPropertyString(AdrBookPartyDesc.FLD_PO_BOX);
  }

  public void setPOBox(String name){
    setPropertyString(AdrBookPartyDesc.FLD_PO_BOX, name);
  }
  
  public void setEMail(String name){
    setPropertyString(AdrBookPartyDesc.FLD_EMAIL, name);
  }
  
  public String getEMail(){
    return getPropertyString(AdrBookPartyDesc.FLD_EMAIL);
  }

  public String newDisplayLabel(){
  	return ASCII.newCodeName(getCode(), getName(), AdrBookPartyDesc.LEN_CODE);
  }
  
  public String getDisplayLabel(){
  	return getPropertyString(AdrBookPartyDesc.FLD_CODE_NAME);
  }
  
  public Currency getCurrency(){
    return (Currency) getPropertyObject(AdrBookPartyDesc.FLD_DEFAULT_CURRENCY);
  }

  public void setCurrency(Currency currency){
    setPropertyObject(AdrBookPartyDesc.FLD_DEFAULT_CURRENCY, currency);
  }

  public Contact getDefaultContact(){
    return (Contact) getPropertyObject(AdrBookPartyDesc.FLD_DEFAULT_CONTACT);
  }

  public void setDefaultContact(Contact contact){
    setPropertyObject(AdrBookPartyDesc.FLD_DEFAULT_CONTACT, contact);
  }

  public Country getCountry(){
    return (Country) getPropertyObject(AdrBookPartyDesc.FLD_COUNTRY);
  }

  public void setCountry(Country country){
    setPropertyObject(AdrBookPartyDesc.FLD_COUNTRY, country);
  }

  public Region getRegion(){
    return (Region) getPropertyObject(AdrBookPartyDesc.FLD_REGION);
  }

  public void setRegion(Region country){
    setPropertyObject(AdrBookPartyDesc.FLD_REGION, country);
  }

  public Region getCity(){
    return (Region) getPropertyObject(AdrBookPartyDesc.FLD_CITY);
  }

  public void setCity(City city){
    setPropertyObject(AdrBookPartyDesc.FLD_CITY, city);
  }

  public String getInvoiceAddress(){
    return getPropertyString(AdrBookPartyDesc.FLD_INVOICE_ADDRESS);
  }

  public void setInvoiceAddress(String adr){
    setPropertyString(AdrBookPartyDesc.FLD_INVOICE_ADDRESS, adr);
  }
  
  public String getDeliveryAddress(){
    return getPropertyString(AdrBookPartyDesc.FLD_DELIVERY_ADDRESS);
  }
  
  public void setDeliveryAddress(String adr){
    setPropertyString(AdrBookPartyDesc.FLD_DELIVERY_ADDRESS, adr);
  }
  
  public String getComment(){
    return getPropertyString(AdrBookPartyDesc.FLD_COMMENT);
  }
  
  public void SetComment(String comment){
    setPropertyString(AdrBookPartyDesc.FLD_COMMENT, comment);
  }

	public FocList newContactList(){
		FocList focList = new FocList(new FocLinkSimple(ContactDesc.getInstance()));
		focList.setCollectionBehaviour(true);
		
		FocList fullList = ContactDesc.getInstance().getFocList();
		fullList.loadIfNotLoadedFromDB();
		for(int i=0; i<fullList.size(); i++){
			Contact contact = (Contact) fullList.getFocObject(i);
			if(contact.getAdrBookParty() != null && contact.getAdrBookParty().equalsRef(this)){
				focList.add(contact);
			}
		}
		
  	return focList;
  }
	
	public static AdrBookParty checkIfAdressBookPartyExistsAndCreateIfNeeded(String auxCode, String auxName){
		AdrBookParty foundAdrBookParty = null;
		FocList adrBookPartyList = AdrBookPartyDesc.getInstance().getFocList();
		foundAdrBookParty = (AdrBookParty) adrBookPartyList.searchByPropertyStringValue(FField.FNAME_CODE, auxCode);
		if(foundAdrBookParty == null){
			foundAdrBookParty = (AdrBookParty) adrBookPartyList.newEmptyItem();
			foundAdrBookParty.setName(auxName);
			foundAdrBookParty.setCode(auxCode);
			adrBookPartyList.add(foundAdrBookParty);
			foundAdrBookParty.validate(true);
			adrBookPartyList.validate(true);
		}
		return foundAdrBookParty;
	}
	
	@Override
	public boolean validate(boolean checkValidity) {
		Contact defaultContactBackup = null;
		
		//This is to prevent infinite loop. Because we have 2 FocObjects that point to each other and they are both created. In this case Foc will
		//go into infinite loop
		if(isCreated() && getDefaultContact() != null && getDefaultContact().isCreated()){
			defaultContactBackup = getDefaultContact();
			setDefaultContact(null);
		}
		
		boolean ret = super.validate(checkValidity);

		if(defaultContactBackup != null){
			defaultContactBackup.validate(false);
			setDefaultContact(defaultContactBackup);
			validate(checkValidity);
		}		
		
		return ret;
	}
}
