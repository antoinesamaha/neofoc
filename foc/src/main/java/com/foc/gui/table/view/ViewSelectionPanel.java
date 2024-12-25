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
package com.foc.gui.table.view;

import com.foc.desc.FocObject;
import com.foc.gui.FGCurrentItemPanel;
import com.foc.gui.FGSplitPane;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class ViewSelectionPanel extends FPanel {

	private ViewConfigGuiBrowsePanel browsePanel = null; 
		
	public ViewSelectionPanel(FocList listOfViews, boolean allowCreation){
		setWithScroll(false);
		setMainPanelSising(MAIN_PANEL_FILL_BOTH);

  	browsePanel = new ViewConfigGuiBrowsePanel(listOfViews, allowCreation ? FocObject.DEFAULT_VIEW_ID : ViewConfigGuiBrowsePanel.VIEW_NO_EDIT);

		FGSplitPane split = new FGSplitPane(FGSplitPane.HORIZONTAL_SPLIT, true);
		add(split, 0, 0);
		split.setTopComponent(browsePanel);
  	
		FGCurrentItemPanel currItem = new FGCurrentItemPanel(browsePanel, allowCreation ? ViewConfigGuiDetailsPanel.VIEW_FULL_EDIT : ViewConfigGuiDetailsPanel.VIEW_FULL_NO_EDIT);
		split.setBottomComponent(currItem);
		
		FValidationPanel validPanel = showValidationPanel(true);

		validPanel.addSubject(listOfViews);
		
		if(listOfViews.getSelectionProperty() != null){
			FocObject obj = (FocObject) listOfViews.getSelectionProperty().getObject();
			if(obj != null){
				browsePanel.setSelectedObject(obj);
			}
		}
		
		validPanel.setValidationType(FValidationPanel.VALIDATION_SAVE_CANCEL);
		split.setDividerLocation(150);
	}
	
	@Override
	public void dispose(){
		super.dispose();
		if(browsePanel != null){
			browsePanel.dispose();
			browsePanel = null;
		}
	}
}
