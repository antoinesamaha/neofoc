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
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

@SuppressWarnings("serial")
public class UserCompanyRights extends FocObject{
	
  
  public UserCompanyRights(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
    
  public void setCompany(Company company){
  	setPropertyObject(UserCompanyRightsDesc.FLD_COMPANY, company);
  }

  public Company getCompany(){
  	return (Company) getPropertyObject(UserCompanyRightsDesc.FLD_COMPANY);
  }

  public void setUser(FocUser user){
  	setPropertyObject(UserCompanyRightsDesc.FLD_USER, user);
  }

  public FocUser getUser(){
  	return (FocUser) getPropertyObject(UserCompanyRightsDesc.FLD_USER);
  }

  public int getAccessRight(){
  	return getPropertyMultiChoice(UserCompanyRightsDesc.FLD_ACCESS_RIGHT);
  }

  public void setAccessRight(int accRight){
  	setPropertyMultiChoice(UserCompanyRightsDesc.FLD_ACCESS_RIGHT, accRight);
  }

  public void copy(UserCompanyRights source) {
  	setUser(source.getUser());
  	setCompany(source.getCompany());
  	setAccessRight(source.getAccessRight());
  }
}
