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

import java.awt.Component;

import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class FilterDefinitionGuiDetailsPanel extends FPanel {
	private FilterDefinition filterDefintion = null;
	
	public FilterDefinitionGuiDetailsPanel(FocObject focObject, int viewID){
		super("Filter", FPanel.FILL_BOTH);
		if(focObject != null){
			this.filterDefintion = (FilterDefinition)focObject;
			switch (viewID){
				case FilterDefinition.VIEW_ID_DEFAULT:
					newDetailsPanelDefaultView(viewID);
					break;
				case FilterDefinition.VIEW_ID_FOR_NEW_ITEM:
					newDetailsPanelForNewItem();
					break;
			}
		}
	}
	
	private void newDetailsPanelDefaultView(int viewID){
		FocList filterFieldList = filterDefintion.getFieldDefinitionList();
		FilterFieldDefinitionGuiBrowsePanel fieldDefintionGuiBrowsePanel = new FilterFieldDefinitionGuiBrowsePanel(filterFieldList, viewID);;
		add(fieldDefintionGuiBrowsePanel, 0, 0);
	}
	
	private void newDetailsPanelForNewItem(){
		Component comp = this.filterDefintion.getGuiComponent(FilterDefinitionDesc.FLD_TITLE);
		add("Title", comp, 0, 0);
		
		comp = this.filterDefintion.getGuiComponent(FilterDefinitionDesc.FLD_BASE_FOC_DESC);
		add("Base Table", comp, 1, 0);
		
		setMainPanelSising(FPanel.FILL_NONE);
		FValidationPanel validPanel = showValidationPanel(true);
		validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
	}
	
	public void dispose(){
		super.dispose();
		this.filterDefintion = null;
	}

}
