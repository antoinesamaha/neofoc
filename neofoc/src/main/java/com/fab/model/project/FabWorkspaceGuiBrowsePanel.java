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
package com.fab.model.project;

import com.fab.FocApplicationBuilder;
import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class FabWorkspaceGuiBrowsePanel extends FListPanel {
	
	public FabWorkspaceGuiBrowsePanel(FocList list, int viewID){
		super("Workspaces", FPanel.FILL_BOTH);
		boolean allowEdit = viewID != FocApplicationBuilder.VIEW_NO_EDIT;
		FocDesc desc = FabWorkspaceDesc.getInstance();

    if(desc != null && list != null) {
    	list.loadIfNotLoadedFromDB();
    	try{
    		setFocList(list);
    	}catch(Exception e){
    		Globals.logException(e);
    	}
      FTableView tableView = getTableView();       
      tableView.addColumn(desc, FabWorkspaceDesc.FLD_NAME, allowEdit);
      tableView.addColumn(desc, FabWorkspaceDesc.FLD_WORKSPACE_PATH, allowEdit);
      construct();
      
      tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
      
      requestFocusOnCurrentItem();
      showEditButton(false);
      showDuplicateButton(false);
      showAddButton(allowEdit);
      showRemoveButton(allowEdit);
      
      FValidationPanel vPanel = showValidationPanel(true);
      vPanel.addSubject(list);
    }
	}
}
