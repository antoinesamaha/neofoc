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

import com.foc.business.company.CompanyDesc;
import com.foc.business.printing.PrnContextDesc;
import com.foc.menu.FMenuAction;
import com.foc.menu.FMenuItem;
import com.foc.menu.FMenuList;

public class BusinessMenu {
  public static FMenuItem addCompanyMenu(FMenuList menuList){
    FMenuItem companyMenu = new FMenuItem("Company", 'C', "COMPANY", new FMenuAction(CompanyDesc.getInstance(), true));
    menuList.addMenu(companyMenu);
    return companyMenu;
  }
  
  public static FMenuItem addReportLayoutMenu(FMenuList menuList){
    FMenuItem companyMenu = new FMenuItem("Report Layout", 'L', "REPORT_LAYOUTS", new FMenuAction(PrnContextDesc.getInstance(), true));
    menuList.addMenu(companyMenu);
    return companyMenu;
  }
}
