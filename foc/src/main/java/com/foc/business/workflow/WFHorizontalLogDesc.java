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
package com.foc.business.workflow;

import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.business.workflow.implementation.WFLogDesc;
import com.foc.business.workflow.implementation.WorkflowDesc;
import com.foc.business.workflow.map.WFMap;
import com.foc.business.workflow.map.WFStageDesc;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FDateTimeField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class WFHorizontalLogDesc extends FocDesc{

	public final static int INCREMENT_DIFF = 100;
	
	public final static int FLD_TRANSACTION = 1;
	
	public final static int FLD_USER        = INCREMENT_DIFF*1;//100
	public final static int FLD_DATE_TIME   = INCREMENT_DIFF*2;//200
	public final static int FLD_COMMENT     = INCREMENT_DIFF*3;
	public final static int FLD_STAGE       = INCREMENT_DIFF*4;
	public final static int FLD_WF_LOG      = INCREMENT_DIFF*5;
	public final static int FLD_TITLE = INCREMENT_DIFF*6;

	public static final String DB_TABLE_NAME = "WF_HORIZONTAL_LOG";

	public WFHorizontalLogDesc(WorkflowDesc workflowDesc){
    super(WFHorizontalLog.class, FocDesc.NOT_DB_RESIDENT, DB_TABLE_NAME, true);
    addReferenceField();

  	FObjectField objectField = new FObjectField("TRANSACTION", "TRANSACTION", FLD_TRANSACTION, workflowDesc.getFocDesc());
  	objectField.setWithList(false);
  	addField(objectField);

  	int nbrOfExpectedSignatures = 0;
  	
  	FocDesc desc = workflowDesc.getFocDesc();
  	IWorkflowDesc iWorkflowDesc = (IWorkflowDesc) desc;
		WFMap map = WFTransactionConfigDesc.getMap_ForTransaction(iWorkflowDesc.iWorkflow_getDBTitle());
		if(map != null){
			nbrOfExpectedSignatures = map.getSignatureList().size();
		
	    for(int i=0; i<nbrOfExpectedSignatures; i++){
	    	FObjectField fObjectFld = new FObjectField("USER_"+i, "User_"+i, FLD_USER+i, false, FocUser.getFocDesc(), "USER_");
	      fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
	      fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
	      fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
	      fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
	      fObjectFld.setAllwaysLocked(true);
	      addField(fObjectFld);
	    	
	      FDateTimeField dateTimeFld = new FDateTimeField("DATE_TIME_"+i, "Date Time "+i, FLD_DATE_TIME+i, false);
	      dateTimeFld.setTimeRelevant(true);
	      dateTimeFld.setAllwaysLocked(true);
	      addField(dateTimeFld);
	      
	    	FStringField chfld = new FStringField("COMMENT_"+i, "Comment "+i, FLD_COMMENT+i, false, WFLogDesc.LEN_FLD_COMMENT);
	      addField(chfld);
	
	    	chfld = new FStringField("TITLE_"+i, "Title "+i, FLD_TITLE+i, false, WFStageDesc.LEN_NAME_FOC);
	      addField(chfld);
	
	      FObjectField objFld = WFStageDesc.newFieldStage("STAGE_"+i, FLD_STAGE+i);
	      addField(objFld);
	      
	      FIntField intFld = new FIntField("WF_LOG_REF_"+i, "WF_LOG_REF_"+i, FLD_WF_LOG+i, false, 10); 
	      addField(intFld);
	    }
		}
  }
}
