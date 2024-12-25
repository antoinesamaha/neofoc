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
package com.foc.link;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;

@SuppressWarnings("serial")
public class FocLinkOutRightsDetails extends FocObject {

	public FocLinkOutRightsDetails(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public FocDesc getTableDesc(){
		return getPropertyDesc(FocLinkOutRightsDetailsDesc.FLD_TABLE_NAME);
	}
	
	public void setTableDesc(FocDesc desc){
		setPropertyDesc(FocLinkOutRightsDetailsDesc.FLD_TABLE_NAME, desc);
	}
	
	public String getFieldName(){
		return getPropertyString(FocLinkOutRightsDetailsDesc.FLD_FIELD_NAME);
	}
	
	public void setFieldName(String fieldName){
		setPropertyString(FocLinkOutRightsDetailsDesc.FLD_FIELD_NAME, fieldName);
	}
}
