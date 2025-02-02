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

import com.foc.admin.FocUserDesc;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceStringField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class WFSignatureDesc extends FocDesc {
	public static final int FLD_NAME               = FField.FLD_NAME;
	public static final int FLD_DESCRIPTION        = FField.FLD_DESCRIPTION;
	
	public static final int FLD_MAP                            =  1;
	public static final int FLD_TARGET_STAGE                   =  2;
	public static final int FLD_PREVIOUS_STAGE                 =  3;
	public static final int FLD_CONDITION_TO_REQUIRE_SIGNATURE =  4;
	public static final int FLD_USER                           =  6;
	public static final int FLD_TITLE_FIRST                    = 30;	
	public static final int FLD_TITLE_COUNT                    =  3;
	public static final int FLD_TRANSACTION_CONTEXT            = 50;
	public static final int FLD_TRANSACTION_VIEW               = 51;
	public static final int FLD_RejectHidden                   = 52;
	public static final int FLD_SignCaption                    = 53;
	public static final int FLD_RejectCaption                  = 54;
	
	public static final String DB_TABLE_NAME = "WF_SIGNATURE";
  
	public WFSignatureDesc(){
		super(WFSignature.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		setGuiBrowsePanelClass(WFSignatureGuiBrowsePanel.class);
		addReferenceField();
	
		addOrderField();
		
    FMultipleChoiceStringField mField = new FMultipleChoiceStringField("TRANSACTION_CONTEXT", "Transaction Context", FLD_TRANSACTION_CONTEXT, false, 20);
		addField(mField);
		
		mField = new FMultipleChoiceStringField("TRANSACTION_VIEW", "Transaction View", FLD_TRANSACTION_VIEW, false, 20);
		addField(mField);
		
		FObjectField objFld = new FObjectField("MAP", "Map", FLD_MAP, false, WFMapDesc.getInstance(), "MAP_", this, WFMapDesc.FLD_SIGNATURE_LIST);
		objFld.setSelectionList(WFMapDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFMapDesc.FLD_NAME);
		objFld.setDisplayField(WFMapDesc.FLD_NAME);
		objFld.setMandatory(true);
		addField(objFld);

		addTitleFields(this, FLD_TITLE_FIRST);
		//objFld = (FObjectField) getFieldByID(FLD_TITLE_FIRST);
		//objFld.setMandatory(true);

		objFld = new FObjectField("TARGET_STAGE", "Target|Stage", FLD_TARGET_STAGE, false, WFStageDesc.getInstance(), "TARGET_STAGE_");
		objFld.setSelectionList(WFStageDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFStageDesc.FLD_NAME);
		objFld.setDisplayField(WFStageDesc.FLD_NAME);
		objFld.setMandatory(true);
		addField(objFld);

		objFld = new FObjectField("PREVIOUS_STAGE", "Previous|Stage", FLD_PREVIOUS_STAGE, false, WFStageDesc.getInstance(), "PREVIOUS_STAGE_");
		objFld.setSelectionList(WFStageDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFStageDesc.FLD_NAME);
		objFld.setDisplayField(WFStageDesc.FLD_NAME);
		addField(objFld);
		
		FStringField formulaField = new FStringField("CONDITION_TO_REQUIRE_SIGNATURE", "Condition to require", FLD_CONDITION_TO_REQUIRE_SIGNATURE, false, 500);
		addField(formulaField);
		
		FStringField signField = new FStringField("SignCaption", "Signature Caption", FLD_SignCaption, false, 500);
		addField(signField);
		
		FStringField rejectField = new FStringField("RejectCaption", "Reject Caption", FLD_RejectCaption, false, 500);
		addField(rejectField);
		
		FBoolField rejectHiddenField = new FBoolField("RejectHidden", "Reject Hidden", FLD_RejectHidden, false);
		addField(rejectHiddenField);
		
		FObjectField fObjectFld = new FObjectField("FOC_USER", "User", FLD_USER, false, FocUserDesc.getInstance(), "USER_");
    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    addField(fObjectFld);
  }

	public static void addTitleFields(FocDesc focDesc, int titleFirst){
		for(int i=0; i<FLD_TITLE_COUNT; i++){
			int index = i+1;
			if(focDesc.getFieldByID(titleFirst+i) == null){
				FObjectField objFld = new FObjectField("TITLE_"+index, "Title "+index, titleFirst+i, false, WFTitleDesc.getInstance(), "TITLE_"+index+"_");
				objFld.setSelectionList(WFTitleDesc.getList(FocList.NONE));
				objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
				objFld.setComboBoxCellEditor(WFTitleDesc.FLD_NAME);
				objFld.setDisplayField(WFTitleDesc.FLD_NAME);
				focDesc.addField(objFld);
			}
		}
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
			w = (WFMap) list.searchByPropertyStringValue(WFSignatureDesc.FLD_NAME, name);
		}
		return w;
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, WFSignatureDesc.class);    
  }
}
