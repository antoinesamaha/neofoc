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
package com.fab.model.table;

import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class FabMultiChoiceSetGuiDetailsPanel extends FPanel {

	public FabMultiChoiceSetGuiDetailsPanel(FocObject obj, int viewID){
		FabMultiChoiceSet set = (FabMultiChoiceSet) obj;
		
		add(set, FabMultiChoiceSetDesc.FLD_NAME, 0, 0);

		FabMultipleChoiceGuiBrowsePanel browse = new FabMultipleChoiceGuiBrowsePanel(set.getMultipleChoiceList(), FocObject.DEFAULT_VIEW_ID); 
		add(browse, 0, 1, 2, 1);
		
		FValidationPanel vPanel = showValidationPanel(true);
		vPanel.addSubject(set);
	}
}
