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

import com.foc.desc.FocDesc;
import com.foc.list.FocList;

public class FocLinkInRightsDesc extends FocDesc {

	public static final String DB_TABLE_NAME = "FOC_LINK_IN_RIGHTS";

	public static final int    FLD_DETAILS_LIST = 1;
	
	public FocLinkInRightsDesc() {
		super(FocLinkInRights.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);

		addReferenceField();
		addNameField(true);
	}

	public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    return list;
  }
	
	public static FocLinkInRightsDesc getInstance() {
		return (FocLinkInRightsDesc) getInstance(DB_TABLE_NAME, FocLinkInRightsDesc.class);    
	}
}
