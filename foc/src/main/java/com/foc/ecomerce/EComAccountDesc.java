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

import java.util.Currency;
import java.util.Iterator;
import java.util.Set;

import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.business.adrBook.AdrBookPartyDesc;
import com.foc.business.country.CountryDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FCompanyField;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FEMailField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceStringField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FPasswordField;
import com.foc.desc.field.FPhoneField;
import com.foc.list.FocList;

public class EComAccountDesc extends FocDesc {

  //public static final int FLD_USERNAME         = 2;
  public static final int FLD_PASSWORD         = 3;
  public static final int FLD_CONFIRM_PASSWORD = 4;
  public static final int FLD_EMAIL            = 5;
  
  public static final int FLD_FIRST_NAME       = 6;
  public static final int FLD_LAST_NAME        = 7;
  public static final int FLD_PHONE            = 8;

  public static final int FLD_COMPANY_NAME     = 9;
  public static final int FLD_COUNTRY          = 10;
  public static final int FLD_CURRENCY_SYMBOL  = 11;
  public static final int FLD_INVOICE_ADDRESS  = 12;
  public static final int FLD_USER             = 13;
  
  public static final int FLD_ACTIVATION_DATE  = 23;
  public static final int FLD_CREATION_DATE    = 24;
  public static final int FLD_ACTIVATION_LINK  = 25;
  public static final int FLD_ADR_BOOK_PARTY   = 26;
  
  public static final int FLD_STONE_SOUK_MATERIAL_LIST = 27;
  
  public static final String DB_TABLE_NAME = "ECOM_ACCOUNT";
  public static final int LEN_ACTIVATION_LINK = 100;
  
  public EComAccountDesc() {
    super(EComAccount.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    
    addReferenceField();
    
    FCompanyField companyFld = new FCompanyField(true, true);
    addField(companyFld);
    
    FField fField = new FPasswordField("PASSWORD", "Password", FLD_PASSWORD);
    fField.setMandatory(true);    
    addField(fField);
    
    fField = new FPasswordField("CONFIRM_PASSWORD", "Confirm Password", FLD_CONFIRM_PASSWORD);
    fField.setMandatory(true);
    fField.setDBResident(false);
    addField(fField);
    
    fField = new FEMailField("EMAIL", "Email", FLD_EMAIL, false);
    fField.setMandatory(true);
    fField.setLockValueAfterCreation(true);
    addField(fField);
    
    fField = new FStringField("FIRST_NAME", "Name", FLD_FIRST_NAME, false, 50);
    fField.setMandatory(true);    
    addField(fField);
    
    fField = new FStringField("LAST_NAME", "Last Name", FLD_LAST_NAME, false, 50);
    fField.setMandatory(true);    
    addField(fField);

    fField = new FPhoneField("PHONE", "Phone", FLD_PHONE);
    addField(fField);

    fField = new FStringField("COMPANY_NAME", "Company name", FLD_COMPANY_NAME, false, 100);
    fField.setLockValueAfterCreation(true);
    fField.setMandatory(true);    
    addField(fField);
        
		fField = new FBlobStringField("INVOICING_ADDRESS", "Invoicing Address", FLD_INVOICE_ADDRESS, false, 4, 30);
    addField(fField);
    
  	FObjectField objFld = new FObjectField("COUNTRY", "Country", FLD_COUNTRY, CountryDesc.getInstance());
    objFld.setComboBoxCellEditor(CountryDesc.FLD_COUNTRY_NAME);
    objFld.setDisplayField(CountryDesc.FLD_COUNTRY_NAME);    
    objFld.setSelectionList(CountryDesc.getList(FocList.NONE));
    addField(objFld);
    
    FMultipleChoiceStringField mField = new FMultipleChoiceStringField("CURRENCY_SYMBOL", "Currency Symbol", FLD_CURRENCY_SYMBOL, false, 5);
		//Set<Currency> set = java.util.Currency.getAvailableCurrencies();
    Set<Currency> set = null;
    if(set != null){
			Iterator<Currency> iter = set.iterator();
			while(iter != null && iter.hasNext()){
				Currency curr = iter.next();
				mField.addChoice(curr.getCurrencyCode());
			}
			mField.setAllowOutofListSelection(false);
	    addField(mField);
	    
	    FDateField datefld = new FDateField("ACTIVATION_DATE", "Activation Date", FLD_ACTIVATION_DATE, false);
	    addField(datefld);
	    
	    datefld = new FDateField("CREATION_DATE", "Creation Date", FLD_CREATION_DATE, false);
	    addField(datefld);
	    
	    FStringField fld = new FStringField("ACTIVATION_LINK", "Activation link", FLD_ACTIVATION_LINK, false, LEN_ACTIVATION_LINK);
	    addField(fld);
	    
	    FObjectField fObjectFld = new FObjectField("USER", "User", FLD_USER, false, FocUser.getFocDesc(), "USER_");
	    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
	    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
	    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
	    addField(fObjectFld);
	    
	    fObjectFld = AdrBookPartyDesc.newAdrBookPartyField("ADR_BOOK_PARTY", "Adr Book Party", FLD_ADR_BOOK_PARTY);
	    addField(fObjectFld);
    }
  }
  
  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    return list;
  }
  
  public static EComAccount findEAccountByEmail(String email){
  	EComAccount portfolio = null;
  	FocList      list         = getList(FocList.LOAD_IF_NEEDED);
  	if(list != null){
  		portfolio = (EComAccount) list.searchByPropertyStringValue(FLD_EMAIL, email);
  	}
  	return portfolio;
  }
  
  public static EComAccount findEAccountByAdrBookParty(int ref){
  	EComAccount portfolio = null;
  	FocList      list         = getList(FocList.LOAD_IF_NEEDED);
  	if(list != null){
  		portfolio = (EComAccount) list.searchByPropertyObjectReference(FLD_ADR_BOOK_PARTY, ref);
  	}
  	return portfolio;
  }
	
	public static EComAccountDesc getInstance() {
    return (EComAccountDesc) getInstance(DB_TABLE_NAME, EComAccountDesc.class);
  }
}
