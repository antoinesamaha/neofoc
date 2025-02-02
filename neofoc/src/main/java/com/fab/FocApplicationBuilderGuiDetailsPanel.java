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
package com.fab;

import javax.swing.JSplitPane;

import com.fab.model.table.TableDefinitionGuiBrowsePanel;
import com.foc.desc.FocObject;
import com.foc.gui.FGCurrentItemPanel;
import com.foc.gui.FGSplitPane;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class FocApplicationBuilderGuiDetailsPanel extends FPanel {
	
	public FocApplicationBuilderGuiDetailsPanel(FocObject applicationBuilder, int view){
		setWithScroll(false);
		FGSplitPane splitPan = new FGSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		FListPanel tablesBrowsePanel = new TableDefinitionGuiBrowsePanel(null, FocObject.DEFAULT_VIEW_ID);
		splitPan.setLeftComponent(tablesBrowsePanel);
		tablesBrowsePanel.setWithScroll(false);
		
  	FGCurrentItemPanel currPanel = new FGCurrentItemPanel(tablesBrowsePanel, FocApplicationBuilder.VIEW_NO_EDIT);
  	splitPan.setRightComponent(currPanel);
		setFrameTitle("Tables");
  	setMainPanelSising(FPanel.FILL_BOTH);
		add(splitPan,0,0);
		
		FValidationPanel savePanel = showValidationPanel(true);
    if (savePanel != null) {
      savePanel.addSubject(tablesBrowsePanel.getFocList());	
    }
	}
}
