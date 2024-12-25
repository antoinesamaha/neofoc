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
package com.foc.desc.parsers.pojo;

import com.foc.Globals;
import com.foc.IFocDescDeclaration;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.business.workflow.implementation.WFLogDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.FocModule;

public class FocDescDeclaration_PojoBased_WFLog implements IFocDescDeclaration{
	private FocDesc transactionFocDesc = null ;
	private FocDesc focDesc            = null ;
	private boolean gettingFocDesc     = false;
	
	public FocDescDeclaration_PojoBased_WFLog(FocDesc transactionFocDesc){
		this.transactionFocDesc = transactionFocDesc;
	}
	
	public void dispose(){
		this.focDesc            = null;
		this.transactionFocDesc = null;
	}
	
	public boolean isGettingFocDesc(){
		return gettingFocDesc;
	}

	public void setGettingFocDesc(boolean getting){
		gettingFocDesc = getting;
	}
	
	public FocDesc getFocDescription() {
		if(focDesc == null && !isGettingFocDesc()){
			setGettingFocDesc(true);
			if(transactionFocDesc != null){
				focDesc = new WFLogDesc((IWorkflowDesc) transactionFocDesc);
				Globals.getApp().putIFocDescDeclaration(focDesc.getName(), this);
			}
			setGettingFocDesc(false);
		}
		return focDesc;
	}
	
	public int getPriority() {
		return IFocDescDeclaration.PRIORITY_SECOND;
	}

	@Override
	public FocModule getFocModule() {
		return transactionFocDesc != null ? transactionFocDesc.getModule() : null;
	}

	@Override
	public String getName() {
		return (getFocDescription() != null) ? getFocDescription().getStorageName() : null;
	}
}
