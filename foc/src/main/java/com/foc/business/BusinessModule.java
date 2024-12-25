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
package com.foc.business;

import com.foc.Application;
import com.foc.Globals;
import com.foc.admin.FocVersion;
import com.foc.business.adrBook.AdrBookPartyDesc;
import com.foc.business.adrBook.ContactDesc;
import com.foc.business.adrBook.PositionDesc;
import com.foc.business.company.CompanyDesc;
import com.foc.business.company.UserCompanyRightsDesc;
import com.foc.business.config.BusinessConfigDesc;
import com.foc.business.country.CountryDesc;
import com.foc.business.country.city.CityDesc;
import com.foc.business.country.region.RegionDesc;
import com.foc.business.department.DepartmentDesc;
import com.foc.business.division.DivisionDesc;
import com.foc.business.downloadableContent.DownloadableContentDesc;
import com.foc.business.printing.PrnContextDesc;
import com.foc.business.printing.PrnLayoutDesc;
import com.foc.desc.FocModule;
import com.foc.menu.FMenuList;

public class BusinessModule extends FocModule{

  public final static String MODULE_NAME  = "BUSINESS";
  public final static int    MODULE_ID    = 1004;

  public static final int MODULE_ID_LAST_WITH_BOTH_ADDRESSES_DIFFERENT_EDITABLE = 1002;
  
	private boolean multiCompany = false;
	
	public BusinessModule(){
		multiCompany = false;
	}
	
	public void dispose(){
		
	}
	
	public boolean isMultiCompany(){
		return multiCompany;
	}

	public void setMultiCompany(boolean multiComp){
		multiCompany = multiComp;
	}

	@Override
	public void declareFocObjectsOnce() {
//		Application app = Globals.getApp();
		FocVersion.addVersion(MODULE_NAME, "BUS1.0" , MODULE_ID);
		
		if(isMultiCompany()){
			declareFocDescClass(CompanyDesc.class);
			declareFocDescClass(UserCompanyRightsDesc.class);
			
			declareFocDescClass(PrnLayoutDesc.class);
			declareFocDescClass(PrnContextDesc.class);
			
			declareFocDescClass(BusinessConfigDesc.class);
			declareFocDescClass(AdrBookPartyDesc.class);
			declareFocDescClass(ContactDesc.class);
			declareFocDescClass(PositionDesc.class);
			declareFocDescClass(CountryDesc.class);
			declareFocDescClass(RegionDesc.class);
			declareFocDescClass(CityDesc.class);
			declareFocDescClass(DepartmentDesc.class);
			declareFocDescClass(DivisionDesc.class);
			declareFocDescClass(DownloadableContentDesc.class);
		}
	}

	public void addApplicationMenu(FMenuList menuList) {
	}

	public void addConfigurationMenu(FMenuList menuList) {
	}

	public void afterAdaptDataModel() {
	}

	public void afterApplicationEntry() {
	}

	public void afterApplicationLogin() {
	}

	public void beforeAdaptDataModel() {
	}

	public void declare() {
    Application app = Globals.getApp();
    app.declareModule(this);      
	}
	
	private static BusinessModule businessModule = null;
	public static BusinessModule getInstance(){
		if(businessModule == null){
			businessModule = new BusinessModule();
		}
		return businessModule;
	}
}
