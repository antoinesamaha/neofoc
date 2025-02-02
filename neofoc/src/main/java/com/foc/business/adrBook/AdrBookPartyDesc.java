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

import com.foc.Globals;
import com.foc.admin.FocVersion;
import com.foc.business.BusinessModule;
import com.foc.business.country.CountryDesc;
import com.foc.business.country.city.CityDesc;
import com.foc.business.country.region.RegionDesc;
import com.foc.business.multilanguage.LanguageDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FCompanyField;
import com.foc.desc.field.FEMailField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceStringField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FPhoneField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class AdrBookPartyDesc extends FocDesc {

  public static final int FLD_CODE                = FField.FLD_CODE;
  public static final int FLD_EXTERNAL_CODE       = FField.FLD_EXTERNAL_CODE;
  public static final int FLD_NAME                = 2;
  public static final int FLD_CODE_NAME           = 3;
  public static final int FLD_INDUSTRY            = 4;
  public static final int FLD_LANGUAGE            = 6;
  public static final int FLD_COUNTRY             = 7;
  public static final int FLD_DELIVERY_ADDRESS    = 8;
  public static final int FLD_INVOICE_ADDRESS     = 9;
  public static final int FLD_PHONE_1             = 10;
  public static final int FLD_PHONE_2             = 11;
  public static final int FLD_FAX                 = 12;
  public static final int FLD_EMAIL               = 13;
  public static final int FLD_WEB                 = 14;
  public static final int FLD_MOF_REG_NBR         = 15;
  public static final int FLD_DEFAULT_CURRENCY    = 19;
  public static final int FLD_REGION              = 22;
  public static final int FLD_CITY                = 23;
  public static final int FLD_PO_BOX              = 24;
  public static final int FLD_MOBILE              = 25;
  public static final int FLD_DEFAULT_CONTACT     = 26;
  public static final int FLD_COMMENT             = 27;
  public static final int FLD_EXTENTION_1         = 28;
  public static final int FLD_EXTENTION_2         = 29;
  public static final int FLD_INTRODUCTION        = 30;
  public static final int FLD_DIFFERENT_ADDRESSES = 31;
  
  public static final String FNAME_INVOICING_ADDRESS = "INVOICING_ADDRESS";
  public static final String FNAME_DELIVERY_ADDRESS  = "DELIVERY_ADDRESS";
  
//  public static final int FLD_CONTACT_LIST        = 31;
  
  public static final int LEN_CODE        = 20;
  public static final int LEN_NAME        = 100;
  public static final int LEN_EMAIL       = 50;
  public static final int LEN_MOF_REG_NBR = 20;
  
  public static final String DB_TABLE_NAME = "ADR_BK_PARTY";
  
  public AdrBookPartyDesc() {
    super(AdrBookParty.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
    setGuiBrowsePanelClass(AdrBookPartyGuiBrowsePanel.class);
    setGuiDetailsPanelClass(AdrBookPartyGuiDetailsPanel.class);
    
//    focDesc = this; 
    	
    FPropertyListener codeNameListener = new FPropertyListener(){
			public void dispose() {
			}

			public void propertyModified(FProperty property) {
				AdrBookParty party = (AdrBookParty) property.getFocObject();
				party.setPropertyString(FLD_CODE_NAME, party.newDisplayLabel());
			}
    };
    
    FField fField = addReferenceField();
    
    fField = new FStringField("CODE", "Code", FLD_CODE, true, LEN_CODE);
    fField.setLockValueAfterCreation(true);
    fField.setMandatory(true);
    fField.addListener(codeNameListener);
    addField(fField);
    
    fField = new FStringField("NAME", "Name", FLD_NAME, false, LEN_NAME);
    fField.setLockValueAfterCreation(true);
    fField.setMandatory(true);    
    fField.addListener(codeNameListener);
    addField(fField);

    addExternalCodeField();

    FMultipleChoiceStringField mFld = new FMultipleChoiceStringField("INDUSTRY", "Industry", FLD_INDUSTRY, false, 40);
    mFld.setChoicesAreFromSameColumn(this);
    addField(mFld);
    
    fField = new FStringField("CODENAME", "Display", FLD_CODE_NAME, false, LEN_CODE + LEN_NAME + 2);//2 for the : and space
    fField.setDBResident(false);
    addField(fField);
    
    fField = new FPhoneField("PHONE1", "Phone 1", FLD_PHONE_1);
    addField(fField);
    
    fField = new FPhoneField("PHONE2", "Phone 2", FLD_PHONE_2);
    addField(fField);
    
    fField = new FStringField("EXTENTION1", "Extention 1", FLD_EXTENTION_1, false, 20);
    addField(fField);
    
    fField = new FStringField("EXTENTION2", "Extention 2", FLD_EXTENTION_2, false, 20);
    addField(fField);

    fField = new FPhoneField("MOBILE", "Mobile", FLD_MOBILE);
    addField(fField);
    
    fField = new FPhoneField("FAX", "Fax", FLD_FAX);
    addField(fField);
    
    fField = new FEMailField("EMAIL", "Email", FLD_EMAIL, false);
    addField(fField);
    
    fField = new FStringField("WEB", "Web", FLD_WEB, false, 30);
    addField(fField);

    fField = new FStringField("PO_BOX", "P.O.Box", FLD_PO_BOX, false, 10);
    addField(fField);
        
  	fField = new FStringField("MOF_REG_NO", "MOF Reg. No.", FLD_MOF_REG_NBR, false, LEN_MOF_REG_NBR);
  	addField(fField);
    
    FCompanyField companyFld = new FCompanyField(false, false);
    addField(companyFld);
    
		fField = new FBlobStringField(FNAME_INVOICING_ADDRESS, "Invoicing Address", FLD_INVOICE_ADDRESS, false, 4, 30);
    addField(fField);
    fField.addListener(new FPropertyListener() {
			@Override
			public void propertyModified(FProperty property) {
				if(property != null){
					AdrBookParty party = (AdrBookParty) property.getFocObject();
					if(party != null && !party.isDifferentAddresses()){
						party.setDeliveryAddress(party.getInvoiceAddress());
					}
				}
			}
			
			@Override
			public void dispose() {
			}
		});
    
  	fField = new FBlobStringField(FNAME_DELIVERY_ADDRESS, "Delivery Address", FLD_DELIVERY_ADDRESS, false, 4, 30);
  	addField(fField);
  	
  	FObjectField objFld = new FObjectField("COUNTRY", "Country", FLD_COUNTRY, CountryDesc.getInstance());
    //objFld.setDetailsPanelViewID(FocObject.DEFAULT_VIEW_ID);
    objFld.setComboBoxCellEditor(CountryDesc.FLD_COUNTRY_NAME);
    objFld.setDisplayField(CountryDesc.FLD_COUNTRY_NAME);    
    objFld.setSelectionList(CountryDesc.getList(FocList.NONE));
    addField(objFld);
    
  	objFld = new FObjectField("REGION", "Region", FLD_REGION, RegionDesc.getInstance());
    //objFld.setDetailsPanelViewID(FocObject.DEFAULT_VIEW_ID);
    objFld.setComboBoxCellEditor(RegionDesc.FLD_REGION_NAME);
    objFld.setDisplayField(RegionDesc.FLD_REGION_NAME);    
    objFld.setSelectionList(RegionDesc.getList(FocList.NONE));
    addField(objFld);

  	objFld = new FObjectField("CITY", "City", FLD_CITY, CityDesc.getInstance());
    //objFld.setDetailsPanelViewID(FocObject.DEFAULT_VIEW_ID);
    objFld.setComboBoxCellEditor(CityDesc.FLD_CITY_NAME);
    objFld.setDisplayField(CityDesc.FLD_CITY_NAME);    
    objFld.setSelectionList(CityDesc.getList(FocList.NONE));
    addField(objFld);
    
    objFld = new FObjectField("LANGUAGE", "Language", FLD_LANGUAGE, false, LanguageDesc.getInstance(), "LANGUAGE_");
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    //objFld.setDetailsPanelViewID(LanguageGuiDetailsPanel.VIEW_SELECTION);
    objFld.setComboBoxCellEditor(LanguageDesc.FLD_DESCRIPTION);
    objFld.setDisplayField(LanguageDesc.FLD_DESCRIPTION);    
    objFld.setSelectionList(LanguageDesc.getList(FocList.NONE));
    addField(objFld);
    
    objFld = ContactDesc.newContactField("DEFAULT_CONTACT", "Main Contact", FLD_DEFAULT_CONTACT, true);
    objFld.setReferenceChecker_PutToZeroWhenReferenceDeleted(true);
    addField(objFld);
    
    fField = new FStringField("COMMENT", "Comment", FLD_COMMENT, false, 200);//2 for the : and space
    addField(fField);
    
    fField = new FStringField("INTRODUCTION", "Introduction", FLD_INTRODUCTION, false, 500);
    addField(fField);
    
    FBoolField bFld = new FBoolField("DIFFERENT_ADDRESSES", "Different Addresses", FLD_DIFFERENT_ADDRESSES, false);
    addField(bFld);
    bFld.addListener(new FPropertyListener() {
			@Override
			public void propertyModified(FProperty property) {
				AdrBookParty party = (AdrBookParty) property.getFocObject();
				if(party != null && !party.isDifferentAddresses()){
					party.setDeliveryAddress(party.getInvoiceAddress());
				}
			}
			
			@Override
			public void dispose() {
			}
		});
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo  
  
  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setListOrder(new FocListOrder(FLD_NAME));
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    return list;
  }
  
  public static AdrBookParty findAdrBookParty(String code){
  	AdrBookParty adrBookParty = null;
  	FocList      list         = getList(FocList.LOAD_IF_NEEDED);
  	if(list != null){
  		adrBookParty = (AdrBookParty) list.searchByPropertyStringValue(AdrBookPartyDesc.FLD_CODE, code);
  	}
  	return adrBookParty;
  }
  
	public static FObjectField newAdrBookPartyField(String name, String title, int id){
		FObjectField objFld = new FObjectField(name, title, id, AdrBookPartyDesc.getInstance());
	  objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
	  objFld.setSelectionList(AdrBookPartyDesc.getList(FocList.NONE));
	  objFld.setComboBoxCellEditor(AdrBookPartyDesc.FLD_CODE);
	  //objFld.setMultiLineComboBoxCellEditor(displayField, tableView)Pa(displayField, tableView)SelectionList(selectionList)
	  //objFld.setBrowsePopupComboBoxCellEditor(PartyDesc.FLD_CODE, tableView);
	  return objFld;
	}
	
	@Override
	public void afterAdaptTableModel() {
		super.afterAdaptTableModel();
		try{
	  	FocVersion dbVersion = FocVersion.getDBVersionForModule(BusinessModule.MODULE_NAME);
	  	if(dbVersion != null && dbVersion.getId() <= BusinessModule.MODULE_ID_LAST_WITH_BOTH_ADDRESSES_DIFFERENT_EDITABLE){
				StringBuffer strBuffer = new StringBuffer("UPDATE "+DB_TABLE_NAME+" set DIFFERENT_ADDRESSES=1 where INVOICING_ADDRESS <> DELIVERY_ADDRESS");
				Globals.getApp().getDataSource().command_ExecuteRequest(strBuffer);
	  	}
		}catch(Exception e){
			Globals.logException(e);
		}
	}
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, AdrBookPartyDesc.class);
  }
}
