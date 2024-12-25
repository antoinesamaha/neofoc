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

import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class UserCompanyRightsDesc extends FocDesc{
  
  public static final int FLD_USER         = 1;
  public static final int FLD_COMPANY      = 2;
  public static final int FLD_ACCESS_RIGHT = 3;
  
  public static final int ACCESS_RIGHT_NONE        = 0;
  public static final int ACCESS_RIGHT_READ_ONLY   = 1;
  public static final int ACCESS_RIGHT_READ_WRITE = 2;
  
  public static final String DB_TABLE_NAME = "COMPANY_USER";
  
  public UserCompanyRightsDesc() {
    super(UserCompanyRights.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    setGuiBrowsePanelClass(UserCompanyRightsGuiBrowsePanel.class);
    
    addReferenceField(); 
    
    FObjectField fObjectFld = new FObjectField("USER", "User", FLD_USER, false, FocUser.getFocDesc(), "USER_", this, FocUserDesc.FLD_COMPANY_RIGHTS_LIST);
    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    addField(fObjectFld);
    
    //USERRIGHTS
    fObjectFld = new FObjectField("COMPANY", "Company", FLD_COMPANY, false, CompanyDesc.getInstance(), "COMPANY_");
    fObjectFld.setComboBoxCellEditor(CompanyDesc.FLD_NAME);
    fObjectFld.setDisplayField(CompanyDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    addField(fObjectFld);
    
    FMultipleChoiceField multiFld = new FMultipleChoiceField("ACCESS_RIGHT", "Access Right", FLD_ACCESS_RIGHT, false, 2);
    multiFld.addChoice(ACCESS_RIGHT_NONE, "-none-");
    multiFld.addChoice(ACCESS_RIGHT_READ_ONLY, "Read Only");
    multiFld.addChoice(ACCESS_RIGHT_READ_WRITE, "Read Write");
    addField(multiFld);
  }
  
  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_USER);
      list.setListOrder(order);
    }
    return list;
  }
  
  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, UserCompanyRightsDesc.class);    
  }
}
