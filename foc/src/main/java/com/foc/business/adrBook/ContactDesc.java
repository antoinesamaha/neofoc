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

import com.foc.business.company.Company;
import com.foc.business.company.CompanyDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FEMailField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceStringField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FPhoneField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class ContactDesc extends FocDesc {

  public static final int FLD_FIRST_NAME     =  1;
  public static final int FLD_FAMILY_NAME    =  2;
  public static final int FLD_FULL_NAME      =  3;
  public static final int FLD_DESCRIPTION    =  4;
  public static final int FLD_POSITION       =  5;
  public static final int FLD_PHONE_1        =  6;
  public static final int FLD_PHONE_2        =  7;
  public static final int FLD_MOBILE         =  8;
  public static final int FLD_EMAIL          =  9;
  public static final int FLD_EMAIL_2        = 10;
  public static final int FLD_ADR_BOOK_PARTY = 11;
  //public static final int FLD_RELEVANT_USER  = 12;
  public static final int FLD_POSITION_STR   = 13;
  public static final int FLD_TITLE          = 14;
  public static final int FLD_INTRODUCTION   = 15;
  public static final int FLD_COMPANY_NAME   = 16;

  public static final int LEN_TITLE          = 10;
  public static final int LEN_FIRST_NAME     = 20;
  public static final int LEN_FAMILY_NAME    = 50;
  public static final int LEN_POSITION       = 50;
  public static final int LEN_FULL_NAME      = LEN_TITLE+1+LEN_FIRST_NAME+1+LEN_FAMILY_NAME+1+1+LEN_POSITION+1;
  
  public static final String USER_PREFIX = "USER_"; 

  public static final String FPARTY_NAME    = "ADR_BK_PARTY";
  public static final String FPARTY_NAME_PREFIX = "ADR_BK_PARTY_";
  public static final String DB_TABLE_NAME = "ADR_BK_CONTACT";
  
  public static final String FNAME_Title = "TITLE";
  public static final String FNAME_FirstName = "FIRST_NAME";
  public static final String FNAME_FamilyName = "FAMILY_NAME";
  public static final String FNAME_Position = "POSITION_DESC";
  public static final String FNAME_Phone1 = "PHONE1";
  public static final String FNAME_Phone2 = "PHONE2";
  public static final String FNAME_Mobile = "MOBILE";
  public static final String FNAME_EMail  = "EMAIL";
  public static final String FNAME_EMail2 = "EMAIL2";
  
  public ContactDesc() {
    super(Contact.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
    setGuiBrowsePanelClass(ContactGuiBrowsePanel.class);
    setGuiDetailsPanelClass(ContactGuiDetailsPanel.class);
    
//    focDesc = this;
    
    FField fField = addReferenceField();

    FPropertyListener firstLastNameListener = new FPropertyListener(){
			public void dispose() {
			}

			public void propertyModified(FProperty property) {
				if(property != null){
					Contact contact = (Contact) property.getFocObject();
					contact.adjustFullName();
				}
			}
    };
    
    fField = new FStringField(FNAME_FirstName, "First Name", FLD_FIRST_NAME, true, LEN_FIRST_NAME);
    fField.setMandatory(true);
    fField.addListener(firstLastNameListener);
    addField(fField);
    
    fField = new FStringField(FNAME_FamilyName, "Family Name", FLD_FAMILY_NAME, true, LEN_FAMILY_NAME);
    fField.setMandatory(true);
    fField.addListener(firstLastNameListener);    
    addField(fField);

    fField = new FStringField("FULL_NAME", "Full Name", FLD_FULL_NAME, false, LEN_FULL_NAME);
    fField.setDBResident(false);
    addField(fField);

    fField = new FStringField("COMPANY_NAME", "Company name", FLD_COMPANY_NAME, false, AdrBookPartyDesc.LEN_NAME);
    fField.setDBResident(true);
    addField(fField);
    
    fField = new FStringField("INTRODUCTION", "Introduction", FLD_INTRODUCTION, false, 500);
    addField(fField);
    
    fField = new FBlobStringField("DESCRIP", "Description", FLD_DESCRIPTION, false, 5, 30);
    addField(fField);

    FMultipleChoiceStringField mFld = new FMultipleChoiceStringField(FNAME_Position, "Position", FLD_POSITION_STR, false, LEN_POSITION);
    mFld.setChoicesAreFromSameColumn(this);
    addField(mFld);
    
    mFld = new FMultipleChoiceStringField(FNAME_Title, "Title", FLD_TITLE, false, LEN_TITLE);
    mFld.setChoicesAreFromSameColumn(this);
    mFld.addListener(firstLastNameListener);
    addField(mFld);
    
    FObjectField objFld = new FObjectField("POSITION", "Position (Do not use)", FLD_POSITION, false, PositionDesc.getInstance(), "POSITION_");
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objFld.setDetailsPanelViewID(PositionGuiDetailsPanel.VIEW_SELECTION);
    objFld.setDisplayField(PositionDesc.FLD_NAME);
    objFld.setSelectionList(PositionDesc.getList(FocList.NONE));
    objFld.setMandatory(false);
    addField(objFld);

    objFld = new FObjectField(FPARTY_NAME, "Party", FLD_ADR_BOOK_PARTY, true, AdrBookPartyDesc.getInstance(), FPARTY_NAME_PREFIX);
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objFld.setDetailsPanelViewID(AdrBookPartyGuiBrowsePanel.VIEW_SELECTION);
    objFld.setDisplayField(AdrBookPartyDesc.FLD_CODE_NAME);
    objFld.setSelectionList(AdrBookPartyDesc.getList(FocList.NONE));
    //objFld.setMandatory(true);
    addField(objFld);
//    ((FListField)AdrBookPartyDesc.getInstance().getFieldByID(AdrBookPartyDesc.FLD_CONTACT_LIST)).setDirectlyEditable(false);
    objFld.addListener(new FPropertyListener() {
			@Override
			public void propertyModified(FProperty property) {
				Contact contact = (Contact) property.getFocObject();
				if(contact != null){
					if(contact.getAdrBookParty() != null){
						contact.setCompanyName(contact.getAdrBookParty().getName());
						contact.getFocProperty(FLD_COMPANY_NAME).setValueLocked(true);
					}else{
						contact.getFocProperty(FLD_COMPANY_NAME).setValueLocked(false);
					}
				}
			}
			
			@Override
			public void dispose() {
			}
		});

    fField = new FPhoneField(FNAME_Phone1, "Phone 1", FLD_PHONE_1);
    addField(fField);

    fField = new FPhoneField(FNAME_Phone2, "Phone 2", FLD_PHONE_2);
    addField(fField);

    fField = new FPhoneField(FNAME_Mobile, "Mobile", FLD_MOBILE);
    addField(fField);
    
    fField = new FEMailField(FNAME_EMail, "Email", FLD_EMAIL, false);
    addField(fField);

    fField = new FEMailField(FNAME_EMail2, "Email 2", FLD_EMAIL_2, false);
    addField(fField);

    /*
    FObjectField fObjectFld = new FObjectField("USER", "User", FLD_RELEVANT_USER, false, FocUser.getFocDesc(), USER_PREFIX);
    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fObjectFld.setMandatory(false);
    fObjectFld.setSelectionList(FocUser.getList(FocList.NONE));
    fObjectFld.setDBResident(NOT_DB_RESIDENT);
    addField(fObjectFld);
    */
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocListOrder order = null;
  public static FocListOrder getListOrder(){
    if(order == null){
    	order = new FocListOrder(FLD_FAMILY_NAME, FLD_FIRST_NAME);
    }
    return order;
  }
  
  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectImpactOnDatabase(true);
    list.setDirectlyEditable(false);
    list.setViewIDForSelectionBrowse(ContactGuiBrowsePanel.VIEW_SELECTION);
    if(list.getListOrder() == null){
    	list.setListOrder(getListOrder());
    }
    return list;
  }

  public static FObjectField newContactField(String name, String title, int id){
  	return newContactField(name, title, id, false);
  }
  
  public static FObjectField newContactField(String name, String title, int id, boolean comboBox){ 
  	FObjectField objFld = new FObjectField(name, title, id, getInstance());
  	objFld.setDisplayField(ContactDesc.FLD_FULL_NAME);
  	if(comboBox){
  		objFld.setComboBoxCellEditor(ContactDesc.FLD_FULL_NAME);
  	}else{
	    objFld.setDetailsPanelViewID(ContactGuiDetailsPanel.VIEW_SELECTION);    
  	}
  	objFld.setSelectionList(getList(FocList.NONE));
  	return objFld;
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, ContactDesc.class);    
  }
  
  public static String getFilterExpression_ForAllOurCompanies(){
		String baseCondition = "ADR_BK_PARTY.REF";
		String conditionLine = "";
		boolean withOr = false;
		FocList focList = CompanyDesc.getList(FocList.LOAD_IF_NEEDED);
		for(int i=0; i<focList.size(); i++){
			Company company = (Company) focList.getFocObject(i);
			if(company != null && company.getAdrBookParty() != null && company.getAdrBookParty().getReference() != null){
				int adrBkRef = company.getAdrBookParty().getReference().getInteger();
				if(!conditionLine.isEmpty()){
					conditionLine += ",";
					withOr = true;
				}
				conditionLine += baseCondition+"="+adrBkRef;
			}
		}
		String expression =  withOr ? "OR(" + conditionLine + ")" : conditionLine;
		return expression;
  }
}
