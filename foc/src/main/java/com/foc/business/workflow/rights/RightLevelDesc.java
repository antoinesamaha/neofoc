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
package com.foc.business.workflow.rights;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class RightLevelDesc extends FocDesc implements RightLevelConst {
  
  public static final String DB_TABLE_NAME = "WF_RIGHT_LEVEL";
  
	public RightLevelDesc(){
		super(RightLevel.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		setGuiBrowsePanelClass(RightLevelGuiBrowsePanel.class);
		setGuiDetailsPanelClass(RightLevelGuiDetailsPanel.class);
		addReferenceField();
		
		FStringField cFld = addNameField();
		cFld.setMandatory(true);
		
		FBoolField bFld = new FBoolField("READ_RIGHT", "Read", FLD_READ, false); 
		addField(bFld);

		bFld = new FBoolField("INSERT_RIGHT", "Insert", FLD_INSERT, false); 
		addField(bFld);

		bFld = new FBoolField("DELETE_DRAFT_RIGHT", "Delete|Draft", FLD_DELETE_DRAFT, false); 
		addField(bFld);

		bFld = new FBoolField("DELETE_APPROVED_RIGHT", "Delete|Approved", FLD_DELETE_APPROVED, false); 
		addField(bFld);

		bFld = new FBoolField("MODDIFY_DRAFT_RIGHT", "Modify|Draft", FLD_MODIFY_DRAFT, false); 
		addField(bFld);

		bFld = new FBoolField("MODDIFY_APPROVED_RIGHT", "Modify|Approved", FLD_MODIFY_APPROVED, false); 
		addField(bFld);

		bFld = new FBoolField("PRINT_DRAFT_RIGHT", "Print|Draft", FLD_PRINT_DRAFT, false); 
		addField(bFld);

		bFld = new FBoolField("PRINT_APPROVE_RIGHT", "Print|Approved", FLD_PRINT_APPROVE, false); 
		addField(bFld);

		bFld = new FBoolField("APPROVE_RIGHT", "Approve", FLD_APPROVE, false); 
		addField(bFld);

		bFld = new FBoolField("CANCEL_RIGHT", "Cancel", FLD_CANCEL, false); 
		addField(bFld);

		bFld = new FBoolField("CLOSE_RIGHT", "Close", FLD_CLOSE, false); 
		addField(bFld);

		bFld = new FBoolField("RESET_TO_PROPOSAL", "Reset to Proposal", FLD_RESET_TO_PROPOSAL, false); 
		addField(bFld);
		
		bFld = new FBoolField("RESET_TO_APPROVED", "Reset to Approved", FLD_RESET_TO_APPROVED, false); 
		addField(bFld);
		
		bFld = new FBoolField("MODIFY_CODE_DRAFT_RIGHT", "Modify|Code|Draft", FLD_MODIFY_CODE_DRAFT, false); 
		addField(bFld);
		
		bFld = new FBoolField("MODIFY_CODE_APPROVED_RIGHT", "Modify|Code|Approved", FLD_MODIFY_CODE_APPROVED, false); 
		addField(bFld);
		
		bFld = new FBoolField("UNDO_SIGNATURE", "Undo|Signature", FLD_UNDO_SIGNATURE, false); 
		addField(bFld);		
		
		bFld = new FBoolField("MODIFY_SIGNATURE_STAGE", "Modify Signature Stage", FLD_MODIFY_SIGNATRUE_STAGE, false); 
		addField(bFld);
		
		bFld = new FBoolField("VIEW_LOG", "View Log", FLD_VIEW_LOG, false); 
		addField(bFld);
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
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FField.FLD_NAME);
      list.setListOrder(order);
    }
    return list;
  }
	
	public static RightLevel getRightLevelByName(String name){
		RightLevel rightLevel = null;
		FocList list = getList(FocList.LOAD_IF_NEEDED);
		rightLevel = (RightLevel) list.searchByPropertyStringValue(FField.FLD_NAME, name);
		return rightLevel;
	}

	public RightLevel findOrAddRightLevel(String rightLevelName) {
		RightLevel rightLevel = getRightLevelByName(rightLevelName);
		if(rightLevel == null){
			rightLevel = (RightLevel) RightLevelDesc.getInstance().getFocList().newEmptyItem();
			rightLevel.setName(rightLevelName);
			rightLevel.setAllowApprove(true);
			rightLevel.setAllowCancel(true);
			rightLevel.setAllowClose(true);
			rightLevel.setAllowDeleteApprove(true);
			rightLevel.setAllowDeleteDraft(true);
			rightLevel.setAllowInsert(true);
			rightLevel.setAllowModifyApproved(true);
			rightLevel.setAllowModifyCodeApproved(true);
			rightLevel.setAllowModifyCodeDraft(true);
			rightLevel.setAllowModifyDraft(true);
			rightLevel.setAllowPrintApprove(true);
			rightLevel.setAllowPrintDraft(true);
			rightLevel.setAllowRead(true);
			rightLevel.setAllowUndoSignature(true);
			rightLevel.validate(true);
		}
		return rightLevel;
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static RightLevelDesc getInstance() {
    return (RightLevelDesc) getInstance(DB_TABLE_NAME, RightLevelDesc.class);    
  }

	public static RightLevel getGuestRightLevelOrCreateIfNotExist() {
		String rightLevelName = "Guest";
		RightLevel rightLevel = getRightLevelByName(rightLevelName);
		if(rightLevel == null){
			rightLevel = (RightLevel) RightLevelDesc.getInstance().getFocList().newEmptyItem();
			rightLevel.setName(rightLevelName);
			rightLevel.setAllowApprove(true);
			rightLevel.setAllowCancel(true);
			rightLevel.setAllowClose(true);
			rightLevel.setAllowDeleteApprove(true);
			rightLevel.setAllowDeleteDraft(true);
			rightLevel.setAllowInsert(true);
			rightLevel.setAllowModifyApproved(true);
			rightLevel.setAllowModifyCodeApproved(true);
			rightLevel.setAllowModifyCodeDraft(true);
			rightLevel.setAllowModifyDraft(true);
			rightLevel.setAllowPrintApprove(true);
			rightLevel.setAllowPrintDraft(true);
			rightLevel.setAllowRead(true);
			rightLevel.setAllowUndoSignature(true);
			rightLevel.validate(true);
		}
		return rightLevel;
	}
}
