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
package com.fab.gui.details;

import com.foc.desc.FocObject;
import com.foc.gui.FPanel;

@SuppressWarnings("serial")
public class GuiDetailsGuiDetailsPanel extends FPanel {
	
	private GuiDetails detailsViewDefinition = null;
	
	public GuiDetailsGuiDetailsPanel(FocObject detailsViewDefinition, int viewId){
		super("Gui details", FPanel.FILL_BOTH);
		this.detailsViewDefinition = (GuiDetails)detailsViewDefinition;
		FPanel detailsFieldDefinitionPanel = new GuiDetailsComponentGuiBrowsePanel(this.detailsViewDefinition.getGuiDetailsFieldList(), viewId);
		add(detailsFieldDefinitionPanel, 0, 0);
	}

}
