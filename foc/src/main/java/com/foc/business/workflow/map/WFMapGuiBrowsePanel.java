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
package com.foc.business.workflow.map;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class WFMapGuiBrowsePanel extends FListPanel {
	
	public static final int VIEW_SELECTION = 1; 
	
	public WFMapGuiBrowsePanel(FocList focList, int viewID){
		super("Workflow Map List", FPanel.FILL_VERTICAL);
		FocDesc focDesc = WFMapDesc.getInstance();
		if(focDesc != null){
			if(focList == null){
				focList = WFMapDesc.getList(FocList.FORCE_RELOAD);
			}
			if(focList != null){
				try {
					setFocList(focList);
				} catch (Exception e) {
					Globals.logException(e);
				}
				FTableView tableView = getTableView();

				if(viewID == VIEW_SELECTION){
					tableView.addSelectionColumn();
				}
				tableView.addColumn(focDesc, WFMapDesc.FLD_NAME, false);
				tableView.addColumn(focDesc, WFMapDesc.FLD_DESCRIPTION, false);
				/*
				tableView.addColumn(focDesc, WFMapDesc.FLD_CREATION_TITLE, false);
				tableView.addColumn(focDesc, WFMapDesc.FLD_MODIFICATION_TITLE, false);
				tableView.addColumn(focDesc, WFMapDesc.FLD_CANCELATION_TITLE, false);
				*/
				
				construct();
				tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
				
				FValidationPanel validPanel = showValidationPanel(true);
				if(validPanel != null){
					if(viewID == VIEW_SELECTION){
						validPanel.setValidationButtonLabel("Ok");
					}else{
						validPanel.addSubject(focList);
					}
				}
				
				showAddButton(viewID != VIEW_SELECTION);
				showRemoveButton(viewID != VIEW_SELECTION);
				showEditButton(viewID != VIEW_SELECTION);
			}
		}
	}
}
