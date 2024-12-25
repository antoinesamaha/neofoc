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

import com.foc.business.workflow.map.WFStage;
import com.foc.business.workflow.map.WFTransactionConfig;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.property.FMultipleChoiceString;

@SuppressWarnings("serial")
public class WFFieldLockStage extends FocObject{

	public WFFieldLockStage(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	public WFTransactionConfig getWFTransactionConfig(){
		return (WFTransactionConfig) getPropertyObject(WFFieldLockStageDesc.FLD_WF_TRANSACTION_CONFIG);
	}
	
	public void setWFTransactionConfig(WFTransactionConfig wfTransactionConfig){
		setPropertyObject(WFFieldLockStageDesc.FLD_WF_TRANSACTION_CONFIG, wfTransactionConfig);
	}
	
	public String getFieldName(){
		return (String) getPropertyString(WFFieldLockStageDesc.FLD_FIELD_NAME);
	}
	
	public void setFieldName(String fieldName){
		setPropertyString(WFFieldLockStageDesc.FLD_FIELD_NAME, fieldName);
	}
	
	public WFStage getWFStage(){
		return (WFStage) getPropertyObject(WFFieldLockStageDesc.FLD_WF_LOCK_STAGE);
	}
	
	public void setWFStage(WFStage wfStage){
		setPropertyObject(WFFieldLockStageDesc.FLD_WF_LOCK_STAGE, wfStage);
	}
	
	public void fillChoicesForStagesProperty(){
		FMultipleChoiceString property = (FMultipleChoiceString) getFocProperty(WFFieldLockStageDesc.FLD_FIELD_NAME);
		if(property != null){
			property.resetLocalSourceList();
			WFTransactionConfig transactionConfig = getWFTransactionConfig();
			FocDesc       desc              = (FocDesc) transactionConfig.getWorkflowDesc();
			if(desc != null){
				for(int i=0; i<desc.getFieldsSize(); i++){
					FField fld = desc.getFieldAt(i);
					property.addLocalChoice(fld.getName());	
				}
			}
		}
	}
}
