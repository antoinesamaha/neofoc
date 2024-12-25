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
package com.foc.admin;

import com.foc.Globals;
import com.foc.business.BusinessModule;
import com.foc.business.adrBook.ContactDesc;
import com.foc.business.company.CompanyDesc;
import com.foc.business.company.UserCompanyRightsDesc;
import com.foc.business.multilanguage.MultiLanguage;
import com.foc.business.workflow.WFOperatorDesc;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FDateTimeField;
import com.foc.desc.field.FImageField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FPasswordField;
import com.foc.desc.field.FStringField;
import com.foc.list.FocLink;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.FocListIterator;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;
import com.foc.util.Encryptor;

public class FocUserDesc extends FocDesc {
	
  public static final String DB_TABLE_NAME = "FUSER";
  public static final String FLDNAME_NAME  = "NAME";
  public static final String FNAME_SUSPENDED = "SUSPENDED";
  public static final String FNAME_USER_COMPANY_LIST = UserCompanyRightsDesc.DB_TABLE_NAME+"_LIST";
  public static final String FNAME_OPERATOR_LIST     = WFOperatorDesc.DB_TABLE_NAME+"_LIST";
  public static final String FNAME_GROUP             = "UGROUP";
  
  public static final int FLD_NAME                 		=  1;
  public static final int FLD_PASSWORD             		=  2;
  public static final int FLD_GROUP                		=  3;  
  public static final int FLD_LANGUAGE                =  4;
  public static final int FLD_FONT_SIZE               =  5;
  public static final int FLD_COMPANY_RIGHTS_LIST     =  6;
  public static final int FLD_CURRENT_COMPANY         =  7;
  public static final int FLD_CURRENT_SITE            =  8;
  public static final int FLD_CURRENT_TITLE           =  9;
  public static final int FLD_MULTI_COMPANY_MODE      = 10;
  public static final int FLD_FULL_NAME               = 11;
  public static final int FLD_SIGNATURE_IMAGE         = 12;
  public static final int FLD_SUSPENDED               = 13;
  public static final int FLD_ENABLE_TOOL_TIP_TEXT    = 14;
  public static final int FLD_EMAIL_SEND_COMMAND_LINE = 15;
  public static final int FLD_CONTACT                 = 16;
  public static final int FLD_PRINT_UPON_SAVE         = 17;
  public static final int FLD_TRANSACTION_SORTING_INCREMENTAL = 18;
  public static final int FLD_PAGE_WIDTH                      = 19;
  public static final int FLD_XML_VIEW_SELECCTION_LIST        = 20;
  public static final int FLD_WF_OPERATOR_LIST                = 21;
  public static final int FLD_HISTORY_LIST                    = 22;
  public static final int FLD_IS_GUEST                        = 23;
  public static final int FLD_CURRENT_SIMULATION_ACTIVE       = 24;
  public static final int FLD_THEME                           = 25;
  public static final int FLD_REPLACEMENT_USER_ACTING_AS      = 26;
  public static final int FLD_SAAS_APPLICATION_ROLE           = 27;
  public static final int FLD_IS_SAAS_ADMIN                   = 28;
  public static final int FLD_CONTEXT_HELP_ACTIVATION         = 29;
  public static final int FLD_FAILED_LOGIN_ATTEMPTS           = 30;
  public static final int FLD_LOCKED                          = 31;
  public static final int FLD_LOCK_DATETIME                   = 32;
  
  public static final int COMPANY_MODE_SEE_ONLY_CURRENT    = 0;
  public static final int COMPANY_MODE_SEE_ONLY_READ_WRITE = 1;
  public static final int COMPANY_MODE_SEE_ALL             = 2;
  
  public static final int APPLICATION_ROLE_NONE                 = 0;
  public static final int APPLICATION_ROLE_REQUESTER            = 1;
  public static final int APPLICATION_ROLE_PROCUREMENT_OFFICER  = 2;

  public static final String APP_ROLE_NAME_PROCUREMENT_OFFICER = "Procurement Officer";
  		
  public static final int THEME_SYSTEM_DEFAULT = 0;
  public static final int THEME_VALO           = 1;
  public static final int THEME_REINDEER       = 2;
  
  public static final int LEN_FULL_NAME                    = 40;
  
  public static final String USER_EXTERNAL = "WEB_EXTERNAL";
  
