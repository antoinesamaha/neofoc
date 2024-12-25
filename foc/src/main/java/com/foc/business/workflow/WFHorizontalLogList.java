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

import java.sql.Date;

import com.foc.admin.FocUser;
import com.foc.business.workflow.implementation.IWorkflow;
import com.foc.business.workflow.implementation.WFLog;
import com.foc.business.workflow.implementation.WFLogDesc;
import com.foc.business.workflow.implementation.WorkflowDesc;
import com.foc.desc.FocDesc;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class WFHorizontalLogList extends FocList{

	private WorkflowDesc workflowDesc = null;
	
	public WFHorizontalLogList(WorkflowDesc workflowDesc) {
		super(new FocLinkSimple(new WFHorizontalLogDesc(workflowDesc)));
		this.workflowDesc = workflowDesc;
	}
	
	public void dispose(){
		super.dispose();
		workflowDesc = null;
	}
	
	public void fill(){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		if(workflowDesc != null){
			FocDesc focDesc = workflowDesc.getFocDesc();
			if(focDesc != null){
				FocList workflowList = focDesc.getFocList(FocList.LOAD_IF_NEEDED);
				if(workflowList != null){
					for(int i=0; i<workflowList.size(); i++){
						IWorkflow iWorkflow = (IWorkflow) workflowList.getFocObject(i);
						if(iWorkflow != null){
							WFHorizontalLog wfHorizontalLog = (WFHorizontalLog) newEmptyItem();
							add(wfHorizontalLog);
							
							wfHorizontalLog.setTransaction(iWorkflow.iWorkflow_getWorkflow().getFocObject());
							FocList wfLogList = iWorkflow.iWorkflow_getWorkflow().getLogList(true);
							
							int signatureIndex = 0;
							
							if(wfLogList != null){
								for(int j=0; j<wfLogList.size(); j++){
									WFLog wfLog = (WFLog) wfLogList.getFocObject(j);
									if(wfLog != null && !wfLog.getEventUndone() &&
											(wfLog.getEventType() == WFLogDesc.EVENT_SIGNATURE || wfLog.getEventType() == WFLogDesc.EVENT_CANCELLATION)){
										FocUser user     = wfLog.getUser();
										Date    dateTime = wfLog.getDateTime();
										String  comment  = wfLog.getComment();
										String  title    = "";
										if(wfLog.getEventType() == WFLogDesc.EVENT_CANCELLATION){
											title = "Cancelled";
										}else if(wfLog.getTargetStage() != null){
											title = wfLog.getTargetStage().getDescription();
										}
										
										wfHorizontalLog.setUserAt(user, signatureIndex);
										wfHorizontalLog.setDateTimeAt(dateTime, signatureIndex);
										wfHorizontalLog.setCommentAt(comment, signatureIndex);
										wfHorizontalLog.setStageAt(wfLog.getTargetStage(), signatureIndex);
										wfHorizontalLog.setTitleAt(title, signatureIndex);
										wfHorizontalLog.setWFLogRefAt(wfLog.getReference().getInteger(), signatureIndex);
										
										signatureIndex++;
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public WorkflowDesc getWorkflowDesc(){
		return workflowDesc;
	}
}
