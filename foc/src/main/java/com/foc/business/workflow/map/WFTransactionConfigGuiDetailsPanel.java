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
package com.foc.business.workflow.map;

import com.foc.desc.FocObject;
import com.foc.gui.FPanel;

@SuppressWarnings("serial")
public class WFTransactionConfigGuiDetailsPanel extends FPanel{
	
	public WFTransactionConfigGuiDetailsPanel(FocObject obj, int viewID){
		WFTransactionConfig assignement = (WFTransactionConfig) obj;
		add(assignement, WFTransactionConfigDesc.FLD_TRANSACTION, 0, 0);
		add(assignement, WFTransactionConfigDesc.FLD_MAP, 0, 1);
		add(assignement, WFTransactionConfigDesc.FLD_DEFAULT_AREA, 0, 2);
		
		//WFAssignFunctionalStageCorrespondanceGuiBrowsePanel browse = new WFAssignFunctionalStageCorrespondanceGuiBrowsePanel(assignement.getFucntionalStageList(), FocObject.DEFAULT_VIEW_ID);
		//add(browse, 0, 3, 2, 1);
	}
}
