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
package com.fab.model.filter;

import com.foc.desc.FocObject;
import com.foc.gui.FGFormulaEditorPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class FilterFieldDefinitionGuiDetailsPanel extends FPanel {
	
	public FilterFieldDefinitionGuiDetailsPanel(FocObject focObject, int viewID){
		super("Filter field", FPanel.FILL_NONE);
		if(focObject != null){
			FilterFieldDefinition fieldFieldDef = (FilterFieldDefinition) focObject;
			FGFormulaEditorPanel comp = (FGFormulaEditorPanel) focObject.getGuiComponent(FilterFieldDefinitionDesc.FLD_CONDITION_PROPERTY_PATH);
			comp.setOriginDesc(fieldFieldDef.getFilterDefinition().getBaseFocDesc());
			add(comp, 0, 0);
			
			FValidationPanel validPanel = showValidationPanel(true);
			validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
		}
	}
}
