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
package com.foc.business.workflow.implementation;

public interface IWorkflowDesc extends ILoggableDesc {
	public WorkflowDesc iWorkflow_getWorkflowDesc();
	public int          iWorkflow_getFieldID_ForSite_1();
	public int          iWorkflow_getFieldID_ForSite_2();
	public String       iWorkflow_getDBTitle();
	public String       iWorkflow_getTitle();
	public String       iWorkflow_getSpecificAdditionalWhere();
	public String       iWorkflow_getCodePrefix();
	public String       iWorkflow_getCodePrefix_ForProforma();
	public int          iWorkflow_getCode_NumberOfDigits();
	public int          iWorkflow_getApprovalMethod();
	//public void         iWorkflow_fillFunctionalStagesArray(WFFunctionalStagesArray functionalArray);
}
