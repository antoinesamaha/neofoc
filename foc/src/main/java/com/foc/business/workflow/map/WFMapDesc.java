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
package com.foc.business.workflow.map;

import com.foc.business.workflow.WFTitleDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class WFMapDesc extends FocDesc {
	public static final int FLD_NAME               = FField.FLD_NAME;
	public static final int FLD_DESCRIPTION        = FField.FLD_DESCRIPTION;
	
	public static final int FLD_TITLE_INITIAL_EDIT  = 1;
	public static final int FLD_STAGE_OF_LOCK_BEGIN = 2;
	
	public static final int FLD_SIGNATURE_LIST     = 10;
	
	public static final String DB_TABLE_NAME = "WF_MAP";
  
	public WFMapDesc(){
		super(WFMap.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
		setGuiBrowsePanelClass(WFMapGuiBrowsePanel.class);
		setGuiDetailsPanelClass(WFMapGuiDetailsPanel.class);
		addReferenceField();
		
		addNameField();
		addDescriptionField();
		
		FObjectField objFld = new FObjectField("TITLE_INITIAL_EDIT", "Title that can initially edit", FLD_TITLE_INITIAL_EDIT, WFTitleDesc.getInstance());
		objFld.setSelectionList(WFTitleDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setDisplayField(WFTitleDesc.FLD_NAME);
		addField(objFld);
		
		objFld = new FObjectField("STAGE_OF_LOCK_BEGIN", "Stage of lock begin", FLD_STAGE_OF_LOCK_BEGIN, WFStageDesc.getInstance());
		objFld.setSelectionList(WFStageDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		addField(objFld);
		
		/*
		FObjectField objFld = new FObjectField("CREATION_TITLE", "Creation Title", FLD_CREATION_TITLE, false, WFTitleDesc.getInstance(), "CREATION_TITLE_");
		objFld.setSelectionList(WFTitleDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFTitleDesc.FLD_NAME);
		objFld.setDisplayField(WFTitleDesc.FLD_NAME);
		objFld.setMandatory(true);
		addField(objFld);

		objFld = new FObjectField("MODIFICATION_TITLE", "Modification Title", FLD_MODIFICATION_TITLE, false, WFTitleDesc.getInstance(), "MODIFICATION_TITLE_");
		objFld.setSelectionList(WFTitleDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFTitleDesc.FLD_NAME);
		objFld.setDisplayField(WFTitleDesc.FLD_NAME);
		objFld.setMandatory(true);
		addField(objFld);

		objFld = new FObjectField("CANCELLATION_TITLE", "Cancellation Title", FLD_CANCELATION_TITLE, false, WFTitleDesc.getInstance(), "CANCELATION_TITLE_");
		objFld.setSelectionList(WFTitleDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFTitleDesc.FLD_NAME);
		objFld.setDisplayField(WFTitleDesc.FLD_NAME);
		objFld.setMandatory(true);
		addField(objFld);
		*/
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
      FocListOrder order = new FocListOrder(FLD_NAME);
      list.setListOrder(order);
    }
    return list;
  }
	
	public static WFMap getArea_ForName(String name){
		FocList list = getList(FocList.LOAD_IF_NEEDED);
		WFMap  w    = null;
		if(list != null){
			w = (WFMap) list.searchByPropertyStringValue(WFMapDesc.FLD_NAME, name);
		}
		return w;
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, WFMapDesc.class);    
  }
}
