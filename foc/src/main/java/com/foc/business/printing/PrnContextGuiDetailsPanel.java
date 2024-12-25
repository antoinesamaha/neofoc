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
package com.foc.business.printing;

import com.foc.desc.FocObject;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class PrnContextGuiDetailsPanel extends FPanel{

	public static final int VIEW_STANDARD  = FocObject.DEFAULT_VIEW_ID;
	public static final int VIEW_SELECTION = 2;
	
	public PrnContextGuiDetailsPanel(FocObject focObj, int view){
		super("Workflow Area", FILL_BOTH);
		PrnContext area = (PrnContext) focObj;
		
		if(view == VIEW_SELECTION){
			FGTextField tf = (FGTextField) add(area, PrnContextDesc.FLD_NAME, 0, 0);
			tf.setEditable(false);
			
			tf = (FGTextField) add(area, PrnContextDesc.FLD_DESCRIPTION, 0, 1);
			tf.setEditable(false);
		}else{
			setMainPanelSising(FILL_BOTH);
			
			add(area, PrnContextDesc.FLD_NAME, 0, 0);
			add(area, PrnContextDesc.FLD_DESCRIPTION, 0, 1);
	
			FocList list = area.getLayoutList();
			PrnLayoutGuiBrowsePanel browse = new PrnLayoutGuiBrowsePanel(list, FocObject.DEFAULT_VIEW_ID);
			add(browse, 0, 2, 2, 1);
			
			FValidationPanel validPanel = showValidationPanel(true);
			validPanel.addSubject(area);
		}
	}
}
