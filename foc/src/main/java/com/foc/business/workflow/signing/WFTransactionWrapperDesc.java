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
package com.foc.business.workflow.signing;

import com.foc.business.workflow.WFSiteDesc;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.business.workflow.map.WFStageDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FStringField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class WFTransactionWrapperDesc extends FocDesc {
	public static final int FLD_TRANSACTION_TYPE          = 1;
	public static final int FLD_TRANSACTION_CODE          = 2;
	public static final int FLD_TRANSACTION_DESCRIPTION   = 3;
	public static final int FLD_TRANSACTION_CURRENT_STAGE = 4;
	public static final int FLD_TRANSACTION_AREA          = 5;
	public static final int FLD_ORIGINAL_TRANSACTION      = 6;
	public static final int FLD_TITLE                     = 7;
	public static final int FLD_ON_BEHALF_OF              = 8;
	public static final int FLD_TRANSACTION_DATE          = 9;
	
	public static final String DB_TABLE_NAME = "WF_TRANSACTION_WRAPPER";
  
	public WFTransactionWrapperDesc(){
		super(WFTransactionWrapper.class, FocDesc.NOT_DB_RESIDENT, DB_TABLE_NAME, false);
		setGuiBrowsePanelClass(WFTransactionWrapperGuiBrowsePanel.class);
		addReferenceField();

  	FObjectField objectField = new FObjectField("TRANSACTION", "TRANSACTION", FLD_ORIGINAL_TRANSACTION, null);
  	objectField.setWithList(false);
  	addField(objectField);
		
		FStringField cFld = new FStringField("TYPE", "Type", FLD_TRANSACTION_TYPE, false, 60);
		addField(cFld);

		cFld = new FStringField("CODE", "Code", FLD_TRANSACTION_CODE, false, 20);
		addField(cFld);

		cFld = new FStringField("DESCRIPTION", "Description", FLD_TRANSACTION_DESCRIPTION, false, 1000);
		addField(cFld);

		FDateField dFld = new FDateField("TRANSACTION_DATE", "Date", FLD_TRANSACTION_DATE, false);
		addField(dFld);
		
		FObjectField oFld = new FObjectField("CURRENT_STAGE", "Current Stage", FLD_TRANSACTION_CURRENT_STAGE, WFStageDesc.getInstance());
		oFld.setSelectionList(WFStageDesc.getList(FocList.NONE));
		oFld.setComboBoxCellEditor(WFStageDesc.FLD_NAME);
		oFld.setDisplayField(WFStageDesc.FLD_NAME);
		addField(oFld);
		
		oFld = new FObjectField("AREA", "Area", FLD_TRANSACTION_AREA, WFSiteDesc.getInstance());
//		oFld.setSelectionList(WFSiteDesc.getList(FocList.NONE));
		oFld.setWithList(false);
		oFld.setComboBoxCellEditor(WFSiteDesc.FLD_NAME);
		oFld.setDisplayField(WFSiteDesc.FLD_NAME);
		addField(oFld);
		
		oFld = new FObjectField("TITLE", "Title", FLD_TITLE, WFTitleDesc.getInstance());
		oFld.setSelectionList(WFTitleDesc.getList(FocList.NONE));
		addField(oFld);

		addField(newOnBehalfOfField(FLD_ON_BEHALF_OF));
  }
	
	public static FField newOnBehalfOfField(int id){
		//It is like a boolean but the multiple choice assures that we show in the Table the text "On Behalf Of"
		FMultipleChoiceField mFld = new FMultipleChoiceField("ON_BEHALF_OF", "Ob Behalf of", id, false, 2);
		mFld.addChoice(0, "");
		mFld.addChoice(1, "On behalf of");
		return mFld;
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_TRANSACTION_CODE);
      list.setListOrder(order);
    }
    return list;
  }
	
	public static WFTransactionWrapper getArea_ForCode(String name){
		FocList list = getList(FocList.LOAD_IF_NEEDED);
		WFTransactionWrapper  w    = null;
		if(list != null){
			w = (WFTransactionWrapper) list.searchByPropertyStringValue(WFTransactionWrapperDesc.FLD_TRANSACTION_CODE, name);
		}
		return w;
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, WFTransactionWrapperDesc.class);    
  }
  
}
