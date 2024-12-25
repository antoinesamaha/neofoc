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
package com.foc.business.workflow;

import com.foc.admin.FocUser;
import com.foc.business.department.Department;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

@SuppressWarnings("serial")
public class WFOperator extends FocObject {

	private boolean duplicatedForUserArrayOfTitles = false;
	
	public WFOperator(){
		this(new FocConstructor(WFOperatorDesc.getInstance(), null));
	}
	
	public WFOperator(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	public FocUser getUser(){
		return (FocUser) getPropertyObject(WFOperatorDesc.FLD_USER);
	}

	public void setUser(FocUser title){
		setPropertyObject(WFOperatorDesc.FLD_USER, title);
	}
	
	public WFTitle getTitle(){
		return (WFTitle) getPropertyObject(WFOperatorDesc.FLD_TITLE);
	}
	
	public void setTitle(WFTitle title){
		setPropertyObject(WFOperatorDesc.FLD_TITLE, title);
	}
	
	public Department getDepartment(){
		return (Department) getPropertyObject(WFOperatorDesc.FLD_DEPARTMENT);
	}

	public WFSite getArea(){
		return (WFSite) getPropertyObject(WFOperatorDesc.FLD_AREA);
	}

	public void setArea(WFSite area){
		setPropertyObject(WFOperatorDesc.FLD_AREA, area);
	}
	
	public void resetUserOperatorArray(){
		if(!isDuplicatedForUserArrayOfTitles()){
			FocUser user = getUser();
//USERREFACTOR
//			if(user != null){
//				user.dispose_OperatorsArrayForThisUserAllAreas();
//			}
		}
	}

	public boolean isDuplicatedForUserArrayOfTitles() {
		return duplicatedForUserArrayOfTitles;
	}

	public void setDuplicatedForUserArrayOfTitles(boolean duplicatedForUserArrayOfTitles) {
		this.duplicatedForUserArrayOfTitles = duplicatedForUserArrayOfTitles;
	}
}
