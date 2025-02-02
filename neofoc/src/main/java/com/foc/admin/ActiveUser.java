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
// EXTERNAL PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// LIST
// DESCRIPTION

/*
 * Created on 20-May-2005
 */
package com.foc.admin;

import java.sql.Date;

import com.foc.business.company.Company;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFTitle;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class ActiveUser extends FocObject{
  
  public static final int VIEW_READ_ONLY = 2;  
  
  public ActiveUser(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public void dispose(){
    super.dispose();
  }
  
  public FocUser getUser(){
    return (FocUser) getPropertyObject(ActiveUserDesc.FLD_USER);
  }
  
  public void setUser(FocUser user){
  	setPropertyObject(ActiveUserDesc.FLD_USER, user);
  }
  
  public Company getUserCompany(){
    return (Company) getPropertyObject(ActiveUserDesc.FLD_COMPANY);
  }
  
  public void setUserCompany(Company company){
  	setPropertyObject(ActiveUserDesc.FLD_COMPANY, company);
  }
  
  public WFSite getUserSite(){
    return (WFSite) getPropertyObject(ActiveUserDesc.FLD_SITE);
  }
  
  public void setUserSite(WFSite site){
  	setPropertyObject(ActiveUserDesc.FLD_SITE, site);
  }
  
  public WFTitle getUserTitle(){
    return (WFTitle) getPropertyObject(ActiveUserDesc.FLD_TITLE);
  }
  
  public void setUserTitle(WFTitle site){
  	setPropertyObject(ActiveUserDesc.FLD_TITLE, site);
  }
  
  public Date getLastHeartBeat(){
    return (Date) getPropertyDate(ActiveUserDesc.FLD_LAST_HEART_BEAT);
  }
  
  public void setLastHeartBeat(Date date){
  	setPropertyDate(ActiveUserDesc.FLD_LAST_HEART_BEAT, date);
  }

	public String getOrigin() {
		return getPropertyString(ActiveUserDesc.FLD_ORIGIN);
	}

	public void setOrigin(String value) {
		setPropertyString(ActiveUserDesc.FLD_ORIGIN, value);
	}
	
  public static FocDesc getFocDesc() {
    return FocGroupDesc.getInstance();
  }
}
