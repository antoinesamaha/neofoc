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

import com.foc.admin.FocUserDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBlobLongField;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class FocLinkOutBoxDesc extends FocDesc {
	
	public static final String DB_TABLE_NAME = "FOC_LINK_OUT_LOG";
	
	public static final int    FLD_FROM_USER        = 1;
	public static final int    FLD_STORAGE          = 2;
	public static final int    FLD_TRANSACTION_TYPE = 3;
	public static final int    FLD_XML_MESSAGE      = 4;
	public static final int    FLD_DETAILS_LIST     = 5;
	
	public FocLinkOutBoxDesc() {
		super(FocLinkOutBox.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		
		addReferenceField();
		
		FObjectField oFld = new FObjectField(FocUserDesc.DB_TABLE_NAME, "From User", FLD_FROM_USER, FocUserDesc.getInstance());
		addField(oFld);
		
		FStringField   cFld = new FStringField("STORAGE", "Storage", FLD_STORAGE, false, 30);
		addField(cFld);
		
		FIntField    iFld = new FIntField("TRANSACTION_TYPE", "Transaction Type", FLD_TRANSACTION_TYPE, false, 5);
		addField(iFld);
		
		FBlobStringField bFld = new FBlobStringField("XML_MESSAGE", "Xml Message", FLD_XML_MESSAGE, false, 3, 100);
		addField(bFld);
		
	}
	
	@Override
  public FocList newFocList(){
  	FocList list = super.newFocList();
  	if(list != null){
	  	list.setDirectImpactOnDatabase(true);
	  	list.setDirectlyEditable(false);
  	}
  	return list;
  }
	
  public static FocList getList() {
	  return getInstance().getFocList(FocList.LOAD_IF_NEEDED);
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
	public static FocLinkOutBoxDesc getInstance() {
		return (FocLinkOutBoxDesc) getInstance(DB_TABLE_NAME, FocLinkOutBoxDesc.class);    
	}

}
