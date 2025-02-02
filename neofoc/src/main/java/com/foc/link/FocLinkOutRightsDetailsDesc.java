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
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FDescFieldStringBased;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;

public class FocLinkOutRightsDetailsDesc extends FocDesc {

	public static final String DB_TABLE_NAME  = "FOC_LINK_OUT_RIGHTS_DETAILS";
	
	public static final int    FLD_TABLE_NAME = 1;
	public static final int    FLD_FIELD_NAME = 2;
	public static final int    FLD_MASTER     = 3;
	
	public FocLinkOutRightsDetailsDesc() {
		super(FocLinkOutRightsDetails.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
		
		addReferenceField();

		FField ffld = new FDescFieldStringBased("TABLE_NAME", "Table", FLD_TABLE_NAME, true);
		ffld.setLockValueAfterCreation(true);
		addField(ffld);
		
		ffld = new FStringField("FIELD_NAME", "Field Name", FLD_FIELD_NAME, true, 50);
		addField(ffld);
		
		ffld = new FObjectField("RIGHTS_MASTER", "Rights Master", FLD_MASTER, FocLinkOutRightsDesc.getInstance(), this, FocLinkOutRightsDesc.FLD_DETAILS_LIST);
		ffld.setKey(true);
		((FObjectField) ffld).setSelectionList(null);
		((FObjectField) ffld).setWithList(false);
		addField(ffld);
	}
	
  @Override
  protected void afterConstruction() {
  	super.afterConstruction();

  	FDescFieldStringBased descField = (FDescFieldStringBased) getFieldByID(FLD_TABLE_NAME);
  	descField.fillWithAllDeclaredFocDesc();
  }
  
	public static FocLinkOutRightsDetailsDesc getInstance() {
		return (FocLinkOutRightsDetailsDesc) getInstance(DB_TABLE_NAME, FocLinkOutRightsDetailsDesc.class);    
	}

}
