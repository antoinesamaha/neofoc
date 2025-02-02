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

import java.sql.Date;

import com.foc.business.calendar.FCalendar;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFTitle;
import com.foc.business.workflow.implementation.IWorkflow;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.business.workflow.implementation.Workflow;
import com.foc.business.workflow.map.WFStage;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.gui.FPanel;

@SuppressWarnings("serial")
public class WFTransactionWrapper extends FocObject {
	
//	private IWorkflow   iWorkflow  = null;
//	private WFSignature signature  = null;
//	private int         titleIndex = -1;
	
	public WFTransactionWrapper(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	@Override
	public void dispose(){
		super.dispose();
//		iWorkflow = null;
//		signature = null;
	}

	public String getTransactionType(){
		return getPropertyString(WFTransactionWrapperDesc.FLD_TRANSACTION_TYPE);
	}

	public void setTransactionType(String type){
		setPropertyString(WFTransactionWrapperDesc.FLD_TRANSACTION_TYPE, type);
	}

	public String getTransactionCode(){
		return getPropertyString(WFTransactionWrapperDesc.FLD_TRANSACTION_CODE);
	}

	public void setTransactionCode(String code){
		setPropertyString(WFTransactionWrapperDesc.FLD_TRANSACTION_CODE, code);
	}
	
	public Date getTransactionDate(){
		return getPropertyDate(WFTransactionWrapperDesc.FLD_TRANSACTION_DATE);
	}

	public void setTransactionDate(Date date){
		setPropertyDate(WFTransactionWrapperDesc.FLD_TRANSACTION_DATE, date);
	}

	public WFTitle getTitle(){
		return (WFTitle) getPropertyObject(WFTransactionWrapperDesc.FLD_TITLE);
	}

	public void setTitle(WFTitle title){
		setPropertyObject(WFTransactionWrapperDesc.FLD_TITLE, title);
	}

	public void setIsOnBehalfOf(boolean onBehalf){
		setPropertyInteger(WFTransactionWrapperDesc.FLD_ON_BEHALF_OF, onBehalf ? 1 : 0);
	}

	public String getTransactionDescription(){
		return getPropertyString(WFTransactionWrapperDesc.FLD_TRANSACTION_DESCRIPTION);
	}

	public void setTransactionDescription(String description){
		setPropertyString(WFTransactionWrapperDesc.FLD_TRANSACTION_DESCRIPTION, description);
	}
	
	public WFSite getTransactionArea(){
		return (WFSite) getPropertyObject(WFTransactionWrapperDesc.FLD_TRANSACTION_AREA);
	}		

	public void setTransactionArea(WFSite area){
		setPropertyObject(WFTransactionWrapperDesc.FLD_TRANSACTION_AREA, area);
	}		
	
	public WFStage getTransactionStage(){
		return (WFStage) getPropertyObject(WFTransactionWrapperDesc.FLD_TRANSACTION_CURRENT_STAGE);
	}

	public void setTransactionStage(WFStage stage){
		setPropertyObject(WFTransactionWrapperDesc.FLD_TRANSACTION_CURRENT_STAGE, stage);
	}

	private void setOriginalTransaction(FocObject focObj){
		setPropertyObject(WFTransactionWrapperDesc.FLD_ORIGINAL_TRANSACTION, focObj);
	}

	public void setWorkflow(IWorkflow iWorkflow){
//		this.iWorkflow = iWorkflow;
		if(iWorkflow != null){
			FocObject focObj = (FocObject) iWorkflow;
			
			setOriginalTransaction(focObj);
			IWorkflowDesc iWorkflowDesc = (IWorkflowDesc) iWorkflow.iWorkflow_getWorkflow().getFocDesc();
			setTransactionType(iWorkflowDesc.iWorkflow_getTitle());
			setTransactionCode(iWorkflow.iWorkflow_getCode());
			if(!FCalendar.isDateZero(focObj.getDate())) setTransactionDate(focObj.getDate());
			setTransactionDescription(iWorkflow.iWorkflow_getDescription());
			setTransactionArea(iWorkflow.iWorkflow_getWorkflow().getArea());
			setTransactionStage(iWorkflow.iWorkflow_getWorkflow().getCurrentStage());
		}
	}

	public FocObject getFocObject(){
		FocObject focObj = null;
		if(getWorkflow() instanceof FocObject){
			focObj = (FocObject) getWorkflow(); 
		}
		return focObj;
	}
	
	public IWorkflow getWorkflow(){
		return (IWorkflow) getPropertyObject(WFTransactionWrapperDesc.FLD_ORIGINAL_TRANSACTION);
//		return this.iWorkflow;
	}
	
	public FPanel newDetailsPanel(boolean hideValidation){
		FPanel panel = null;
		IWorkflow workflow = getWorkflow();
		if(workflow != null){
			panel = workflow.iWorkflow_newDetailsPanel();
			if(hideValidation){
				panel.showValidationPanel(false);
			}
		}
		return panel;
	}

	/*
	public WFSignature getSignature() {
		return signature;
	}

	public void setSignature(WFSignature signature) {
		this.signature = signature;
	}

	public int getTitleIndex() {
		return titleIndex;
	}

	public void setTitleIndex(int titleIndex) {
		this.titleIndex = titleIndex;
	}
	*/
	
	public void sign(){
		IWorkflow iworkflow = getWorkflow();
		if(iworkflow != null) {
			Workflow  workflow  = iworkflow.iWorkflow_getWorkflow();
			
			WFSignatureNeededResult result = getFocObject() != null ? getFocObject().workflow_NeedsSignatureOfThisUser_AsTitleIndex(null) : null;
			if(result != null){
				workflow.sign(result.getSignature(), result.getTitleIndex(), result.isOnBehalfOf());
			}else{
				workflow.sign();
			}
		}
//		workflow.sign(getSignature(), getTitleIndex());
	}
	
	public void setHide(boolean hide){
		IWorkflow iworkflow = getWorkflow();
		Workflow  workflow  = iworkflow.iWorkflow_getWorkflow();
		workflow.setHide(hide);
//		workflow.sign(getSignature(), getTitleIndex());
	}
	
	public void undoAllSignatures(){
		IWorkflow iworkflow = getWorkflow();
		Workflow  workflow  = iworkflow.iWorkflow_getWorkflow();
		workflow.undoAllSignatures();
	}
	
	public void undoAllSignatures(String comment){
		IWorkflow iworkflow = getWorkflow();
		Workflow  workflow  = iworkflow.iWorkflow_getWorkflow();
		workflow.undoAllSignatures(comment);
	}
}
