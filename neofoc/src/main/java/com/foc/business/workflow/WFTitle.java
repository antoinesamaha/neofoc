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

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

@SuppressWarnings("serial")
public class WFTitle extends FocObject {
	
	public static final String TITLE_SUPERUSER = "Super User"; 
	public static final String TITLE_GM        = "GM";
	
	public WFTitle(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	public boolean isProjectSpecific(){
		return getPropertyBoolean(WFTitleDesc.FLD_IS_PROJECT_SPECIFIC);
	}
	
	public void setProjectSpecific(boolean sys){
		setPropertyBoolean(WFTitleDesc.FLD_IS_PROJECT_SPECIFIC, sys);
	}
	
	public String getUserDataPathFromProjWBS(){
		return getPropertyString(WFTitleDesc.FLD_USER_DATAPATH_FROM_PROJ_WBS);
	}
	
	public void setUserDataPathFromProjWBS(String path){
		setPropertyString(WFTitleDesc.FLD_USER_DATAPATH_FROM_PROJ_WBS, path);
	}
}
