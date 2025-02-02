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
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class PrnLayoutGuiDetailsPanel extends FPanel{

	public static final int VIEW_STANDARD  = FocObject.DEFAULT_VIEW_ID;
	
	public PrnLayoutGuiDetailsPanel(FocObject focObj, int view){
		PrnLayout layout = (PrnLayout) focObj;
		
		add(layout, PrnLayoutDesc.FLD_CONTEXT, 0, 0);
		add(layout, PrnLayoutDesc.FLD_FILE_NAME, 0, 1);

		FValidationPanel validPanel = showValidationPanel(true);
		validPanel.addSubject(layout);
	}
}
