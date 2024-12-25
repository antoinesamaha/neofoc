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
package com.foc.business.department;

import com.foc.desc.field.FBoolField;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class DepartmentList extends FocList{
	
	public DepartmentList(boolean endDepartmentsOnly){
		super(new FocLinkSimple(DepartmentDesc.getInstance()));
		if(endDepartmentsOnly){
			FBoolField bFld = (FBoolField) DepartmentDesc.getInstance().getFieldByID(DepartmentDesc.FLD_END_DEPARTMENT);
			getFilter().putAdditionalWhere("END_DEP", bFld.getName()+"=1");
		}
	  setDirectlyEditable(false);
	  setDirectImpactOnDatabase(true);
	}
	
	private static DepartmentList endDepartmentList = null;
	@Deprecated
	public static DepartmentList getInstance_ForEndDepartments(){
		if(endDepartmentList == null){
			endDepartmentList = new DepartmentList(true);
		}
		return endDepartmentList;
	}
}
