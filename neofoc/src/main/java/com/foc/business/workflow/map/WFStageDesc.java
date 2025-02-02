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

import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FStringField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class WFStageDesc extends FocDesc {
	public static final int FLD_NAME        = FField.FLD_NAME;
	public static final int FLD_DESCRIPTION = FField.FLD_DESCRIPTION;
	public static final int FLD_IS_APPROVAL = 1;
	public static final int FLD_IS_CLOSE    = 2;
	
	public static final String DB_TABLE_NAME = "WF_STAGE";
  
	public WFStageDesc(){
		super(WFStage.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
		setGuiBrowsePanelClass(WFStageGuiBrowsePanel.class);
		setGuiDetailsPanelClass(WFStageGuiDetailsPanel.class);
		addReferenceField();
		
		FStringField nameField = addNameField();
		nameField.setSize(200);
		addDescriptionField();
		
		FBoolField bFld = new FBoolField("IS_APPROVAL", "In Approval", FLD_IS_APPROVAL, false);
		addField(bFld);
		
		bFld = new FBoolField("IS_CLOSE", "Is CLose", FLD_IS_CLOSE, false);
		addField(bFld);
  }
	
	public static FObjectField newFieldStage(String name, int fieldID){
		FObjectField objFld = new FObjectField(name, "Stage", fieldID, WFStageDesc.getInstance());
		objFld.setSelectionList(WFStageDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFStageDesc.FLD_NAME);
		objFld.setDisplayField(WFStageDesc.FLD_NAME);
		return objFld;
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
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, WFStageDesc.class);    
  }
}