	public FocUserDesc(){
		super(FocUser.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
		
		setGuiBrowsePanelClass(FocUserGuiBrowsePanel.class);
    setGuiDetailsPanelClass(FocUserGuiDetailsPanel.class);
    
    addReferenceField();

    FStringField focCharFld = new FStringField(FLDNAME_NAME, "Name", FLD_NAME, true, FStringField.NAME_LEN);
    focCharFld.setCapital(true);
    focCharFld.setMandatory(true);
    focCharFld.setLockValueAfterCreation(true);      
    addField(focCharFld);

    focCharFld = new FStringField("FULL_NAME", "Full name", FLD_FULL_NAME, false, LEN_FULL_NAME);
    addField(focCharFld);
    
    FPasswordField focPassFld = new FPasswordField("PASSWORD", "Password", FLD_PASSWORD, false, 22);
    focPassFld.setCapital(true);
    addField(focPassFld);
    
    FObjectField focObjFld = new FObjectField(FNAME_GROUP, "Group", FLD_GROUP, false, FocGroup.getFocDesc(), "GRP_");
    focObjFld.setSelectionList(FocGroup.getList(FocList.NONE));
    focObjFld.setComboBoxCellEditor(FocGroupDesc.FLD_NAME);
//    focObjFld.setMandatory(true);
    //focObjFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    addField(focObjFld);
    
    if(MultiLanguage.isMultiLanguage()){
      FMultipleChoiceField multiFld = new FMultipleChoiceField("LANGUAGE", "Language", FLD_LANGUAGE, false, 2);
      MultiLanguage.fillMutipleChoices(multiFld);
      addField(multiFld);
    }
    
    if(BusinessModule.getInstance().isMultiCompany()){
    	FObjectField objFld = new FObjectField("CURRENT_COMPANY", "Current Company", FLD_CURRENT_COMPANY, false, CompanyDesc.getInstance(), "CURRENT_COMPANY_");
    	objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    	objFld.setSelectionList(CompanyDesc.getList(FocList.NONE));
    	objFld.setComboBoxCellEditor(CompanyDesc.FLD_NAME);
    	addField(objFld);
    	/*
    	objFld.addListener(new FPropertyListener() {
				@Override
				public void propertyModified(FProperty property) {
					if(property != null && property.isManualyEdited()){
						FocUser user      = (FocUser) property.getFocObject();
						if(user != null){
							user.companyChanged();
						}
					}
				}
				
				@Override
				public void dispose() {
				}
			});
			*/

    	FMultipleChoiceField companyModeField = new FMultipleChoiceField("COMPANY_MODE", "Multi-Company", FLD_MULTI_COMPANY_MODE, false, 2);
    	companyModeField.addChoice(COMPANY_MODE_SEE_ONLY_CURRENT, "Access This Company Only");
    	companyModeField.addChoice(COMPANY_MODE_SEE_ONLY_READ_WRITE, "Access All Companies With Write Access");
    	companyModeField.addChoice(COMPANY_MODE_SEE_ALL, "Access All Companies");
    	addField(companyModeField);
    	
    	FMultipleChoiceField applicationRoleField = new FMultipleChoiceField("SAAS_APPLICATION_ROLE", "SaaS Application Role", FLD_SAAS_APPLICATION_ROLE, false, 4);
    	applicationRoleField.addChoice(APPLICATION_ROLE_NONE, "None");
    	applicationRoleField.addChoice(APPLICATION_ROLE_REQUESTER, "Requester");
    	applicationRoleField.addChoice(APPLICATION_ROLE_PROCUREMENT_OFFICER, APP_ROLE_NAME_PROCUREMENT_OFFICER);
    	addField(applicationRoleField);
    }

  	FObjectField objFld = new FObjectField("CURRENT_SITE", "Current Site", FLD_CURRENT_SITE, WFSiteDesc.getInstance());
  	objFld.setComboBoxCellEditor(WFSiteDesc.FLD_NAME);
  	addField(objFld);

  	objFld = new FObjectField("CURRENT_TITLE", "Current Title", FLD_CURRENT_TITLE, WFTitleDesc.getInstance());
  	objFld.setSelectionList(WFTitleDesc.getList(FocList.NONE));
  	objFld.setComboBoxCellEditor(WFTitleDesc.FLD_NAME);
  	addField(objFld);

    addFontSizeField(this, "FONT_SIZE", FLD_FONT_SIZE);
    
    FImageField signatureField = new FImageField("SIGNATURE", "Signature", FLD_SIGNATURE_IMAGE, 200, 100);
    addField(signatureField);
    
    FBoolField bFld = new FBoolField(FNAME_SUSPENDED, "Suspended", FLD_SUSPENDED, false);
    addField(bFld);
    
    bFld = new FBoolField("IS_SAAS_ADMIN", "Is Saas Admin", FLD_IS_SAAS_ADMIN, false);
    addField(bFld);

    bFld = new FBoolField("IS_GUEST", "Is Guest", FLD_IS_GUEST, false);
    //bFld.setAllwaysLocked(true);
    addField(bFld);
    
    bFld = new FBoolField("SIMULATION_ACTIVE", "Simulation Active", FLD_CURRENT_SIMULATION_ACTIVE, false);
    addField(bFld);

    bFld = new FBoolField("PRINT_UPON_SAVE", "Print upon save", FLD_PRINT_UPON_SAVE, false);
    addField(bFld);
    
    bFld = new FBoolField("TRAN_ORDER_INCREASE", "Transaction order increasing", FLD_TRANSACTION_SORTING_INCREMENTAL, false);
    addField(bFld);
    
    bFld = new FBoolField("ENABLE_TTT", "Enable Tool Tip Text", FLD_ENABLE_TOOL_TIP_TEXT, false);
    addField(bFld);
    
    FStringField cFld = new FStringField("SEND_EMAIL_COMMAND_LINE", "Send Email Command line",  FLD_EMAIL_SEND_COMMAND_LINE, false, 250);
//    cFld.setDefaultStringValue("\"C:\\Program Files (x86)\\Microsoft Office\\Office14\\Outlook.exe\" /c ipm.note /m");
    addField(cFld);
    
    objFld = ContactDesc.newContactField("CONTACT", "Contact details", FLD_CONTACT);
//    objFld.setAllwaysLocked(true);
    addField(objFld);
    
    FIntField iFld = new FIntField("PAGE_WIDTH", "Page width", FLD_PAGE_WIDTH, false, 4);
    addField(iFld);
    
  	FMultipleChoiceField bThemeField = new FMultipleChoiceField("THEME", "Theme", FLD_THEME, false, 2);
  	bThemeField.addChoice(THEME_SYSTEM_DEFAULT, "Factory default");
  	bThemeField.addChoice(THEME_REINDEER, "Reindeer");
  	bThemeField.addChoice(THEME_VALO, "Valo");
  	addField(bThemeField);
  	bThemeField.addListener(new FPropertyListener() {
			@Override
			public void propertyModified(FProperty property) {
				if(property.isManualyEdited()){
					Globals.applyUserTheme();
				}
			}
			
			@Override
			public void dispose() {
			}
		});  	
  	
  	FObjectField replacementUserFld = new FObjectField("REPLACEMENT_USER", "Replacement User", FLD_REPLACEMENT_USER_ACTING_AS, FocUserDesc.this);
  	replacementUserFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
  	addField(replacementUserFld);
  	
  	FBoolField boolField = new FBoolField("CONTEXT_HELP_ACTIVATION", "Context Help Activation", FLD_CONTEXT_HELP_ACTIVATION, false);
    addField(boolField);
    
    iFld = new FIntField("FAILED_LOGIN_ATTEMPTS", "Failed Login Attempts", FLD_FAILED_LOGIN_ATTEMPTS, false, 4);
    addField(iFld);
    
    boolField = new FBoolField("LOCKED", "Account Locked", FLD_LOCKED, false);
    addField(boolField);
    
    FDateTimeField dateFld = new FDateTimeField("LOCK_DATETIME", "Account Lock Date Time", FLD_LOCK_DATETIME, false);
		addField(dateFld);
	}

	public static FMultipleChoiceField addFontSizeField(FocDesc focDesc, String name, int id){
	  FMultipleChoiceField multiFld = new FMultipleChoiceField(name, "Font size", id, false, 2);
	  multiFld.addChoice(8, "8");
	  multiFld.addChoice(9, "9");
	  multiFld.addChoice(10, "10");
	  multiFld.addChoice(11, "11");
	  multiFld.addChoice(12, "12");
	  multiFld.addChoice(13, "13");
	  multiFld.addChoice(14, "14");      
	  multiFld.addChoice(15, "15");
	  multiFld.addChoice(16, "16");
	  multiFld.addChoice(17, "17");
	  multiFld.addChoice(18, "18");
	  multiFld.setSortItems(false);
	  focDesc.addField(multiFld);
	  return multiFld;
	}
	
  public void afterAdaptTableModel(){
  	FocVersion dbVersion = FocVersion.getDBVersionForModule("FOC");
  	if(dbVersion != null && dbVersion.getId() <= 1503){
    	FocList usersList = FocUserDesc.newList();
    	usersList.loadIfNotLoadedFromDB();
    	usersList.iterate(new FocListIterator(){
				public boolean treatElement(FocListElement element, FocObject focObj) {
					FocUser user = (FocUser) focObj;
					user.setPassword(Encryptor.encrypt_MD5(user.getPassword()));
					return false;
				}
    	});
    	usersList.validate(false);
  	}
  }
  
  public static FocList getList() {
	  return getList(FocList.LOAD_IF_NEEDED);
	}

	public static FocList newList() {
	  FocLink link = new FocLinkSimple(getInstance());
	  FocList list = new FocList(link);
	  list.setDirectImpactOnDatabase(true);
	  list.setDirectlyEditable(false);
	  return list;
	}

	public FocList newFocList(){
		FocList list = super.newFocList();
	  list.setDirectImpactOnDatabase(true);
	  list.setDirectlyEditable(false);
	  return list;
	}
	
	public static FocList getList(int mode) {
		return getInstance().getFocList(mode);
  }

	public static FocUserDesc getInstance(){
		return (FocUserDesc) getInstance(DB_TABLE_NAME, FocUserDesc.class);
  }
}
