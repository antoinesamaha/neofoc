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

import java.sql.Date;

import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.map.WFSignature;
import com.foc.gui.FPanel;

public interface IWorkflow extends ILoggable {
	public Workflow     iWorkflow_getWorkflow();
	public WFSite       iWorkflow_getComputedSite();
	public String       iWorkflow_getCode();
	public Date         iWorkflow_getDate();	
	public String       iWorkflow_getDescription();
	public FPanel       iWorkflow_newDetailsPanel();
	public boolean      iWorkflow_allowSignature(WFSignature signature);
}
