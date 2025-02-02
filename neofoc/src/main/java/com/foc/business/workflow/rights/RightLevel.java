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

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

public class RightLevel extends FocObject implements RightLevelConst {

	public RightLevel(){
		this(new FocConstructor(RightLevelDesc.getInstance(), null));
	}
	
	public RightLevel(FocConstructor constr){
		super(constr);
		newFocProperties();
	}

	public void setAllowViewLog(boolean allow){
	  setPropertyBoolean(FLD_VIEW_LOG, allow); 
	}

	public boolean isAllowViewLog(){
	  return getPropertyBoolean(FLD_VIEW_LOG); 
	}

	public boolean isAllowRead(){
	  return getPropertyBoolean(FLD_READ); 
	}
	
	public void setAllowRead(boolean allowRead){
		setPropertyBoolean(FLD_READ, allowRead); 
	}

	public boolean isAllowDeleteDraft(){
	  return getPropertyBoolean(FLD_DELETE_DRAFT); 
	}
	
	public void setAllowDeleteDraft(boolean allowDeleteDraft){
		setPropertyBoolean(FLD_DELETE_DRAFT, allowDeleteDraft); 
	}

	public boolean isAllowDeleteApprove(){
	  return getPropertyBoolean(FLD_DELETE_APPROVED); 
	}
	
	public void setAllowDeleteApprove(boolean allowDeleteApprove){
	  setPropertyBoolean(FLD_DELETE_APPROVED, allowDeleteApprove); 
	}

	public boolean isAllowModifyDraft(){
	  return getPropertyBoolean(FLD_MODIFY_DRAFT); 
	}
	
	public void setAllowModifyDraft(boolean allowModifyDraft){
	  setPropertyBoolean(FLD_MODIFY_DRAFT, allowModifyDraft); 
	}

	public boolean isAllowModifyApproved(){
	  return getPropertyBoolean(FLD_MODIFY_APPROVED); 
	}
	
	public void setAllowModifyApproved(boolean allowModifyApproved){
	  setPropertyBoolean(FLD_MODIFY_APPROVED, allowModifyApproved); 
	}

	public boolean isAllowInsert(){
	  return getPropertyBoolean(FLD_INSERT); 
	}
	
	public void setAllowInsert(boolean allowInsert){
	  setPropertyBoolean(FLD_INSERT, allowInsert); 
	}
	
	public boolean isAllowCancel(){
	  return getPropertyBoolean(FLD_CANCEL); 
	}
	
	public void setAllowCancel(boolean allowCancel){
	  setPropertyBoolean(FLD_CANCEL, allowCancel); 
	}

	public boolean isAllowClose(){
	  return getPropertyBoolean(FLD_CLOSE); 
	}
	
	public void setAllowClose(boolean allowClose){
	  setPropertyBoolean(FLD_CLOSE, allowClose); 
	}

	public boolean isAllowApprove(){
	  return getPropertyBoolean(FLD_APPROVE); 
	}
	
	public void setAllowApprove(boolean allowApprove){
	  setPropertyBoolean(FLD_APPROVE, allowApprove); 
	}
	
	public boolean isAllowResetToProposal(){
	  return getPropertyBoolean(FLD_RESET_TO_PROPOSAL); 
	}
	
	public void setAllowResetToProposal(boolean allowCancel){
	  setPropertyBoolean(FLD_RESET_TO_PROPOSAL, allowCancel); 
	}
	
	public boolean isAllowResetToApproved(){
	  return getPropertyBoolean(FLD_RESET_TO_APPROVED); 
	}
	
	public void setAllowResetToApproved(boolean allowCancel){
	  setPropertyBoolean(FLD_RESET_TO_APPROVED, allowCancel); 
	}
	
	public boolean isAllowModifyCodeDraft(){
	  return getPropertyBoolean(FLD_MODIFY_CODE_DRAFT); 
	}
	
	public void setAllowModifyCodeDraft(boolean allowModifyCodeDraft){
	  setPropertyBoolean(FLD_MODIFY_CODE_DRAFT, allowModifyCodeDraft); 
	}

	public boolean isAllowModifyCodeApproved(){
	  return getPropertyBoolean(FLD_MODIFY_CODE_APPROVED); 
	}
	
	public void setAllowModifyCodeApproved(boolean allowModifyCodeApproved){
	  setPropertyBoolean(FLD_MODIFY_CODE_APPROVED, allowModifyCodeApproved); 
	}
	
	public boolean isAllowUndoSignature(){
	  return getPropertyBoolean(FLD_UNDO_SIGNATURE); 
	}
	
	public void setAllowUndoSignature(boolean allowUndoSignature){
	  setPropertyBoolean(FLD_UNDO_SIGNATURE, allowUndoSignature); 
	}

	public boolean isAllowPrintDraft(){
	  return getPropertyBoolean(FLD_PRINT_DRAFT); 
	}
	
	public void setAllowPrintDraft(boolean allowPrintDraft){
	  setPropertyBoolean(FLD_PRINT_DRAFT, allowPrintDraft); 
	}

	public boolean isAllowPrintApprove(){
	  return getPropertyBoolean(FLD_PRINT_APPROVE); 
	}
	
	public void setAllowPrintApprove(boolean allowPrintApprove){
	  setPropertyBoolean(FLD_PRINT_APPROVE, allowPrintApprove); 
	}
	
	public boolean isModifySignatureStage(){
	  return getPropertyBoolean(FLD_MODIFY_SIGNATRUE_STAGE); 
	}
	
	public void setModifySignatureStage(boolean modifySignatureStage){
	  setPropertyBoolean(FLD_MODIFY_SIGNATRUE_STAGE, modifySignatureStage); 
	}
}
