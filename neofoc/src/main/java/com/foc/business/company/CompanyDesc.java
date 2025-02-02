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

import com.foc.Globals;
import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.business.adrBook.AdrBookPartyDesc;
import com.foc.business.adrBook.AdrBookPartyGuiDetailsPanel;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FCompanyField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FImageField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class CompanyDesc extends FocDesc{
  
	public static final int FLD_NAME             =   FField.FLD_NAME;
  public static final int FLD_DESCRIPTION      =   FField.FLD_DESCRIPTION;
//  public static final int FLD_USER_RIGHTS_LIST =   1;
  public static final int FLD_VAT_RULE         =   2;
  public static final int FLD_OFFICIAL_ADDRESS =   3;
  public static final int FLD_ADR_BOOK_PARTY   =   4;
  public static final int FLD_LOGO_IMAGE       =   5;
  public static final int FLD_SITE_LIST        = 100;
  
//  public static int ADR_BOOK_PARTY_FLD_ADRESS = FField.NO_FIELD_ID;
  public static final String FNAME_OFFICIAL_ADDRESS = "OFFICIAL_ADDRESS";
  public static final String FNAME_LOGO             = "LOGO";
  
  public static final String DB_TABLE_NAME = "COMPANY";
  
  public CompanyDesc() {
    super(Company.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    setGuiBrowsePanelClass(CompanyGuiBrowsePanel.class);
    setGuiDetailsPanelClass(CompanyGuiDetailsPanel.class);
    addReferenceField();
    
    addNameField();
    addDescriptionField();
    
		FBlobStringField fField = new FBlobStringField(FNAME_OFFICIAL_ADDRESS, "Official Address", FLD_OFFICIAL_ADDRESS, false, 4, 30);
		fField.setReflectingField(true);
    addField(fField);
    
    FImageField signatureField = new FImageField(FNAME_LOGO, "Logo", FLD_LOGO_IMAGE, 400, 270);
    addField(signatureField);
    
    FObjectField oFld = new FObjectField("ADR_BOOK_PARTY", "Adress book party", CompanyDesc.FLD_ADR_BOOK_PARTY, AdrBookPartyDesc.getInstance());
		oFld.setSelectionList(AdrBookPartyDesc.getList(FocList.NONE));
		oFld.setDetailsPanelViewID(AdrBookPartyGuiDetailsPanel.VIEW_SELECTION);
		oFld.setComboBoxCellEditor(AdrBookPartyDesc.FLD_CODE_NAME);
		oFld.setMandatory(true);
		addField(oFld);
//		CompanyDesc.ADR_BOOK_PARTY_FLD_ADRESS = AdrBookPartyDesc.FLD_INVOICE_ADDRESS;    
  }

  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FField.FLD_NAME);
      list.setListOrder(order);
    }
    return list;
  }
  
  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, CompanyDesc.class);    
  }
  
	@Override
	public void afterLogin() {
		FocUser user = Globals.getApp().getUser();
		if(user != null && !user.isAdmin()){
			//Scan the full company list and hide FocListElements that we do not have access to
			//This has the effect of bringing combo boxes with only the companies we have rights to
			//-------------------------------------------------------------------------------------
			//USERRIGHTS
			/*
			FocList companyFullList = getList(FocList.LOAD_IF_NEEDED);
			if(companyFullList != null){
				for(int i=0; i<companyFullList.size(); i++){
					FocListElement companyElement = companyFullList.getFocListElement(i);
					Company        company        = (Company) companyFullList.getFocObject(i);
					
					if(company.getUserRights() != UserCompanyRightsDesc.ACCESS_RIGHT_READ_WRITE){
						companyElement.setHide_Soft(true);
					}
				}
			}
			
			//Check if this user has a default company if not or if the default company is not correct 
			//Set Any company
			if(!Globals.getApp().isWebServer()){
				Company comp = Globals.getApp().getCurrentCompany();
				if(comp == null || comp.getUserRights() != UserCompanyRightsDesc.ACCESS_RIGHT_READ_WRITE){
					user.setCurrentCompany(null);
					FocList list = user.getCompanyRightsList();
					if(list != null){
						for(int i=0; i<list.size(); i++){
							UserCompanyRights userCompanyRights = (UserCompanyRights) list.getFocObject(i);
							if(userCompanyRights != null && userCompanyRights.getAccessRight() == UserCompanyRightsDesc.ACCESS_RIGHT_READ_WRITE){
								user.setCurrentCompany(userCompanyRights.getCompany());
							}
						}
					}
					if(Globals.getApp().getCurrentCompany() == null){
						String message = "The user "+user.getName()+" has no Company assignement, please contact your administrator.";
						if(Globals.getDisplayManager() != null){
							Globals.getDisplayManager().popupMessage(message);
						}else{
							Globals.logString(message);
						}
					}
				}
			}
			*/
			//END USERRIGHTS
		}
	}

	public static void addCompanyFilter_IfNeeded(FocList list){
		FCompanyField field = (FCompanyField) list.getFocDesc().getFieldByID(FField.FLD_COMPANY);
		addCompanyFilter_IfNeeded(list, field.getDBName());
	}
	
	public static void addCompanyFilter_IfNeeded(FocList list, String fieldName){
		String whereKey = "COMPANY";
		if(list.getFilter().getAdditionalWhere(whereKey) == null){
			String whereExpression = getCompanyFilter_IfNeeded(fieldName);
			list.getFilter().putAdditionalWhere(whereKey, whereExpression);
		}
	}
	
	public static String getCompanyFilter_IfNeeded(String fieldName){
		return getCompanyFilter_IfNeeded(fieldName, false);
	}
	
	public static String getCompanyFilter_IfNeeded(String fieldName, boolean companyFieldMandatory){
		String  whereExpression = null;
		FocUser user            = Globals.getApp().getUser_ForThisSession();
		
		if(user != null){
			StringBuffer where       = new StringBuffer();
			Company      currCompany = Globals.getApp().getCurrentCompany();
			
			if(!companyFieldMandatory){
				where.append(fieldName + "=0");
			}
			
			if(currCompany != null){
				switch(user.getMultiCompanyMode()){
				case FocUserDesc.COMPANY_MODE_SEE_ONLY_CURRENT:
					if(!companyFieldMandatory){
						where.append(" OR ");
					}
					where.append(fieldName+"="+currCompany.getReference().getInteger());
					break;
				case FocUserDesc.COMPANY_MODE_SEE_ONLY_READ_WRITE:
				case FocUserDesc.COMPANY_MODE_SEE_ALL:
					StringBuffer refsList = new StringBuffer();
					FocList rightsList = user.getCompanyRightsList();
					for(int i=0; i<rightsList.size(); i++){
						UserCompanyRights compRights = (UserCompanyRights) rightsList.getFocObject(i);
						if(compRights != null){
							if(	   (compRights.getAccessRight() == UserCompanyRightsDesc.ACCESS_RIGHT_READ_ONLY  && user.getMultiCompanyMode() == FocUserDesc.COMPANY_MODE_SEE_ALL)
									|| (compRights.getAccessRight() == UserCompanyRightsDesc.ACCESS_RIGHT_READ_WRITE /*&& user.getMultiCompanyMode() == FocUserDesc.COMPANY_MODE_SEE_ONLY_READ_WRITE*/)){
								if(refsList.length() != 0){
									refsList.append(",");
								}
								refsList.append(compRights.getCompany().getReference().getInteger());
							}
						}
					}
					
					if(refsList.length() > 0){
						if(!companyFieldMandatory){
							where.append(" OR ");
						}
						where.append(fieldName+" in ("+refsList+")");
					}
					break;
				}
			}
			
			whereExpression = where.toString();
		}
		return whereExpression;
	}
}
