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
package com.foc.business.workflow.rights;

import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class RightLevelGuiDetailsPanel extends FPanel implements RightLevelConst {

	public static final int VIEW_STANDARD  = FocObject.DEFAULT_VIEW_ID;
	
	public RightLevelGuiDetailsPanel(FocObject focObj, int view){
		RightLevel rightLevel = (RightLevel) focObj;
		
		int y = 0;
		
		add(rightLevel, FField.FLD_NAME, 0, y++);
		add(rightLevel, FLD_INSERT, 1, y++);
		add(rightLevel, FLD_MODIFY_APPROVED, 1, y++);
		add(rightLevel, FLD_DELETE_DRAFT, 1, y++);
		add(rightLevel, FLD_DELETE_APPROVED, 1, y++);
		add(rightLevel, FLD_MODIFY_CODE_DRAFT, 1, y++);
		add(rightLevel, FLD_MODIFY_CODE_APPROVED, 1, y++);
		add(rightLevel, FLD_APPROVE, 1, y++);
		add(rightLevel, FLD_CLOSE, 1, y++);
		add(rightLevel, FLD_CANCEL, 1, y++);
		add(rightLevel, FLD_INSERT, 1, y++);
		add(rightLevel, FLD_UNDO_SIGNATURE, 1, y++);
		
		FValidationPanel validPanel = showValidationPanel(true);
		validPanel.addSubject(rightLevel);
	}
}
