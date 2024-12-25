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

import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class FGWorkflowGuiPanel extends FPanel {

	private FGWorkflowButtonPanel signatureButton = null;
	private FocObject        focObject       = null;
	
	public FGWorkflowGuiPanel(FocObject object, String title, int view){
		super(title, view);
		focObject = object;
	}
	
	public void dispose(){
		super.dispose();
		focObject = null;
	}
	
	public FPanel getMainPanel(){
		return this;
	}

	@Override
  public FValidationPanel showValidationPanel(boolean show){
		FValidationPanel vPanel = super.showValidationPanel(show, true);
		if(focObject != null && !focObject.isCreated()){
			signatureButton = new FGWorkflowButtonPanel(focObject);
			vPanel.addButton(signatureButton);
		}
    return vPanel;
  }
}
