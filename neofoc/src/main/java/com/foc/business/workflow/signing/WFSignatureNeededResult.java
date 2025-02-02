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

import com.foc.business.workflow.WFTitle;
import com.foc.business.workflow.map.WFSignature;

public class WFSignatureNeededResult {
	private int         titleIndex = -1;
	private boolean     onBehalfOf = false;
	private WFSignature signature  = null;
	
	public WFSignatureNeededResult(int titleIndex, boolean onBehalfOf){
		this.titleIndex = titleIndex;
		this.onBehalfOf = onBehalfOf; 
	}
	
	public int getTitleIndex(){
		return titleIndex;
	}
	
	public boolean isOnBehalfOf(){
		return onBehalfOf;
	}

	public void setTitleIndex(int titleIndex) {
		this.titleIndex = titleIndex;
	}

	public void setOnBehalfOf(boolean onBehalfOf) {
		this.onBehalfOf = onBehalfOf;
	}
	
	public WFSignature getSignature(){
		return signature;
	}
	
	public void setSignature(WFSignature signature){
		this.signature = signature;
	}
	
	public WFTitle getTitle(){
		return (getSignature() != null && getTitleIndex() >= 0) ? getSignature().getTitle(getTitleIndex()) : null;
	}
}
