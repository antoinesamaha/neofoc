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
package com.foc.business.workflow.implementation.join;

import com.foc.business.workflow.implementation.ILoggableDesc;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.business.workflow.implementation.WFLogDesc;
import com.foc.business.workflow.implementation.WorkflowDesc;
import com.foc.db.SQLJoin;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.join.FocRequestDesc;
import com.foc.join.FocRequestField;
import com.foc.join.Join;
import com.foc.join.JoinUsingObjectField;
import com.foc.join.TableAlias;

public class WFLogJoinDesc extends FocDesc {

	private FocDesc masterFocDesc = null;
	
	public final static int SHIFT_TRANSACTION = 1000;	
	public final static String ALIAS_LOG    = "L";
	public final static String ALIAS_MASTER = "M";
	
	private FocRequestDesc requestDesc = null;
	private TableAlias masterAlias = null;
	private TableAlias logAlias = null;
	
	private int siteFieldID = -1;
	
	public WFLogJoinDesc(FocDesc masterFocDesc) {
		super(WFLogJoin.class, DB_RESIDENT, masterFocDesc.getStorageName()+"_LOG_JOIN", false, false, false);
		
		this.masterFocDesc = masterFocDesc;
		
		FField fld = addReferenceField();
		fld.setDBResident(false);
		FocRequestDesc reqDesc = getFocRequestDesc();
		reqDesc.fillFocDesc(this);
	}

	public void dispose() {
		super.dispose();
		masterAlias = null;
		logAlias = null;
		requestDesc = null;
	}

	private void addMasterFieldToRequest(int fieldID) {
		if(masterFocDesc.getFieldByID(fieldID) != null) {
			FocRequestField reqFld = new FocRequestField(SHIFT_TRANSACTION + fieldID, masterAlias, fieldID);
			requestDesc.addField(reqFld);
		}
	}
	
	private void addLogFieldToRequest(int fieldID) {
		FocRequestField reqFld = new FocRequestField(fieldID, logAlias, fieldID);
		requestDesc.addField(reqFld);
	}

	public FocRequestDesc getFocRequestDesc() {
		if (requestDesc == null) {
			requestDesc = new FocRequestDesc();
			
			FocDesc logFocDesc = ((ILoggableDesc)masterFocDesc).iWorkflow_getWorkflowDesc().getWFLogDesc();
			
			// Log Table
			logAlias = new TableAlias(ALIAS_LOG, logFocDesc);
			requestDesc.addTableAlias(logAlias);
			
			// Master
			masterAlias = new TableAlias(ALIAS_MASTER, masterFocDesc);
			Join join = new JoinUsingObjectField(logAlias, WFLogDesc.FLD_MASTER);
			join.setType(SQLJoin.JOIN_TYPE_LEFT);
			masterAlias.addJoin(join);
			requestDesc.addTableAlias(masterAlias);
			
			addMasterFieldToRequest(FField.REF_FIELD_ID);
			addMasterFieldToRequest(FField.FLD_CODE);
			addMasterFieldToRequest(FField.FLD_DATE);
			addMasterFieldToRequest(FField.FLD_COMPANY);
			
			if(masterFocDesc instanceof IWorkflowDesc) {
		 		WorkflowDesc workflowDesc = ((IWorkflowDesc) masterFocDesc).iWorkflow_getWorkflowDesc();
	      if(workflowDesc != null){
	      	siteFieldID = workflowDesc.getFieldID_Site_1();
	      }
			}

			if(siteFieldID > 0) {
				addMasterFieldToRequest(siteFieldID);	
			}

			addLogFieldToRequest(WFLogDesc.FLD_CHANGES);
			addLogFieldToRequest(WFLogDesc.FLD_COMMENT);
			addLogFieldToRequest(WFLogDesc.FLD_DATE_TIME);
			addLogFieldToRequest(WFLogDesc.FLD_DOC_HASH);
			addLogFieldToRequest(WFLogDesc.FLD_DOC_VERSION);
			addLogFieldToRequest(WFLogDesc.FLD_DOC_ZIP);
			addLogFieldToRequest(WFLogDesc.FLD_EVT_STATUS);
			addLogFieldToRequest(WFLogDesc.FLD_EVENT_TYPE);
			addLogFieldToRequest(WFLogDesc.FLD_USER);

			requestDesc.fillRequestDescWithJoinFields();
		}
		return requestDesc;
	}

	public FocDesc getMasterFocDesc() {
		return masterFocDesc;
	}
}
